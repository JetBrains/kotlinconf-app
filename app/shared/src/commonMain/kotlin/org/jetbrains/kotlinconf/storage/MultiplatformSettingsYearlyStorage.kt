package org.jetbrains.kotlinconf.storage

import com.russhwolf.settings.ExperimentalSettingsApi
import com.russhwolf.settings.ObservableSettings
import com.russhwolf.settings.coroutines.getStringOrNullFlow
import com.russhwolf.settings.set
import dev.zacsweers.metro.ContributesBinding
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import org.jetbrains.kotlinconf.Conference
import org.jetbrains.kotlinconf.ConferenceInfo
import org.jetbrains.kotlinconf.NotificationSettings
import org.jetbrains.kotlinconf.SessionId
import org.jetbrains.kotlinconf.VoteInfo
import org.jetbrains.kotlinconf.di.YearScope
import org.jetbrains.kotlinconf.utils.Logger
import org.jetbrains.kotlinconf.utils.tagged

@ContributesBinding(YearScope::class)
@OptIn(ExperimentalSettingsApi::class)
class MultiplatformSettingsYearlyStorage(
    private val year: Int,
    private val settings: ObservableSettings,
) : YearlyStorage {
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

    private fun key(name: String): String = "${year}_$name"

    override fun getUserId(): Flow<String?> = settings.getStringOrNullFlow(key(Keys.USER_ID))
    override suspend fun setUserId(value: String?) = settings.set(key(Keys.USER_ID), value)

    override fun getPendingUserId(): Flow<String?> = settings.getStringOrNullFlow(key(Keys.PENDING_USER_ID))
    override suspend fun setPendingUserId(value: String?) = settings.set(key(Keys.PENDING_USER_ID), value)

    override fun getConferenceCache(): Flow<Conference?> = settings.getStringOrNullFlow(key(Keys.CONFERENCE_CACHE))
        .map { it.decodeOrNull<Conference>() }

    override suspend fun setConferenceCache(value: Conference) = settings
        .set(key(Keys.CONFERENCE_CACHE), json.encodeToString(value))

    override fun getConferenceInfoCache(): Flow<ConferenceInfo?> = settings.getStringOrNullFlow(key(Keys.CONFERENCE_INFO_CACHE))
        .map { it.decodeOrNull<ConferenceInfo>() }

    override suspend fun setConferenceInfoCache(value: ConferenceInfo) = settings
        .set(key(Keys.CONFERENCE_INFO_CACHE), json.encodeToString(value))

    override fun getFavorites(): Flow<Set<SessionId>> = settings.getStringOrNullFlow(key(Keys.FAVORITES))
        .map { it.decodeOrNull<Set<SessionId>>() ?: emptySet() }

    override suspend fun setFavorites(value: Set<SessionId>) = settings
        .set(key(Keys.FAVORITES), json.encodeToString(value))

    override fun getNotificationSettings(): Flow<NotificationSettings?> =
        settings.getStringOrNullFlow(key(Keys.NOTIFICATION_SETTINGS))
            .map { it.decodeOrNull<NotificationSettings>() }

    override suspend fun setNotificationSettings(value: NotificationSettings) = settings
        .set(key(Keys.NOTIFICATION_SETTINGS), json.encodeToString(value))

    override fun getVotes(): Flow<List<VoteInfo>> = settings.getStringOrNullFlow(key(Keys.VOTES))
        .map { it.decodeOrNull<List<VoteInfo>>() ?: emptyList() }

    override suspend fun setVotes(value: List<VoteInfo>) = settings
        .set(key(Keys.VOTES), json.encodeToString(value))

    private object Keys {
        const val USER_ID = "userId"
        const val PENDING_USER_ID = "pendingUserId"
        const val CONFERENCE_CACHE = "conferenceCache"
        const val CONFERENCE_INFO_CACHE = "conferenceInfoCache"
        const val FAVORITES = "favorites"
        const val NOTIFICATION_SETTINGS = "notificationSettings"
        const val VOTES = "votes"
    }
}
