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
import org.jetbrains.kotlinconf.SessionId
import org.jetbrains.kotlinconf.Theme

expect fun createSettings(context: ApplicationContext): ObservableSettings

@OptIn(ExperimentalSettingsApi::class)
class MultiplatformSettingsStorage(context: ApplicationContext) : ApplicationStorage {
    private val settings = createSettings(context)

    override fun getUserId(): Flow<String?> = settings.getStringOrNullFlow("userid2025")
    override suspend fun setUserId(value: String?) = settings.set("userid2025", value)

    override fun isOnboardingComplete(): Flow<Boolean> = settings.getBooleanFlow("onboardingComplete", false)
    override suspend fun setOnboardingComplete(value: Boolean) = settings.set("onboardingComplete", value)

    override fun getNotificationsAllowed(): Flow<Boolean> = settings.getBooleanFlow("notificationsAllowed", false)
    override suspend fun setNotificationsAllowed(value: Boolean) = settings.set("notificationsAllowed", value)

    override fun getTheme(): Flow<Theme> = settings.getStringOrNullFlow("theme").map { it?.let { Theme.valueOf(it) } ?: Theme.SYSTEM }
    override suspend fun setTheme(value: Theme) = settings.set("theme", value.name)

    override fun getConferenceCache(): Flow<Conference> = settings.getStringOrNullFlow("conferenceCache").map { it?.let { Json.decodeFromString<Conference>(it) } ?: Conference() }
    override suspend fun setConferenceCache(value: Conference) = settings.set(
        "conferenceCache",
        Json.encodeToString(value)
    )

    override fun getFavorites(): Flow<Set<SessionId>> = settings.getStringOrNullFlow("favorites").map { it?.let { Json.decodeFromString<Set<SessionId>>(it) } ?: emptySet() }
    override suspend fun setFavorites(value: Set<SessionId>) = settings.set("favorites", Json.encodeToString(value))
}
