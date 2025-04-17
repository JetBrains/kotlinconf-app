package org.jetbrains.kotlinconf

import com.mmk.kmpnotifier.notification.NotifierManager
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDateTime
import org.jetbrains.kotlinconf.LocalNotificationId.Type
import org.jetbrains.kotlinconf.storage.ApplicationStorage
import org.jetbrains.kotlinconf.utils.DateTimeFormatting
import org.jetbrains.kotlinconf.utils.Logger
import org.jetbrains.kotlinconf.utils.tagged
import kotlin.time.Duration.Companion.minutes
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

class ConferenceService(
    private val client: APIClient,
    private val timeProvider: TimeProvider,
    private val storage: ApplicationStorage,
    private val localNotificationService: LocalNotificationService,
    private val scope: CoroutineScope,
    logger: Logger,
) {
    companion object {
        private const val LOG_TAG = "ConferenceService"
    }

    private val taggedLogger = logger.tagged(LOG_TAG)

    init {
        storage.ensureCurrentVersion()

        val userIdLoaded = CompletableDeferred<Unit>()

        scope.launch {
            storage.getUserId().collect {
                userIdLoaded.complete(Unit)
                client.userId = it
            }
        }

        scope.launch {
            // Download fresh conference data
            loadConferenceData()

            // Load fresh news items
            loadNews()

            // Wait for user ID to be loaded
            userIdLoaded.await()

            // Synchronize user votes
            syncVotes()
        }

        scope.launch {
            timeProvider.run()
        }

        scope.launch {
            storage.getNotificationSettings()
                .filterNotNull()
                .collect { settings ->
                    taggedLogger.log { "Synchronizing settings to Firebase topics: $settings" }
                    val notifier = NotifierManager.getPushNotifier()
                    listOf(
                        settings.scheduleUpdates to PushNotificationConstants.TOPIC_SCHEDULE_UPDATES,
                        settings.kotlinConfNews to PushNotificationConstants.TOPIC_KOTLINCONF_NEWS,
                        settings.jetBrainsNews to PushNotificationConstants.TOPIC_JETBRAINS_NEWS,
                    ).forEach { (enabled, topic) ->
                        if (enabled) notifier.subscribeToTopic(topic)
                        else notifier.unSubscribeFromTopic(topic)
                    }
                }
        }
    }

    val news: Flow<List<NewsDisplayItem>> = storage.getNews()
        .map { newsItems ->
            val now = timeProvider.now()
            newsItems.map { newsItem -> mapNewsItemToDisplayItem(newsItem, now) }
        }
        .flowOn(Dispatchers.Default)

    val agenda: StateFlow<List<Day>> =
        combine(
            storage.getConferenceCache(),
            storage.getFavorites(),
            timeProvider.time,
            storage.getVotes(),
        ) { conference, favorites, time, votes ->
            conference?.buildAgenda(favorites, votes, time) ?: emptyList()
        }.stateIn(scope, SharingStarted.Eagerly, emptyList())

    private val sessionCards: StateFlow<List<SessionCardView>> =
        agenda.map {
            it.flatMap { it.timeSlots }.flatMap { it.sessions }
        }.stateIn(scope, SharingStarted.Eagerly, emptyList())

    val userId: StateFlow<String?> = storage.getUserId()
        .stateIn(scope, SharingStarted.Eagerly, null)

    val speakers: StateFlow<List<Speaker>> = storage.getConferenceCache()
        .map {
            (it?.speakers ?: emptyList())
                .filter { speaker -> speaker.photoUrl.isNotBlank() }
        }
        .stateIn(scope, SharingStarted.Eagerly, emptyList())

    private val speakersById: StateFlow<Map<SpeakerId, Speaker>> = speakers
        .map { speakers ->
            speakers.associateBy { it.id }
        }
        .stateIn(scope, SharingStarted.Eagerly, emptyMap())

    fun getTheme(): Flow<Theme> = storage.getTheme()

    fun setTheme(theme: Theme) {
        scope.launch { storage.setTheme(theme) }
    }

    suspend fun loadConferenceData() {
        val newData = client.downloadConferenceData() ?: return
        val oldData = storage.getConferenceCache().first()

        storage.setConferenceCache(newData)

        if (oldData != null) {
            val oldSessions = oldData.sessions.associateBy { it.id }
            val newSessions = newData.sessions.associateBy { it.id }

            // Cancel notifications for removed sessions
            val removedIds = oldSessions.keys - newSessions.keys
            removedIds.forEach { sessionId ->
                cancelNotifications(sessionId)
            }

            // Remove removed sessions from favorites
            val favorites = storage.getFavorites().first().toMutableSet()
            favorites.removeAll { it in removedIds }
            storage.setFavorites(favorites)

            // Check if any favorite sessions were rescheduled
            favorites.forEach { sessionId ->
                val newSession = newSessions[sessionId] ?: return@forEach
                val oldSession = oldSessions[sessionId] ?: return@forEach
                if (oldSession.startsAt != newSession.startsAt || oldSession.endsAt != newSession.endsAt) {
                    cancelNotifications(sessionId)
                    scheduleNotification(
                        start = newSession.startsAt,
                        end = newSession.endsAt,
                        sessionId = newSession.id,
                        title = newSession.title,
                    )
                }
            }
        }
    }

    fun isOnboardingComplete(): Flow<Boolean> {
        return storage.isOnboardingComplete()
    }

    suspend fun completeOnboarding() {
        storage.setOnboardingComplete(true)
    }

    suspend fun acceptPrivacyNotice(): Boolean {
        val userId = storage.getUserId().first()
        if (userId != null) return true

        @OptIn(ExperimentalUuidApi::class)
        val generatedUserId = "${getPlatformId()}-${Uuid.random()}"
        return registerUser(generatedUserId)
    }

    fun acceptPrivacyNoticeAsync() {
        scope.launch {
            acceptPrivacyNotice()
        }
    }

    private suspend fun registerUser(newUserId: String): Boolean {
        val success = client.sign(newUserId)
        if (success) {
            storage.setUserId(newUserId)
            storage.setPendingUserId(null)
            taggedLogger.log { "Signed up successfully with $newUserId" }
        } else {
            storage.setPendingUserId(newUserId)
            taggedLogger.log { "Sign up failed, stored pending user ID $newUserId" }
        }
        return success
    }

    /**
     * Ensure that we have a valid user ID.
     *
     * If a user ID is not yet set, but there's a pending user ID,
     * this method will attempt to send that to the server.
     *
     * @return true if there's a valid user ID set.
     */
    private suspend fun checkUserId(): Boolean {
        val userId = storage.getUserId().first()
        if (userId != null) return true

        val pendingUserId = storage.getPendingUserId().first()
        if (pendingUserId == null) return false

        return registerUser(pendingUserId)
    }

    /**
     * Request permissions to send notifications.
     * @return true if permission was granted, false otherwise
     */
    suspend fun requestNotificationPermissions(): Boolean = localNotificationService.requestPermission()
        .also { taggedLogger.log { "Notification permissions granted: $it" } }

    fun getNotificationSettings(): Flow<NotificationSettings> = storage.getNotificationSettings()
        .map {
            // No stored value yet, create settings with everything enabled by default
            it ?: NotificationSettings(
                sessionReminders = true,
                scheduleUpdates = true,
                kotlinConfNews = true,
                jetBrainsNews = true,
            )
        }

    suspend fun setNotificationSettings(settings: NotificationSettings) {
        storage.setNotificationSettings(settings)
    }

    fun canVote(): Boolean {
        return client.userId != null
    }

    suspend fun vote(sessionId: SessionId, rating: Score?): Boolean {
        if (!checkUserId()) return false

        val localVotes = storage.getVotes().first().toMutableList()
        val existingIndex = localVotes.indexOfFirst { it.sessionId == sessionId }
        if (existingIndex != -1) {
            localVotes[existingIndex] = VoteInfo(sessionId, rating)
        } else {
            localVotes += VoteInfo(sessionId, rating)
        }
        storage.setVotes(localVotes)

        return client.vote(sessionId, rating)
    }

    suspend fun sendFeedback(sessionId: SessionId, feedbackValue: String): Boolean {
        if (!checkUserId()) return false
        return client.sendFeedback(sessionId, feedbackValue)
    }

    fun speakerById(speakerId: SpeakerId): Speaker? = speakersById.value[speakerId]

    fun speakerByIdFlow(speakerId: SpeakerId): Flow<Speaker?> =
        speakersById.map { it[speakerId] }

    fun sessionByIdFlow(sessionId: SessionId): Flow<SessionCardView?> =
        sessionCards.map { sessions -> sessions.find { it.id == sessionId } }

    fun speakersBySessionId(sessionId: SessionId): Flow<List<Speaker>> =
        sessionByIdFlow(sessionId).map { session ->
            session?.speakerIds?.mapNotNull { speakerId -> speakerById(speakerId) } ?: emptyList()
        }

    fun sessionsForSpeakerFlow(id: SpeakerId): Flow<List<SessionCardView>> =
        sessionCards.map { sessions -> sessions.filter { id in it.speakerIds } }

    fun newsById(newsId: String): Flow<NewsDisplayItem?> =
        news.map { allNews ->
            allNews.find { it.id == newsId }
        }

    fun setFavorite(sessionId: SessionId, favorite: Boolean) {
        scope.launch {
            val favorites = storage.getFavorites().first().toMutableSet()
            if (favorite) favorites.add(sessionId) else favorites.remove(sessionId)
            storage.setFavorites(favorites)

            if (favorite) {
                val session = sessionByIdFlow(sessionId).first()
                if (session != null) {
                    scheduleNotification(
                        start = session.startsAt,
                        end = session.endsAt,
                        sessionId = session.id,
                        title = session.title,
                    )
                }
            } else {
                cancelNotifications(sessionId)
            }
        }
    }

    private fun scheduleNotification(
        start: LocalDateTime,
        end: LocalDateTime,
        sessionId: SessionId,
        title: String,
    ) {
        val now = timeProvider.now()

        val reminderTime = start - 5.minutes

        // Notifications for session start
        val startsLater = now < reminderTime
        val startsSoon = now in reminderTime..<start
        val isLive = now in start..<end
        when {
            startsLater -> localNotificationService.post(
                time = reminderTime,
                localNotificationId = LocalNotificationId(Type.SessionStart, sessionId.id),
                title = title,
                message = "Starts in 5 minutes",
            )

            startsSoon -> localNotificationService.post(
                localNotificationId = LocalNotificationId(Type.SessionStart, sessionId.id),
                title = title,
                message = "The session is about to start",
            )

            isLive -> localNotificationService.post(
                localNotificationId = LocalNotificationId(Type.SessionStart, sessionId.id),
                title = title,
                message = "Hurry up, the session has already started!",
            )
        }

        // Notifications for session end
        if (end > now) {
            localNotificationService.post(
                time = end,
                localNotificationId = LocalNotificationId(Type.SessionEnd, sessionId.id),
                title = "$title finished",
                message = "How was the talk?",
            )
        }
    }

    private fun cancelNotifications(sessionId: SessionId) {
        localNotificationService.cancel(LocalNotificationId(Type.SessionStart, sessionId.id))
        localNotificationService.cancel(LocalNotificationId(Type.SessionEnd, sessionId.id))
    }

    private fun mapNewsItemToDisplayItem(
        item: NewsItem,
        now: LocalDateTime,
    ): NewsDisplayItem {
        return NewsDisplayItem(
            id = item.id,
            photoUrl = item.photoUrl,
            date = item.publicationDate.toNewsDisplayTime(now),
            title = item.title,
            content = item.content,
        )
    }

    private fun LocalDateTime.toNewsDisplayTime(now: LocalDateTime): String {
        val isToday = year == now.year && dayOfYear == now.dayOfYear
        return when {
            isToday -> DateTimeFormatting.time(this)
            year == now.year -> DateTimeFormatting.date(this)
            else -> DateTimeFormatting.dateWithYear(this)
        }
    }

    suspend fun loadNews() {
        val news = client.getNews() ?: return
        storage.setNews(news)
    }

    private suspend fun syncVotes() {
        if (!checkUserId()) return

        val apiVotes = client.myVotes().associateBy { it.sessionId }
        val localVotes = storage.getVotes().first().associateBy { it.sessionId }
        val mergedVotes = (apiVotes.keys union localVotes.keys)
            .mapNotNull { sessionId ->
                val localVote = localVotes[sessionId]
                val apiVote = apiVotes[sessionId]

                if (localVote?.score != apiVote?.score) {
                    client.vote(sessionId, localVote?.score)
                }

                localVote ?: apiVote
            }
        storage.setVotes(mergedVotes)
    }
}
