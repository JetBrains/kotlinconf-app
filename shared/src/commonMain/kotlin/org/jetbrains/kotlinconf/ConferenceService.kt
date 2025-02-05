package org.jetbrains.kotlinconf

import io.ktor.util.date.GMTDate
import io.ktor.util.date.plus
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import org.jetbrains.kotlinconf.storage.ApplicationStorage
import org.jetbrains.kotlinconf.storage.MultiplatformSettingsStorage

val UNKNOWN_SESSION_CARD: SessionCardView = SessionCardView(
    id = SessionId("unknown"),
    title = "unknown",
    speakerLine = "unknown",
    locationLine = "unknown",
    startsAt = GMTDate.START,
    endsAt = GMTDate.START,
    speakerIds = emptyList(),
    isFinished = false,
    isFavorite = false,
    description = "unknown",
    vote = null,
    tags = emptyList()
)

val UNKNOWN_SPEAKER: Speaker = Speaker(
    SpeakerId("unknown"), "unknown", "unknown", "unknown", ""
)

class ConferenceService(
    val context: ApplicationContext,
    val endpoint: String,
) {
    private val storage: ApplicationStorage = MultiplatformSettingsStorage(context)

    private val client: APIClient by lazy {
        APIClient(endpoint)
    }

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    private var serverTime = GMTDate()
    private var requestTime = GMTDate()
    private val notificationManager = NotificationManager(context)

    private val votes = MutableStateFlow(emptyList<VoteInfo>())

    private val _time = MutableStateFlow(GMTDate())
    val time: StateFlow<GMTDate> = _time.asStateFlow()

    val agenda: StateFlow<Agenda> by lazy {
        combine(
            storage.getConferenceCache(),
            storage.getFavorites(),
            time,
            votes,
        ) { conference, favorites, time, votes ->
            conference.buildAgenda(favorites, votes, time)
        }.stateIn(scope, SharingStarted.Eagerly, Agenda())
    }

    val sessionCards: StateFlow<List<SessionCardView>> by lazy {
        agenda.map {
            it.days
                .flatMap { it.timeSlots }
                .flatMap { it.sessions }
        }.stateIn(scope, SharingStarted.Eagerly, emptyList())
    }

    val speakers: StateFlow<Speakers> = storage.getConferenceCache()
        .map { it.speakers }
        .map { it.filter { speaker -> speaker.photoUrl.isNotBlank() } }
        .map { Speakers(it) }
        .stateIn(scope, SharingStarted.Eagerly, Speakers(emptyList()))

    fun getTheme(): Flow<Theme> = storage.getTheme()

    fun setTheme(theme: Theme) {
        scope.launch { storage.setTheme(theme) }
    }

    init {
        scope.launch {
            // Set user ID
            val userId = storage.getUserId().first()
            if (userId == null) {
                client.sign()
            }

            // Download fresh conference data
            val conferenceData = client.downloadConferenceData()
            storage.setConferenceCache(conferenceData)

            // Do whatever with votes
            votes.value = client.myVotes()
        }

        scope.launch {
            runCatching {
                serverTime = client.getServerTime()
                requestTime = GMTDate()
            }
            _time.value = now()

            while (true) {
                delay(1000)
                _time.value = now()
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

    /**
     * Accept privacy policy clicked.
     */
    fun acceptPrivacyPolicy() {
        scope.launch {
            val userId = storage.getUserId().first()
            if (userId != null) return@launch

            val newUserId = generateUserId()
            client.userId = newUserId
            client.sign()
            storage.setUserId(newUserId)
        }
    }

    /**
     * Request permissions to send notifications.
     */
    fun requestNotificationPermissions() {
        scope.launch {
            storage.setNotificationsAllowed(true)
            notificationManager.requestPermission()
        }
    }

    /**
     * Vote for session.
     */
    suspend fun vote(sessionId: SessionId, rating: Score?): Boolean {
        if (!client.vote(sessionId, rating)) return false

        val allVotes = votes.value
            .filter { it.sessionId != sessionId }
            .toMutableList()

        allVotes.add(VoteInfo(sessionId, rating))
        votes.value = allVotes
        return true
    }

    suspend fun sendFeedback(sessionId: SessionId, feedbackValue: String): Boolean =
        client.sendFeedback(sessionId, feedbackValue)

    fun speakerById(id: SpeakerId): Speaker = speakers.value[id] ?: UNKNOWN_SPEAKER

    fun sessionById(id: SessionId): SessionCardView =
        sessionCards.value.find { it.id == id } ?: UNKNOWN_SESSION_CARD

    fun sessionByIdFlow(id: SessionId): Flow<SessionCardView> =
        sessionCards
            .map { sessions -> sessions.find { it.id == id } ?: UNKNOWN_SESSION_CARD }

    fun speakersBySessionId(id: SessionId): Flow<List<Speaker>> =
        sessionByIdFlow(id).map { session ->
            session.speakerIds.map { speakerId -> speakerById(speakerId) }
        }

    fun sessionsForSpeaker(id: SpeakerId): List<SessionCardView> =
        sessionCards.value.filter { id in it.speakerIds }

    /**
     * Mark session as favorite.
     */
    fun toggleFavorite(sessionId: SessionId, favorite: Boolean) {
        scope.launch {
            val favorites = storage.getFavorites().first().toMutableSet()
            if (favorite) favorites.add(sessionId) else favorites.remove(sessionId)
            storage.setFavorites(favorites)
        }
    }

    fun partnerDescription(name: String): String {
        return PARTNER_DESCRIPTIONS[name] ?: ""
    }

    private fun scheduleNotification(session: SessionCardView) {
        scope.launch {
            val notificationsAllowed = storage.getNotificationsAllowed().first()
            if (!notificationsAllowed) return@launch

            val startTimestamp = session.startsAt.timestamp
            val reminderTimestamp = startTimestamp - 5 * 60 * 1000
            val nowTimestamp = now().timestamp
            val delay = reminderTimestamp - nowTimestamp
            val voteTimeStamp = session.endsAt.timestamp

            when {
                delay >= 0 -> {
                    notificationManager.schedule(delay, session.title, "Starts in 5 minutes.")
                }

                nowTimestamp in reminderTimestamp..<startTimestamp -> {
                    notificationManager.schedule(0, session.title, "The session is about to start.")
                }

                nowTimestamp in startTimestamp..<voteTimeStamp -> {
                    notificationManager.schedule(0, session.title, "Hurry up! The session has already started!")
                }
            }

            if (nowTimestamp > voteTimeStamp) return@launch

            val voteDelay = voteTimeStamp - nowTimestamp
            notificationManager.schedule(
                voteDelay,
                "${session.title} finished",
                "How was the talk?"
            )
        }
    }

    private fun cancelNotification(session: SessionCardView) {
        scope.launch {
            val allowed = storage.getNotificationsAllowed().first()
            if (allowed) {
                notificationManager.cancel(session.title)
                notificationManager.cancel("${session.title} finished")
            }
        }
    }

    /**
     * Get current time synchronized with server.
     */
    private fun now(): GMTDate {
        return GMTDate() + (serverTime.timestamp - requestTime.timestamp)
    }
}
