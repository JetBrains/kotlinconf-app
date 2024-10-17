package org.jetbrains.kotlinconf

import io.ktor.util.date.*
import io.ktor.utils.io.core.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import kotlinx.serialization.builtins.*
import org.jetbrains.kotlinconf.storage.*
import org.jetbrains.kotlinconf.utils.*
import kotlin.coroutines.*

val UNKNOWN_SESSION_CARD: SessionCardView = SessionCardView(
    "unknown", "unknown", "unknown",
    "unknown",
    GMTDate.START,
    GMTDate.START,
    emptyList(),
    isFinished = false,
    isFavorite = false,
    description = "unknown",
    vote = null,
    tags = emptyList()
)

val UNKNOWN_SPEAKER: Speaker = Speaker(
    "unknown", "unknown", "unknown", "unknown", ""
)

class ConferenceService(
    val context: ApplicationContext,
    val endpoint: String,
) : CoroutineScope, Closeable {
    private val storage: ApplicationStorage = ApplicationStorage(context)
    private var userId2024: String? by storage.bind(String.serializer().nullable) { null }
    private var needsOnboarding: Boolean by storage.bind(Boolean.serializer()) { true }
    private var notificationsAllowed: Boolean by storage.bind(Boolean.serializer()) { false }

    private val client: APIClient by lazy {
        APIClient(endpoint)
    }

    override val coroutineContext: CoroutineContext =
        SupervisorJob() + Dispatchers.App

    private var serverTime = GMTDate()
    private var requestTime = GMTDate()
    private val notificationManager = NotificationManager(context)

    val favorites = MutableStateFlow(emptySet<String>())
    private val conference = MutableStateFlow(Conference())

    private val votes = MutableStateFlow(emptyList<VoteInfo>())

    private val _time = MutableStateFlow(GMTDate())
    val time: StateFlowClass<GMTDate> = _time
        .asStateFlowClass()

    val agenda: StateFlowClass<Agenda> by lazy {
        combine(
            conference,
            favorites,
            time,
            votes,
        ) { conference, favorites, time, votes ->
            conference.buildAgenda(favorites, votes, time)
        }.stateIn(this, SharingStarted.Eagerly, Agenda())
            .asStateFlowClass()
    }

    val sessionCards: StateFlowClass<List<SessionCardView>> by lazy {
        agenda.map {
            it.days
                .flatMap { it.timeSlots }
                .flatMap { it.sessions }
        }.stateIn(this, SharingStarted.Eagerly, emptyList())
            .asStateFlowClass()
    }

    val speakers: StateFlowClass<Speakers> = conference
        .map { it.speakers }
        .map { it.filter { speaker -> speaker.photoUrl.isNotBlank() } }
        .map { Speakers(it) }
        .stateIn(this, SharingStarted.Eagerly, Speakers(emptyList()))
        .asStateFlowClass()

    fun sign() {
        client.userId = userId2024

        launch {
            if (userId2024 != null) {
                client.sign()
            }
        }
    }

    fun syncTime() {
        launch {
            synchronizeTime()
            _time.value = now()

            while (true) {
                delay(1000)
                _time.value = now()
            }
        }
    }

    fun updateConferenceData() {
        storage.get<Conference>("conferenceCache")?.let {
            conference.value = it
        }

        launch {
            conference.value = client.downloadConferenceData()
            storage.put("conferenceCache", conference.value)
        }
    }

    fun syncVotes() {
        launch {
            votes.value = client.myVotes()
        }
    }

    fun syncFavorites() {
        launch {
            val favoritesValue = storage.get<List<String>>("favorites")?.toSet() ?: emptySet()
            favorites.value = favoritesValue
            favorites.debounce(1000)
                .onEach {
                    storage.put("favorites", it.toList())
                }
                .collect()
        }
    }

    init {
        sign()
        syncTime()
        updateConferenceData()
        syncVotes()
        syncFavorites()
    }

    /**
     * Returns true if app is launched first time.
     */
    fun needsOnboarding(): Boolean {
        return needsOnboarding
    }

    fun completeOnboarding() {
        needsOnboarding = false
    }

    /**
     * ------------------------------
     * User actions.
     * ------------------------------
     */

    /**
     * Accept privacy policy clicked.
     */
    fun acceptPrivacyPolicy() {
        if (userId2024 != null) return
        userId2024 = generateUserId()
        client.userId = userId2024
        launch {
            client.sign()
        }
    }

    /**
     * Request permissions to send notifications.
     */
    fun requestNotificationPermissions() {
        notificationsAllowed = true
        notificationManager.requestPermission()
    }

    /**
     * Vote for session.
     */
    suspend fun vote(sessionId: String, rating: Score?): Boolean {
        if (!client.vote(sessionId, rating)) return false

        val allVotes = votes.value
            .filter { it.sessionId != sessionId }
            .toMutableList()

        allVotes.add(VoteInfo(sessionId, rating))
        votes.value = allVotes
        return true
    }

    suspend fun sendFeedback(sessionId: String, feedbackValue: String): Boolean =
        client.sendFeedback(sessionId, feedbackValue)

    fun speakerById(id: String): Speaker = speakers.value[id] ?: UNKNOWN_SPEAKER

    fun sessionById(id: String): SessionCardView =
        sessionCards.value.find { it.id == id } ?: UNKNOWN_SESSION_CARD

    fun sessionsForSpeaker(id: String): List<SessionCardView> =
        sessionCards.value.filter { it.speakerIds.contains(id) }

    /**
     * Mark session as favorite.
     */
    fun toggleFavorite(sessionId: String) {
        val newValue = favorites.value.toMutableSet()
        if (sessionId in newValue) {
            newValue.remove(sessionId)
            cancelNotification(sessionById(sessionId))
        } else {
            newValue.add(sessionId)
            scheduleNotification(sessionById(sessionId))
        }

        favorites.value = newValue
    }

    fun partnerDescription(name: String): String {
        return PARTNER_DESCRIPTIONS[name] ?: ""
    }

    private fun scheduleNotification(session: SessionCardView) {
        if (!notificationsAllowed) return

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

        if (nowTimestamp > voteTimeStamp) return
        
        val voteDelay = voteTimeStamp - nowTimestamp
        notificationManager.schedule(
            voteDelay,
            "${session.title} finished",
            "How was the talk?"
        )
    }

    private fun cancelNotification(session: SessionCardView) {
        if (!notificationsAllowed) {
            return
        }

        notificationManager.cancel(session.title)
        notificationManager.cancel("${session.title} finished")
    }

    private suspend fun synchronizeTime() {
        kotlin.runCatching {
            serverTime = client.getServerTime()
            requestTime = GMTDate()
        }
    }

    /**
     * Get current time synchronized with server.
     */
    private fun now(): GMTDate {
        return GMTDate() + (serverTime.timestamp - requestTime.timestamp)
    }

    override fun close() {
        client.close()
    }
}
