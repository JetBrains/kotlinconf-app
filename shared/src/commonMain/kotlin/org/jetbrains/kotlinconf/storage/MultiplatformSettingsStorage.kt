package org.jetbrains.kotlinconf.storage

import com.russhwolf.settings.ExperimentalSettingsApi
import com.russhwolf.settings.ObservableSettings
import com.russhwolf.settings.coroutines.getBooleanFlow
import com.russhwolf.settings.coroutines.getStringOrNullFlow
import com.russhwolf.settings.set
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.jetbrains.kotlinconf.ApplicationContext
import org.jetbrains.kotlinconf.Conference
import org.jetbrains.kotlinconf.NewsItem
import org.jetbrains.kotlinconf.NotificationSettings
import org.jetbrains.kotlinconf.SessionId
import org.jetbrains.kotlinconf.Theme

expect fun createSettings(context: ApplicationContext): ObservableSettings

@OptIn(ExperimentalSettingsApi::class)
class MultiplatformSettingsStorage(context: ApplicationContext) : ApplicationStorage {
    private val settings = createSettings(context)

    override fun getUserId(): Flow<String?> = settings.getStringOrNullFlow(Keys.USER_ID)
    override suspend fun setUserId(value: String?) = settings.set(Keys.USER_ID, value)

    override fun isOnboardingComplete(): Flow<Boolean> = settings.getBooleanFlow(Keys.ONBOARDING_COMPLETE, false)
    override suspend fun setOnboardingComplete(value: Boolean) = settings.set(Keys.ONBOARDING_COMPLETE, value)

    override fun getNotificationsAllowed(): Flow<Boolean> = settings.getBooleanFlow(Keys.NOTIFICATIONS_ALLOWED, false)
    override suspend fun setNotificationsAllowed(value: Boolean) = settings.set(Keys.NOTIFICATIONS_ALLOWED, value)

    override fun getTheme(): Flow<Theme> = settings.getStringOrNullFlow(Keys.THEME).map { it?.let { Theme.valueOf(it) } ?: Theme.SYSTEM }
    override suspend fun setTheme(value: Theme) = settings.set(Keys.THEME, value.name)

    override fun getConferenceCache(): Flow<Conference> = settings.getStringOrNullFlow(Keys.CONFERENCE_CACHE).map { it?.let { Json.decodeFromString<Conference>(it) } ?: Conference() }
    override suspend fun setConferenceCache(value: Conference) = settings.set(
        Keys.CONFERENCE_CACHE,
        Json.encodeToString(value)
    )

    override fun getFavorites(): Flow<Set<SessionId>> = settings.getStringOrNullFlow(Keys.FAVORITES).map { it?.let { Json.decodeFromString<Set<SessionId>>(it) } ?: emptySet() }
    override suspend fun setFavorites(value: Set<SessionId>) = settings.set(Keys.FAVORITES, Json.encodeToString(value))

    override fun getNews(): Flow<List<NewsItem>> = settings.getStringOrNullFlow(Keys.NEWS_CACHE)
        .map { it?.let { Json.decodeFromString<List<NewsItem>>(it) } ?: emptyList() }

    override suspend fun setNews(value: List<NewsItem>) = settings.set(
        Keys.NEWS_CACHE,
        Json.encodeToString(value)
    )

    override fun getNotificationSettings(): Flow<NotificationSettings> =
        settings.getStringOrNullFlow(Keys.NOTIFICATION_SETTINGS)
            .map { it?.let { Json.decodeFromString<NotificationSettings>(it) } ?: NotificationSettings() }

    override suspend fun setNotificationSettings(value: NotificationSettings) = settings.set(
        Keys.NOTIFICATION_SETTINGS,
        Json.encodeToString(value)
    )

    private object Keys {
        const val USER_ID = "userid2025"
        const val ONBOARDING_COMPLETE = "onboardingComplete"
        const val NOTIFICATIONS_ALLOWED = "notificationsAllowed"
        const val THEME = "theme"
        const val CONFERENCE_CACHE = "conferenceCache"
        const val NEWS_CACHE = "newsCache"
        const val FAVORITES = "favorites"
        const val NOTIFICATION_SETTINGS = "notificationSettings"
    }
}
