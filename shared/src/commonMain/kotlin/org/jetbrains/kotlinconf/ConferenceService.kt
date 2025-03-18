package org.jetbrains.kotlinconf

import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDateTime
import org.jetbrains.kotlinconf.storage.ApplicationStorage
import org.jetbrains.kotlinconf.utils.DateTimeFormatting
import org.jetbrains.kotlinconf.utils.Logger
import kotlin.time.Duration.Companion.minutes

class ConferenceService(
    private val client: APIClient,
    private val timeProvider: TimeProvider,
    private val storage: ApplicationStorage,
    private val notificationService: NotificationService,
    private val logger: Logger,
) {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

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

            // Load user votes
            votes.value = client.myVotes()
        }

        scope.launch {
            timeProvider.run()
        }
    }

    companion object {
        private const val LOG_TAG = "ConferenceService"
    }

    private val votes = MutableStateFlow(emptyList<VoteInfo>())

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
            votes,
        ) { conference, favorites, time, votes ->
            conference?.buildAgenda(favorites, votes, time) ?: emptyList()
        }.stateIn(scope, SharingStarted.Eagerly, emptyList())

    private val sessionCards: StateFlow<List<SessionCardView>> =
        agenda.map {
            it.flatMap { it.timeSlots }.flatMap { it.sessions }
        }.stateIn(scope, SharingStarted.Eagerly, emptyList())

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
            val oldIds = oldData.sessions.map { it.id }
            val newIds = newData.sessions.map { it.id }
            val removedIds = oldIds - newIds
            removedIds.forEach { sessionId ->
                cancelNotifications(sessionId)
            }
        }
    }

    /**
     * Returns true if app is launched first time.
     */
    fun isOnboardingComplete(): Flow<Boolean> {
        return storage.isOnboardingComplete()
    }

    suspend fun completeOnboarding() {
        storage.setOnboardingComplete(true)
    }

    suspend fun acceptPrivacyPolicy(): Boolean {
        val userId = storage.getUserId().first()
        if (userId != null) return true

        return registerUser(generateUserId())
    }

    fun acceptPrivacyPolicyAsync() {
        scope.launch {
            acceptPrivacyPolicy()
        }
    }

    private suspend fun registerUser(newUserId: String): Boolean {
        val success = client.sign(newUserId)
        if (success) {
            storage.setUserId(newUserId)
            storage.setPendingUserId(null)
            logger.log(LOG_TAG) { "Signed up successfully with $newUserId" }
        } else {
            storage.setPendingUserId(newUserId)
            logger.log(LOG_TAG) { "Sign up failed, stored pending user ID $newUserId" }
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
    suspend fun requestNotificationPermissions(): Boolean = notificationService.requestPermission().also {
        logger.log(LOG_TAG) { "Notification permissions granted: $it" }
    }

    fun getNotificationSettings(): Flow<NotificationSettings> = storage.getNotificationSettings()

    suspend fun setNotificationSettings(settings: NotificationSettings) {
        storage.setNotificationSettings(settings)
    }

    fun canVote(): Boolean {
        return client.userId != null
    }

    /**
     * Vote for session.
     */
    suspend fun vote(sessionId: SessionId, rating: Score?): Boolean {
        if (!checkUserId()) return false
        if (!client.vote(sessionId, rating)) return false

        val allVotes = votes.value
            .filter { it.sessionId != sessionId }
            .toMutableList()

        allVotes.add(VoteInfo(sessionId, rating))
        votes.value = allVotes
        return true
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

    /**
     * Mark session as favorite.
     */
    fun setFavorite(sessionId: SessionId, favorite: Boolean) {
        scope.launch {
            val favorites = storage.getFavorites().first().toMutableSet()
            if (favorite) favorites.add(sessionId) else favorites.remove(sessionId)
            storage.setFavorites(favorites)

            if (favorite) {
                val session = sessionByIdFlow(sessionId).first()
                if (session != null) {
                    scheduleNotification(session)
                }
            } else {
                cancelNotifications(sessionId)
            }
        }
    }

    private fun scheduleNotification(session: SessionCardView) {
        val start = session.startsAt
        val end = session.endsAt
        val now = timeProvider.now()

        val reminderTime = start - 5.minutes

        // Notifications for session start
        val startsLater = now < reminderTime
        val startsSoon = now in reminderTime..<start
        val isLive = now in start..<end
        when {
            startsLater -> notificationService.post(
                time = reminderTime,
                notificationId = session.id.toNotificationId(NotificationType.Start),
                title = session.title,
                message = "Starts in 5 minutes",
            )

            startsSoon -> notificationService.post(
                notificationId = session.id.toNotificationId(NotificationType.Start),
                title = session.title,
                message = "The session is about to start",
            )

            isLive -> notificationService.post(
                notificationId = session.id.toNotificationId(NotificationType.Start),
                title = session.title,
                message = "Hurry up, the session has already started!",
            )
        }

        // Notifications for session end
        if (end > now) {
            notificationService.post(
                time = end,
                notificationId = session.id.toNotificationId(NotificationType.End),
                title = "${session.title} finished",
                message = "How was the talk?",
            )
        }
    }

    private fun cancelNotifications(sessionId: SessionId) {
        NotificationType.entries.forEach { notificationType ->
            notificationService.cancel(sessionId.toNotificationId(notificationType))
        }
    }

    private enum class NotificationType { Start, End }

    private fun SessionId.toNotificationId(type: NotificationType) = buildString {
        append(id)
        append("-")
        append(
            when (type) {
                NotificationType.Start -> "start"
                NotificationType.End -> "end"
            }
        )
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
}
