package org.jetbrains.kotlinconf

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
import kotlinx.datetime.toInstant
import org.jetbrains.kotlinconf.storage.ApplicationStorage
import org.jetbrains.kotlinconf.utils.DateTimeFormatting
import kotlin.time.Duration.Companion.minutes

class ConferenceService(
    private val client: APIClient,
    private val timeProvider: TimeProvider,
    private val storage: ApplicationStorage,
    private val notificationManager: NotificationManager,
) {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    private val votes = MutableStateFlow(emptyList<VoteInfo>())

    val news: Flow<List<NewsDisplayItem>> = storage.getNews()
        .map { newsItems ->
            val now = timeProvider.now()
            newsItems.map { newsItem -> mapNewsItemToDisplayItem(newsItem, now) }
        }
        .flowOn(Dispatchers.Default)

    val agenda: StateFlow<Agenda> by lazy {
        combine(
            storage.getConferenceCache(),
            storage.getFavorites(),
            timeProvider.time,
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
            if (userId != null) {
                client.userId = userId
                client.sign()
            }

            // Download fresh conference data
            val conferenceData = client.downloadConferenceData()
            storage.setConferenceCache(conferenceData)

            // Do whatever with votes
            votes.value = client.myVotes()

            // Load fresh news items
            loadNews()
        }

        scope.launch {
            timeProvider.run()
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
    suspend fun acceptPrivacyPolicy() {
        val userId = storage.getUserId().first()
        if (userId != null) return

        val newUserId = generateUserId()
        client.userId = newUserId
        client.sign()
        storage.setUserId(newUserId)
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

    fun getNotificationSettings(): Flow<NotificationSettings> = storage.getNotificationSettings()

    suspend fun setNotificationSettings(settings: NotificationSettings) {
        storage.setNotificationSettings(settings)
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

    fun speakerById(id: SpeakerId): Speaker? = speakers.value[id]

    fun sessionByIdFlow(id: SessionId): Flow<SessionCardView?> =
        sessionCards.map { sessions -> sessions.find { it.id == id } }

    fun speakersBySessionId(id: SessionId): Flow<List<Speaker>> =
        sessionByIdFlow(id).map { session ->
            session?.speakerIds?.mapNotNull { speakerId -> speakerById(speakerId) } ?: emptyList()
        }

    fun sessionsForSpeaker(id: SpeakerId): List<SessionCardView> =
        sessionCards.value.filter { id in it.speakerIds }

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
        }
    }

    fun partnerDescription(name: String): String {
        return PARTNER_DESCRIPTIONS[name] ?: ""
    }

    private fun scheduleNotification(session: SessionCardView) {
        scope.launch {
            val notificationsAllowed = storage.getNotificationsAllowed().first()
            if (!notificationsAllowed) return@launch

            val start = session.startsAt.toInstant(EVENT_TIME_ZONE)
            val end = session.endsAt.toInstant(EVENT_TIME_ZONE)
            val now = timeProvider.now().toInstant(EVENT_TIME_ZONE)

            val reminderTime = start - 5.minutes

            // Notifications for session start
            val startsLater = now < reminderTime
            val startsSoon = now in reminderTime..<start
            val isLive = now in start..<end
            when {
                startsLater -> notificationManager.schedule((reminderTime - now).inWholeMilliseconds, session.title, "Starts in 5 minutes.")
                startsSoon -> notificationManager.schedule(0, session.title, "The session is about to start.")
                isLive -> notificationManager.schedule(0, session.title, "Hurry up! The session has already started!")
            }

            // Notifications for session end
            if (end > now) {
                notificationManager.schedule((end - now).inWholeMilliseconds, "${session.title} finished", "How was the talk?")
            }
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

    private fun mapNewsItemToDisplayItem(
        item: NewsItem,
        now: LocalDateTime,
    ): NewsDisplayItem {
        return NewsDisplayItem(
            id = item.id,
            photoUrl = item.photoUrl,
            date = item.date.toNewsDisplayTime(now),
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
        storage.setNews(client.getNews())
    }
}
