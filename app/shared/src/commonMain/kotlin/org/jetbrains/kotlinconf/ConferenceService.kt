package org.jetbrains.kotlinconf

import com.mmk.kmpnotifier.notification.NotifierManager
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.SingleIn
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.datetime.LocalDateTime
import org.jetbrains.kotlinconf.LocalNotificationId.Type
import org.jetbrains.kotlinconf.di.YearGraph
import org.jetbrains.kotlinconf.storage.ApplicationStorage
import org.jetbrains.kotlinconf.storage.YearlyStorage
import org.jetbrains.kotlinconf.utils.Logger
import org.jetbrains.kotlinconf.utils.tagged
import kotlin.time.Duration.Companion.minutes
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalCoroutinesApi::class)
@Inject
@SingleIn(AppScope::class)
class ConferenceService(
    private val appClient: APIClient,
    private val appStorage: ApplicationStorage,
    private val timeProvider: TimeProvider,
    private val yearGraphFactory: YearGraph.Factory,
    private val localNotificationService: LocalNotificationService,
    private val scope: CoroutineScope,
    logger: Logger,
) {
    companion object {
        private const val LOG_TAG = "ConferenceService"
    }

    private val taggedLogger = logger.tagged(LOG_TAG)

    private val yearGraphs: MutableStateFlow<Map<Int, YearGraph>> = MutableStateFlow(emptyMap())

    private val currentYearGraph: StateFlow<YearGraph?> = combine(
        appStorage.getConfig(),
        yearGraphs,
    ) { config, years ->
        years[config?.currentYear]
    }.stateIn(scope, SharingStarted.Eagerly, null)

    private val currentYearlyStorage: Flow<YearlyStorage> = combine(
        appStorage.getConfig(),
        yearGraphs,
    ) { config, years ->
        years[config?.currentYear]?.yearlyStorage
    }.filterNotNull()

    init {
        appStorage.ensureCurrentVersion()

        scope.launch {
            val newConfig = appClient.getConfig()
            if (newConfig != null) {
                taggedLogger.log { "New config received from server: $newConfig" }
                appStorage.setConfig(newConfig)
                taggedLogger.log { "Stored new config locally" }
            }

            appStorage.getConfig()
                .distinctUntilChanged()
                .collect { config ->
                    taggedLogger.log { "Loaded local config: $config" }

                    yearGraphs.update {
                        // TODO clean up old graphs if necessary here

                        config?.supportedYears?.associateWith { year -> yearGraphFactory.create(year) } ?: emptyMap()
                    }

                    taggedLogger.log { "Recreated year graphs: ${yearGraphs.value}" }

                    taggedLogger.log { "Loading conference data for year ${config?.currentYear}" }
                    loadConferenceData()

                    syncVotes()

                    taggedLogger.log { "ConferenceService init successful" }
                }
        }

        scope.launch {
            timeProvider.run()
        }

        scope.launch {
            currentYearlyStorage.flatMapLatest { it.getNotificationSettings() }
                .filterNotNull()
                .collect { settings ->
                    taggedLogger.log { "Synchronizing settings to Firebase topics: $settings" }
                    val notifier = NotifierManager.getPushNotifier()
                    listOf(
                        settings.scheduleUpdates to PushNotificationConstants.TOPIC_SCHEDULE_UPDATES,
                    ).forEach { (enabled, topic) ->
                        if (enabled) notifier.subscribeToTopic(topic)
                        else notifier.unSubscribeFromTopic(topic)
                    }
                }
        }
    }


    val agenda: StateFlow<List<Day>> =
        combine(
            currentYearlyStorage.flatMapLatest { it.getConferenceCache() },
            currentYearlyStorage.flatMapLatest { it.getFavorites() },
            timeProvider.time,
            currentYearlyStorage.flatMapLatest { it.getVotes() },
        ) { conference, favorites, time, votes ->
            conference?.buildAgenda(favorites, votes, time) ?: emptyList()
        }.stateIn(scope, SharingStarted.Eagerly, emptyList())

    private val sessionCards: StateFlow<List<SessionCardView>> =
        agenda.map {
            it.flatMap { it.timeSlots }.flatMap { it.sessions }
        }.stateIn(scope, SharingStarted.Eagerly, emptyList())

    val userId: StateFlow<String?> = currentYearlyStorage.flatMapLatest { it.getUserId() }
        .stateIn(scope, SharingStarted.Eagerly, null)

    val speakers: StateFlow<List<Speaker>> =
        currentYearlyStorage.flatMapLatest { it.getConferenceCache() }
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

    val conferenceInfo: StateFlow<ConferenceInfo?> =
        currentYearlyStorage.flatMapLatest { it.getConferenceInfoCache() }
            .stateIn(scope, SharingStarted.Eagerly, null)

    fun getTheme(): Flow<Theme> = appStorage.getTheme()

    fun setTheme(theme: Theme) {
        scope.launch { appStorage.setTheme(theme) }
    }

    suspend fun loadConferenceData() {
        val currentYearGraph = currentYearGraph.value ?: return
        val storage = currentYearGraph.yearlyStorage
        val client = currentYearGraph.yearlyAPIClient

        // Load conference info (partners, days, about blocks, tags)
        client.downloadConferenceInfo()?.let { storage.setConferenceInfoCache(it) }

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
        return appStorage.isOnboardingComplete()
    }

    suspend fun completeOnboarding() {
        appStorage.setOnboardingComplete(true)
    }

    suspend fun acceptPrivacyNotice(): Boolean {
        val currentYearGraph = currentYearGraph.value ?: return false
        val storage = currentYearGraph.yearlyStorage

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
        val currentYearGraph = currentYearGraph.value ?: return false
        val storage = currentYearGraph.yearlyStorage
        val client = currentYearGraph.yearlyAPIClient

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
        val currentYearGraph = currentYearGraph.value ?: return false
        val storage = currentYearGraph.yearlyStorage

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
    suspend fun requestNotificationPermissions(): Boolean =
        localNotificationService.requestPermission()
            .also { taggedLogger.log { "Notification permissions granted: $it" } }

    fun getNotificationSettings(): Flow<NotificationSettings> =
        currentYearlyStorage.flatMapLatest { it.getNotificationSettings() }
            .map {
                // No stored value yet, create settings with everything enabled by default
                it ?: NotificationSettings(
                    sessionReminders = true,
                    scheduleUpdates = true,
                )
            }

    suspend fun setNotificationSettings(settings: NotificationSettings) {
        val currentYearGraph = currentYearGraph.value ?: return
        val storage = currentYearGraph.yearlyStorage

        storage.setNotificationSettings(settings)
    }

    suspend fun canVote(): Boolean {
        val currentYearGraph = currentYearGraph.value ?: return false
        val storage = currentYearGraph.yearlyStorage
        return storage.getUserId().first() != null
    }

    suspend fun vote(sessionId: SessionId, rating: Score?): Boolean {
        if (!checkUserId()) return false

        val currentYearGraph = currentYearGraph.value ?: return false
        val storage = currentYearGraph.yearlyStorage
        val client = currentYearGraph.yearlyAPIClient

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
        val client = currentYearGraph.value?.yearlyAPIClient ?: return false
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


    suspend fun setFavorite(sessionId: SessionId, favorite: Boolean) {
        withContext(NonCancellable) {
            val currentYearGraph = currentYearGraph.value ?: return@withContext
            val storage = currentYearGraph.yearlyStorage

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


    private suspend fun syncVotes() {
        if (!checkUserId()) {
            taggedLogger.log { "Can't sync votes, missing userId" }
            return
        }

        val currentYearGraph = currentYearGraph.value ?: return
        val storage = currentYearGraph.yearlyStorage
        val client = currentYearGraph.yearlyAPIClient

        taggedLogger.log { "Synchronizing votes for user ${client.userId.value}" }

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

        taggedLogger.log { "Synchronized votes successfully" }
    }
}
