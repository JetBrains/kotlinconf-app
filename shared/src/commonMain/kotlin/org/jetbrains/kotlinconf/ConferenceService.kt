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
)

val UNKNOWN_SPEAKER: Speaker = Speaker(
    "unknown", "unknown", "unknown", "unknown", ""
)

class ConferenceService(
    val context: ApplicationContext,
    val endpoint: String,
) : CoroutineScope, Closeable {
    private val storage: ApplicationStorage = ApplicationStorage(context)
    private var userId2023: String? by storage.bind(String.serializer().nullable) { null }
    private var needsOnboarding: Boolean by storage.bind(Boolean.serializer()) { true }
    private var notificationsAllowed: Boolean by storage.bind(Boolean.serializer()) { false }

    private val client: APIClient by lazy {
        APIClient(endpoint)
    }

    private val exceptionHandler: CoroutineExceptionHandler = object : CoroutineExceptionHandler {
        override val key: CoroutineContext.Key<*> = CoroutineExceptionHandler

        override fun handleException(context: CoroutineContext, exception: Throwable) {
            println("Failed with exception: ${exception.message}")
            exception.printStackTrace()
        }
    }

    override val coroutineContext: CoroutineContext =
        SupervisorJob() + exceptionHandler + Dispatchers.IO

    private var serverTime = GMTDate()
    private var requestTime = GMTDate()
    private val notificationManager = NotificationManager(context)

    private val favorites = MutableStateFlow(emptySet<String>())
    private val conference = MutableStateFlow(Conference())

    private val votes = MutableStateFlow(emptyList<VoteInfo>())
    private val _speakers = MutableStateFlow(Speakers())

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

    val sessionsCards: StateFlowClass<List<SessionCardView>> by lazy {
        agenda.map {
            it.days
                .flatMap { it.timeSlots }
                .flatMap { it.sessions }
        }.stateIn(this, SharingStarted.Eagerly, emptyList())
            .asStateFlowClass()
    }
    val speakers: StateFlowClass<Speakers> by lazy {
        _speakers.asStateFlowClass()
    }

    init {
        try {
            launch {
                client.userId = userId2023

                if (userId2023 != null) {
                    runCatching {
                        client.sign()
                    }
                }

                synchronizeTime()
                _time.value = now()

                storage.get<Conference>("conferenceCache")?.let {
                    conference.value = it
                }

                conference.value = client.downloadConferenceData()
                storage.put("conferenceCache", conference.value)

                val speakers = conference.value.speakers
                    .filter { it.photoUrl.isNotBlank() }
                _speakers.value = Speakers(speakers)

                votes.value = client.myVotes()

                while (true) {
                    delay(60 * 1000)
                    _time.value = now()
                }
            }

            launch {
                val favoritesValue = storage.getList("favorites").toSet()
                favorites.value = favoritesValue
                favorites.debounce(1000)
                    .onEach {
                        storage.putList("favorites", it.toList())
                    }
                    .collect()
            }
        } catch (cause: Throwable) {
            println("Cause ${cause.message}")
            cause.printStackTrace()
        }
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
        if (userId2023 != null) return
        userId2023 = generateUserId()
        client.userId = userId2023
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
        sessionsCards.value.find { it.id == id } ?: UNKNOWN_SESSION_CARD

    fun sessionsForSpeaker(id: String): List<SessionCardView> =
        sessionsCards.value.filter { it.speakerIds.contains(id) }

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

        val startTimestamp = session.startsAt.timestamp - 5 * 60 * 1000
        val delay = startTimestamp - now().timestamp
        if (delay < 0) {
            return
        }

        notificationManager.schedule(delay, session.title, "Starts in 5 minutes.")

        val voteTimeStamp = session.endsAt.timestamp
        val voteDelay = voteTimeStamp - now().timestamp
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
