package org.jetbrains.kotlinconf.storage

import kotlinx.coroutines.flow.Flow
import org.jetbrains.kotlinconf.Conference
import org.jetbrains.kotlinconf.ConferenceInfo
import org.jetbrains.kotlinconf.GoldenKodeeData
import org.jetbrains.kotlinconf.NotificationSettings
import org.jetbrains.kotlinconf.SessionId
import org.jetbrains.kotlinconf.VoteInfo

interface YearlyStorage {
    fun isPolicySigned(): Flow<Boolean>
    suspend fun setPolicySigned(value: Boolean)

    fun getConferenceCache(): Flow<Conference?>
    suspend fun setConferenceCache(value: Conference)

    fun getConferenceInfoCache(): Flow<ConferenceInfo?>
    suspend fun setConferenceInfoCache(value: ConferenceInfo)

    fun getGoldenKodeeCache(): Flow<GoldenKodeeData?>
    suspend fun setGoldenKodeeCache(value: GoldenKodeeData)

    fun getFavorites(): Flow<Set<SessionId>>
    suspend fun setFavorites(value: Set<SessionId>)

    fun getNotificationSettings(): Flow<NotificationSettings?>
    suspend fun setNotificationSettings(value: NotificationSettings)

    fun getVotes(): Flow<List<VoteInfo>>
    suspend fun setVotes(value: List<VoteInfo>)

    suspend fun getAsset(name: String): String?
    suspend fun setAsset(name: String, content: String)
}
