package org.jetbrains.kotlinconf.storage

import com.russhwolf.settings.ExperimentalSettingsApi
import com.russhwolf.settings.ObservableSettings
import com.russhwolf.settings.coroutines.getBooleanFlow
import com.russhwolf.settings.coroutines.getStringOrNullFlow
import com.russhwolf.settings.set
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.SingleIn
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import org.jetbrains.kotlinconf.Conference
import org.jetbrains.kotlinconf.ConferenceInfo
import org.jetbrains.kotlinconf.GoldenKodeeData
import org.jetbrains.kotlinconf.NotificationSettings
import org.jetbrains.kotlinconf.SessionId
import org.jetbrains.kotlinconf.VoteInfo
import org.jetbrains.kotlinconf.di.Year
import org.jetbrains.kotlinconf.di.YearScope


@ContributesBinding(YearScope::class)
@SingleIn(YearScope::class)
@OptIn(ExperimentalSettingsApi::class)
class YearlyStorageImpl(
    @Year private val year: Int,
    private val settings: ObservableSettings,
    private val assetStorage: AssetStorage,
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

    val POLICY_SIGNED = key("policySigned")
    val CONFERENCE_CACHE = key("conferenceCache")
    val CONFERENCE_INFO_CACHE = key("conferenceInfoCache")
    val GOLDEN_KODEE_CACHE = key("goldenKodeeCache")
    val FAVORITES = key("favorites")
    val NOTIFICATION_SETTINGS = key("notificationSettings")
    val VOTES = key("votes")

    override fun isPolicySigned(): Flow<Boolean> = settings.getBooleanFlow(POLICY_SIGNED, false)
    override suspend fun setPolicySigned(value: Boolean) = settings.set(POLICY_SIGNED, value)

    override fun getConferenceCache(): Flow<Conference?> = settings.getStringOrNullFlow(CONFERENCE_CACHE).map { it.decodeOrNull<Conference>() }
    override suspend fun setConferenceCache(value: Conference) = settings.set(CONFERENCE_CACHE, json.encodeToString(value))

    override fun getConferenceInfoCache(): Flow<ConferenceInfo?> = settings.getStringOrNullFlow(CONFERENCE_INFO_CACHE).map { it.decodeOrNull<ConferenceInfo>() }
    override suspend fun setConferenceInfoCache(value: ConferenceInfo) = settings.set(CONFERENCE_INFO_CACHE, json.encodeToString(value))

    override fun getGoldenKodeeCache(): Flow<GoldenKodeeData?> = settings.getStringOrNullFlow(GOLDEN_KODEE_CACHE).map { it.decodeOrNull<GoldenKodeeData>() }
    override suspend fun setGoldenKodeeCache(value: GoldenKodeeData) = settings.set(GOLDEN_KODEE_CACHE, json.encodeToString(value))

    override fun getFavorites(): Flow<Set<SessionId>> = settings.getStringOrNullFlow(FAVORITES).map { it.decodeOrNull<Set<SessionId>>() ?: [] }
    override suspend fun setFavorites(value: Set<SessionId>) = settings.set(FAVORITES, json.encodeToString(value))

    override fun getNotificationSettings(): Flow<NotificationSettings?> = settings.getStringOrNullFlow(NOTIFICATION_SETTINGS).map { it.decodeOrNull<NotificationSettings>() }
    override suspend fun setNotificationSettings(value: NotificationSettings) = settings.set(NOTIFICATION_SETTINGS, json.encodeToString(value))

    override fun getVotes(): Flow<List<VoteInfo>> = settings.getStringOrNullFlow(VOTES).map { it.decodeOrNull<List<VoteInfo>>() ?: [] }
    override suspend fun setVotes(value: List<VoteInfo>) = settings.set(VOTES, json.encodeToString(value))

    override suspend fun getAsset(name: String): String? = assetStorage.read(name)
    override suspend fun setAsset(name: String, content: String) = assetStorage.write(name, content)
}
