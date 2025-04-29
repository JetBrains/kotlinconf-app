package org.jetbrains.kotlinconf.storage

import com.russhwolf.settings.ExperimentalSettingsApi
import com.russhwolf.settings.ObservableSettings
import com.russhwolf.settings.coroutines.getBooleanFlow
import com.russhwolf.settings.coroutines.getStringOrNullFlow
import com.russhwolf.settings.set
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import org.jetbrains.kotlinconf.Conference
import org.jetbrains.kotlinconf.Flags
import org.jetbrains.kotlinconf.NewsItem
import org.jetbrains.kotlinconf.NotificationSettings
import org.jetbrains.kotlinconf.SessionId
import org.jetbrains.kotlinconf.Theme
import org.jetbrains.kotlinconf.VoteInfo

@OptIn(ExperimentalSettingsApi::class)
class MultiplatformSettingsStorage(
    private val settings: ObservableSettings,
) : ApplicationStorage {
    private val json = Json {
        ignoreUnknownKeys = true
    }

    private inline fun <reified T> String?.decodeOrNull(): T? {
        if (this == null) return null

        return try {
            json.decodeFromString<T>(this)
        } catch (_: SerializationException) {
            null
        }
    }

    override fun getUserId(): Flow<String?> = settings.getStringOrNullFlow(Keys.USER_ID)
    override suspend fun setUserId(value: String?) = settings.set(Keys.USER_ID, value)

    override fun getPendingUserId(): Flow<String?> = settings.getStringOrNullFlow(Keys.PENDING_USER_ID)
    override suspend fun setPendingUserId(value: String?) = settings.set(Keys.PENDING_USER_ID, value)

    override fun isOnboardingComplete(): Flow<Boolean> = settings.getBooleanFlow(Keys.ONBOARDING_COMPLETE, false)
    override suspend fun setOnboardingComplete(value: Boolean) = settings.set(Keys.ONBOARDING_COMPLETE, value)

    override fun getTheme(): Flow<Theme> = settings.getStringOrNullFlow(Keys.THEME)
        .map { it?.let { Theme.valueOf(it) } ?: Theme.SYSTEM }

    override suspend fun setTheme(value: Theme) = settings
        .set(Keys.THEME, value.name)

    override fun getConferenceCache(): Flow<Conference?> = settings.getStringOrNullFlow(Keys.CONFERENCE_CACHE)
        .map { it.decodeOrNull<Conference>() }

    override suspend fun setConferenceCache(value: Conference) = settings
        .set(Keys.CONFERENCE_CACHE, json.encodeToString(value))

    override fun getFavorites(): Flow<Set<SessionId>> = settings.getStringOrNullFlow(Keys.FAVORITES)
        .map { it.decodeOrNull<Set<SessionId>>() ?: emptySet() }

    override suspend fun setFavorites(value: Set<SessionId>) = settings
        .set(Keys.FAVORITES, json.encodeToString(value))

    override fun getNews(): Flow<List<NewsItem>> = settings.getStringOrNullFlow(Keys.NEWS_CACHE)
        .map { it.decodeOrNull<List<NewsItem>>() ?: emptyList() }

    override suspend fun setNews(value: List<NewsItem>) = settings
        .set(Keys.NEWS_CACHE, json.encodeToString(value))

    override fun getNotificationSettings(): Flow<NotificationSettings?> =
        settings.getStringOrNullFlow(Keys.NOTIFICATION_SETTINGS)
            .map { it.decodeOrNull<NotificationSettings>() }

    override suspend fun setNotificationSettings(value: NotificationSettings) = settings
        .set(Keys.NOTIFICATION_SETTINGS, json.encodeToString(value))

    override fun getVotes(): Flow<List<VoteInfo>> = settings.getStringOrNullFlow(Keys.VOTES)
        .map { it.decodeOrNull<List<VoteInfo>>() ?: emptyList() }

    override suspend fun setVotes(value: List<VoteInfo>) = settings
        .set(Keys.VOTES, json.encodeToString(value))

    override fun getFlagsBlocking(): Flags? =
        settings.getStringOrNull(Keys.FLAGS)?.decodeOrNull<Flags>()

    override fun getFlags(): Flow<Flags?> = settings.getStringOrNullFlow(Keys.FLAGS)
        .map { it.decodeOrNull<Flags>() }

    override suspend fun setFlags(value: Flags) = settings
        .set(Keys.FLAGS, json.encodeToString(value))

    override fun ensureCurrentVersion() {
        val version = settings.getInt(Keys.STORAGE_VERSION, 0)
        if (version < CURRENT_STORAGE_VERSION) {
            // Fully destructive migration on version mismatch
            settings.clear()
            settings.set(Keys.STORAGE_VERSION, CURRENT_STORAGE_VERSION)
        }
    }

    private companion object {
        const val CURRENT_STORAGE_VERSION: Int = 2025_000
    }

    private object Keys {
        const val STORAGE_VERSION = "storageVersion"
        const val USER_ID = "userId2025"
        const val PENDING_USER_ID = "pendingUserId2025"
        const val ONBOARDING_COMPLETE = "onboardingComplete"
        const val THEME = "theme"
        const val CONFERENCE_CACHE = "conferenceCache"
        const val NEWS_CACHE = "newsCache"
        const val FAVORITES = "favorites"
        const val NOTIFICATION_SETTINGS = "notificationSettings"
        const val VOTES = "votes"
        const val FLAGS = "flags"
    }
}
