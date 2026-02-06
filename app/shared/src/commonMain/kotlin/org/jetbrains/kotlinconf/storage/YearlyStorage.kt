package org.jetbrains.kotlinconf.storage

import kotlinx.coroutines.flow.Flow
import org.jetbrains.kotlinconf.Conference
import org.jetbrains.kotlinconf.ConferenceInfo
import org.jetbrains.kotlinconf.NotificationSettings
import org.jetbrains.kotlinconf.SessionId
import org.jetbrains.kotlinconf.VoteInfo

interface YearlyStorage {
    fun getUserId(): Flow<String?>
    suspend fun setUserId(value: String?)

    fun getPendingUserId(): Flow<String?>
    suspend fun setPendingUserId(value: String?)

    fun getConferenceCache(): Flow<Conference?>
    suspend fun setConferenceCache(value: Conference)

    fun getConferenceInfoCache(): Flow<ConferenceInfo?>
    suspend fun setConferenceInfoCache(value: ConferenceInfo)

    fun getFavorites(): Flow<Set<SessionId>>
    suspend fun setFavorites(value: Set<SessionId>)

    fun getNotificationSettings(): Flow<NotificationSettings?>
    suspend fun setNotificationSettings(value: NotificationSettings)

    fun getVotes(): Flow<List<VoteInfo>>
    suspend fun setVotes(value: List<VoteInfo>)
}
