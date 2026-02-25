package org.jetbrains.kotlinconf

import com.mmk.kmpnotifier.notification.NotifierManager
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.SingleIn
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMap
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.datetime.LocalDateTime
import org.jetbrains.kotlinconf.LocalNotificationId.Type
import org.jetbrains.kotlinconf.di.YearGraph
import org.jetbrains.kotlinconf.network.ApplicationApi
import org.jetbrains.kotlinconf.storage.ApplicationStorage
import org.jetbrains.kotlinconf.storage.YearlyStorage
import org.jetbrains.kotlinconf.utils.Logger
import org.jetbrains.kotlinconf.utils.tagged
import kotlin.time.Duration.Companion.minutes

@OptIn(ExperimentalCoroutinesApi::class)
@Inject
@SingleIn(AppScope::class)
class ConferenceService(
    private val appClient: ApplicationApi,
    private val applicationStorage: ApplicationStorage,
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

    private val currentYearGraph: MutableStateFlow<YearGraph?> = MutableStateFlow(null)

    private val currentYearlyStorage: Flow<YearlyStorage> =
        currentYearGraph.map { it?.storage }.filterNotNull()

    init {
        applicationStorage.initialize()

        scope.launch {
            val newConfig = appClient.getConfig()
            if (newConfig != null) {
                taggedLogger.log { "New config received from server: $newConfig" }
                applicationStorage.setConfig(newConfig)
                taggedLogger.log { "Stored new config locally" }
            }
        }

        scope.launch {
            applicationStorage.getConfig()
                .filterNotNull()
                .collect { config ->
                    taggedLogger.log { "Loaded local config: $config, loading data for ${config.currentYear}" }

                    taggedLogger.log { "Recreating year graph" }
                    currentYearGraph.update {
                        yearGraphFactory.create(config.currentYear)
                    }

                    taggedLogger.log { "Loading conference data" }
                    loadConferenceData()

                    taggedLogger.log { "Preloading assets" }
                    downloadAllAssets()

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

    private val userId = applicationStorage.userId

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

    val mapData: StateFlow<MapData?> = conferenceInfo
        .map { it?.mapData }
        .stateIn(scope, SharingStarted.Eagerly, null)

    fun getTheme(): Flow<Theme> = applicationStorage.getTheme()

    fun setTheme(theme: Theme) {
        scope.launch { applicationStorage.setTheme(theme) }
    }

    suspend fun loadConferenceData() {
        val currentYearGraph = currentYearGraph.value ?: return
        val storage = currentYearGraph.storage
        val client = currentYearGraph.api

        // Load conference info (partners, days, about blocks, tags, map data)
        client.downloadConferenceInfo()?.let { storage.setConferenceInfoCache(it) }

        // Load conference schedule data
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
        return applicationStorage.isOnboardingComplete()
    }

    suspend fun completeOnboarding() {
        applicationStorage.setOnboardingComplete(true)
    }

    /**
     * Sign the privacy policy for the current year.
     *
     * @return true if the policy is signed.
     */
    suspend fun acceptPrivacyNotice(): Boolean {
        val currentYearGraph = currentYearGraph.value ?: return false
        val storage = currentYearGraph.storage
        val client = currentYearGraph.api

        if (storage.isPolicySigned().first()) return true

        val success = client.sign(userId.value)
        if (success) {
            storage.setPolicySigned(true)
            taggedLogger.log { "Policy signed successfully for user ${userId.value}" }
        } else {
            taggedLogger.log { "Policy signing failed for user ${userId.value}" }
        }
        return success
    }

    fun acceptPrivacyNoticeAsync() {
        scope.launch {
            acceptPrivacyNotice()
        }
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
        val storage = currentYearGraph.storage

        storage.setNotificationSettings(settings)
    }

    suspend fun isPolicySigned(): Boolean {
        val storage = currentYearGraph.value?.storage ?: return false
        return storage.isPolicySigned().first()
    }

    suspend fun vote(sessionId: SessionId, rating: Score?): Boolean {
        if (!isPolicySigned()) return false

        val currentYearGraph = currentYearGraph.value ?: return false
        val storage = currentYearGraph.storage
        val client = currentYearGraph.api

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
        if (!isPolicySigned()) return false
        val client = currentYearGraph.value?.api ?: return false
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
        withContext(Dispatchers.Default + NonCancellable) {
            val currentYearGraph = currentYearGraph.value ?: return@withContext
            val storage = currentYearGraph.storage

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
        if (!isPolicySigned()) {
            taggedLogger.log { "Can't sync votes, policy not signed" }
            return
        }

        val currentYearGraph = currentYearGraph.value ?: return
        val storage = currentYearGraph.storage
        val client = currentYearGraph.api

        taggedLogger.log { "Synchronizing votes for user ${userId.value}" }

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

    suspend fun downloadAllAssets() {
        val currentYearGraph = currentYearGraph.value ?: return
        val storage = currentYearGraph.storage
        val client = currentYearGraph.api

        val docPaths = listOf(
            "documents/app-privacy-notice.md",
            "documents/app-terms.md",
            "documents/code-of-conduct.md",
            "documents/visitors-privacy-notice.md",
            "documents/visitors-terms.md",
        )
        val mapData = storage.getConferenceInfoCache().first()?.mapData
        val mapPaths = mapData?.floors?.flatMap { listOf(it.svgPathLight, it.svgPathDark) } ?: emptyList()

        val allFiles = (docPaths + mapPaths)
        val missing = allFiles.filter { storage.getAsset(it) == null }

        if (missing.isEmpty()) {
            taggedLogger.log { "All assets cached locally" }
            return
        }

        taggedLogger.log { "${missing.size} asset(s) missing, downloading" }

        for (path in missing) {
            val content = client.downloadAsset(path)
            if (content != null) {
                storage.setAsset(path, content)
                taggedLogger.log { "Cached asset: $path" }
            } else {
                taggedLogger.log { "Failed to download asset: $path" }
            }
        }
    }

    /**
     * Reads the file contents from cache if available,
     * or attempts to download it (once) if missing.
     */
    suspend fun getAsset(path: String): String? {
        taggedLogger.log { "Reading asset: $path" }

        val storage = currentYearGraph.value?.storage ?: return null
        val cached = storage.getAsset(path)

        if (cached != null) {
            taggedLogger.log { "Found in cache: $path" }
            return cached
        }

        downloadAsset(path)

        return storage.getAsset(path)
    }

    suspend fun downloadAsset(path: String) {
        val currentYearGraph = currentYearGraph.value ?: return
        val storage = currentYearGraph.storage
        val client = currentYearGraph.api

        taggedLogger.log { "Downloading asset: $path" }
        val content = client.downloadAsset(path)
        if (content != null) {
            taggedLogger.log { "Cached asset: $path" }
            storage.setAsset(path, content)
        } else {
            taggedLogger.log { "Failed to download asset: $path" }
        }
    }

    fun getPartner(partnerId: PartnerId): Flow<PartnerInfo?> {
        return conferenceInfo
            .filterNotNull()
            .map { info ->
                info.partners
                    .flatMap { it.partners }
                    .firstOrNull { it.id == partnerId }
            }
    }
}
