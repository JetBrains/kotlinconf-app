package org.jetbrains.kotlinconf

import io.ktor.util.date.*
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.*
import kotlinx.coroutines.flow.*
import kotlinx.serialization.*
import kotlinx.serialization.internal.*
import org.jetbrains.kotlinconf.presentation.*
import org.jetbrains.kotlinconf.storage.*
import kotlin.collections.set
import kotlin.coroutines.*
import kotlin.math.*
import kotlin.native.concurrent.*
import kotlin.time.*

/**
 * [ConferenceService] handles data and builds model.
 */
@ThreadLocal
@UseExperimental(ExperimentalTime::class, ExperimentalCoroutinesApi::class)
class ConferenceService(val context: ApplicationContext) : CoroutineScope {
    private val exceptionHandler = object : CoroutineExceptionHandler {
        override val key: CoroutineContext.Key<*> = CoroutineExceptionHandler

        override fun handleException(context: CoroutineContext, exception: Throwable) {
            _errors.offer(exception)
        }
    }

    private val _errors = ConflatedBroadcastChannel<Throwable>()
    val errors = _errors.wrap()

    override val coroutineContext: CoroutineContext = dispatcher() + SupervisorJob() + exceptionHandler

    private val storage: ApplicationStorage = ApplicationStorage(context)
    private var userId: String? by storage(String.serializer().nullable) { null }
    private var firstLaunch: Boolean by storage(Boolean.serializer()) { true }
    private var notificationsAllowed: Boolean by storage(Boolean.serializer()) { false }

    private var serverTime = GMTDate()
    private var requestTime = GMTDate()

    /**
     * Cached.
     */
    private var cards: MutableMap<String, SessionCard> = mutableMapOf()

    private val notificationManager = NotificationManager(context)

    /**
     * ------------------------------
     * Observables.
     * ------------------------------
     */

    /**
     * Public conference information.
     */
    private val _publicData by storage.live { SessionizeData() }
    val publicData = _publicData.wrap()

    private var _votesCountRequired: Int = 10

    /**
     * Favorites list.
     */
    private val _favorites by storage.live { mutableSetOf<String>() }
    val favorites = _favorites.wrap()

    private val _votes by storage.live { mutableMapOf<String, RatingData>() }
    private val _feed = ConflatedBroadcastChannel<FeedData>(FeedData())

    /**
     * Votes list.
     */
    val votes = _votes.wrap()

    /**
     * Twitter feed.
     */
    val feed = _feed.wrap()

    private var _videos = ConflatedBroadcastChannel<List<LiveVideo>>(emptyList())
    private var videos = _videos.wrap()

    /**
     * Live sessions.
     */
    private val _liveSessions = ConflatedBroadcastChannel<Set<String>>(mutableSetOf())
    private val _upcomingFavorites = ConflatedBroadcastChannel<Set<String>>(mutableSetOf())

    @UseExperimental(ExperimentalTime::class)
    private val _homeState = ConflatedBroadcastChannel<HomeState>(HomeState.During)

    val liveSessions = _liveSessions.asFlow().map {
        it.toList().map { id -> sessionCard(id) }
    }.wrap()

    val upcomingFavorites = _upcomingFavorites.asFlow().map {
        it.toList().map { id -> sessionCard(id) }
    }.wrap()

    val schedule = publicData.map {
        it.sessions
            .filter { !it.title.equals("break", ignoreCase = true) }
            .groupByDay(this)
            .addDayStart()
    }.wrap()

    val favoriteSchedule = favorites.map {
        it.map { id -> session(id) }
            .groupByDay(this)
            .addDayStart()
    }.wrap()

    val speakers = publicData.map { it.speakers.filter { it.profilePicture != null } }.wrap()

    val sessions = publicData.map {
        it.sessions
            .filter { !it.isServiceSession }
            .sortedBy { it.startsAt.timestamp }.map { sessionCard(it.id) }
    }.wrap()

    val homeState = _homeState.wrap()

    init {
        launch {
            try {
                userId?.let { ClientApi.sign(it) }
                refresh()
            } catch (cause: Throwable) {
            }
        }

        launch {
            while (true) {
                try {
                    scheduledUpdate()
                } catch (cause: Throwable) {
                    _errors.offer(cause)
                }
                delay(60 * 1000)
            }
        }

        launch {
            var lastState: HomeState? = null
            while (true) {
                try {
                    val now = now()
                    val state = when {
                        now < CONFERENCE_START -> {
                            val diff = max(0, (CONFERENCE_START.timestamp - now.timestamp) / 1000)
                            val remaining = diff.toDuration(DurationUnit.SECONDS)

                            HomeState.Before(remaining)
                        }
                        now > CONFERENCE_END -> HomeState.After
                        else -> HomeState.During
                    }

                    if (lastState != state && state == HomeState.During) {
                        updateUpcoming()
                        updateLive()
                    }

                    _homeState.offer(state)
                    lastState = state
                } catch (cause: Throwable) {
                    _errors.offer(cause)
                }

                delay(1000)
            }
        }
    }

    /**
     * ------------------------------
     * Representation.
     * ------------------------------
     */

    /**
     * Returns true if app is launched first time.
     */
    fun isFirstLaunch(): Boolean {
        val result = firstLaunch
        if (result) {
            firstLaunch = false
        }

        return result
    }

    /**
     * Return current state for home screen.
     */
    fun currentHomeState(): HomeState = _homeState.value

    /**
     * Get speakers from session.
     */
    fun sessionSpeakers(sessionId: String): List<SpeakerData> {
        val speakerIds = session(sessionId).speakers
        return speakerIds.map { speaker(it) }.filter { it.profilePicture != null }
    }

    /**
     * Get sessions for the speaker with id [speakerId].
     */
    fun speakerSessions(speakerId: String): List<SessionCard> {
        val sessionIds = speaker(speakerId).sessions
        return sessionIds.map { sessionCard(it) }
    }

    /**
     * Find sessions in the room with id [roomId].
     */
    fun roomSessions(roomId: Int): List<SessionCard> = talks().filter { it.roomId == roomId }
        .map { sessionCard(it.id) }
        .sortedBy { it.session.startsAt }
        .filter { !it.session.isWorkshop() }

    /**
     * Find room by location label.
     */
    fun roomByMapName(namesInArea: List<String>): RoomData? {
        val matching = mapOf(
            "keynote hot spot" to 7972,
            "aud. 15 hot spot" to 7975,
            "aud. 12 hot spot" to 7974,
            "aud. 11 hot spot" to 7973,
            "party area hot spot" to 0,
            "registration hot spot" to 0,
            "aud. 16 hot spot" to 0,
            "aud. 17 hot spot" to 0,
            "aud. 18 hot spot" to 0,
            "aud. 19 hot spot" to 0,
            "aud. 20 hot spot" to 7976
        )

        val id = namesInArea.mapNotNull { matching[it] }.firstOrNull()?.takeIf { it > 0 } ?: return null
        return room(id)
    }

    /**
     * Find speaker by id.
     */
    fun speaker(id: String): SpeakerData =
        _publicData.value.speakers.find { it.id == id } ?: error("Internal error. Speaker with id $id not found.")

    /**
     * Find session by id.
     */
    fun session(id: String): SessionData =
        _publicData.value.sessions.find { it.id == id } ?: error("Internal error. Session with id $id not found.")

    /**
     * Find room by id.
     */
    fun room(id: Int): RoomData? =
        _publicData.value.rooms.find { it.id == id }

    /**
     * Get session card.
     */
    fun sessionCard(id: String): SessionCard {
        cards[id]?.let { return it }

        val session = session(id)
        val roomId = session.roomId ?: error("No room id in session: ${session.id}")

        val location = room(roomId)!!
        val speakers = sessionSpeakers(id)
        val isFavorite = favorites.map { id in it }.wrap()
        val ratingData = votes.map { it[id] }.wrap()
        val isLive = _liveSessions.asFlow().map {
            if (id !in it) return@map null
            val videos = _videos.value
            videos.find { it.room == location.id }?.videoId
        }.distinctUntilChanged().wrap()

        val result = SessionCard(
            session,
            session.startsAt.dayAndMonth(),
            "${session.startsAt.time()}-${session.endsAt.time()}",
            location,
            isLive,
            speakers,
            isFavorite,
            ratingData
        )

        cards[id] = result
        return result
    }

    /**
     * Estimate how many minutes left for the session card.
     */
    fun minutesLeft(card: SessionCard): Int {
        val remaining = (card.session.endsAt.timestamp - now().timestamp) / (1000 * 60)
        return max(0, remaining.toInt())
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
        if (userId != null) return

        val id = generateUserId().also {
            userId = it
        }

        launch {
            ClientApi.sign(id)
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
    fun vote(sessionId: String, rating: RatingData) {
        launch {
            val userId = userId ?: throw Unauthorized()
            val votes = _votes.value
            val oldRating = votes[sessionId]
            val newRating = if (rating == oldRating) null else rating

            try {
                updateVote(sessionId, newRating)

                if (newRating != null) {
                    val vote = VoteData(sessionId, rating)
                    ClientApi.postVote(userId, vote)
                } else {
                    ClientApi.deleteVote(userId, sessionId)
                }
            } catch (cause: Throwable) {
                updateVote(sessionId, oldRating)
                throw cause
            }

        }
    }

    /**
     * Get required count of votes.
     */
    fun votesCountRequired(): Int = _votesCountRequired

    /**
     * Mark session as favorite.
     */
    fun markFavorite(sessionId: String) {
        launch {
            val userId = userId ?: throw Unauthorized()
            val newValue = sessionId !in _favorites.value

            try {
                updateFavorite(sessionId, newValue)
                if (newValue) {
                    ClientApi.postFavorite(userId, sessionId)
                    scheduleNotification(session(sessionId))
                } else {
                    ClientApi.deleteFavorite(userId, sessionId)
                    cancelNotification(session(sessionId))
                }
            } catch (cause: Throwable) {
                updateFavorite(sessionId, !newValue)
            }
        }
    }

    private fun scheduleNotification(session: SessionData) {
        if (!notificationsAllowed) {
            return
        }

        val startTimestamp = session.startsAt.timestamp - 15 * 60 * 1000
        val delay = startTimestamp - now().timestamp
        if (delay < 0) {
            return
        }

        notificationManager.schedule(delay, session.title, "Starts in 15 minutes.")
    }

    private fun cancelNotification(session: SessionData) {
        if (!notificationsAllowed) {
            return
        }

        notificationManager.cancel(session.title)
    }

    /**
     * Reload data model from server.
     */
    fun refresh() {
        launch {
            serverTime = ClientApi.getServerTime()

            ClientApi.getAll(userId).apply {
                _videos.offer(liveVideos)
                _publicData.offer(allData)
                _favorites.offer(favorites.toMutableSet())
                val votesMap = mutableMapOf<String, RatingData>().apply {
                    putAll(votes.mapNotNull { vote -> vote.rating?.let { vote.sessionId to it } })
                }
                _votes.offer(votesMap)
                _votesCountRequired = votesCountRequired

                updateModel()
            }

        }
    }

    private suspend fun scheduledUpdate() {
        if (_publicData.value.sessions.isEmpty()) {
            refresh()
        }

        updateModel()
    }

    private suspend fun updateModel() {
        synchronizeTime()
        updateLive()
        updateUpcoming()
        updateFeed()
    }

    private suspend fun synchronizeTime() {
        serverTime = ClientApi.getServerTime()
        requestTime = GMTDate()
    }

    private fun updateLive() {
        val sessions = _publicData.value.sessions
        if (sessions.isEmpty()) {
            _liveSessions.offer(emptySet())
            return
        }

        val now = now()
        val result = sessions
            .filter { now >= it.startsAt && now <= it.endsAt && !it.isServiceSession }
            .filter {
                val room = it.roomId ?: return@filter false
                _videos.value.any { it.room == room }
            }
            .map { it.id }
            .toSet()

        _liveSessions.offer(result)
    }

    private fun updateUpcoming() {
        val favorites = _favorites.value.toList()
        if (favorites.isEmpty()) {
            _upcomingFavorites.offer(emptySet())
            return
        }

        val now = now()
        val today = now.dayOfYear
        val cards = favorites
            .asSequence()
            .map { sessionCard(it) }
            .filter { it.session.startsAt.dayOfYear == today && it.session.endsAt >= now() }
            .sortedBy { it.session.startsAt.timestamp }
            .map { it.session.id }
            .toSet()

        _upcomingFavorites.offer(cards)
    }

    private fun updateVote(sessionId: String, rating: RatingData?) {
        val votes = _votes.value

        if (rating == null) {
            votes.remove(sessionId)
        } else {
            votes[sessionId] = rating
        }

        _votes.offer(votes)
    }

    private fun updateFavorite(sessionId: String, isFavorite: Boolean) {
        val favorites = _favorites.value
        if (isFavorite) check(sessionId !in favorites)
        if (!isFavorite) check(sessionId in favorites)

        if (!isFavorite) {
            favorites.remove(sessionId)
        } else {
            favorites.add(sessionId)
        }

        _favorites.offer(favorites)

        updateUpcoming()
    }

    private suspend fun updateFeed() {
        _feed.offer(ClientApi.getFeed())
    }

    /**
     * Get talk sessions.
     */
    private fun talks(): List<SessionData> = _publicData.value.sessions.filter { !it.isServiceSession }

    /**
     * Get current time synchronized with server.
     */
    private fun now(): GMTDate {
        return GMTDate() + (serverTime.timestamp - requestTime.timestamp)
    }
}
