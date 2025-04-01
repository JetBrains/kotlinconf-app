package org.jetbrains.kotlinconf.storage

import kotlinx.coroutines.flow.Flow
import org.jetbrains.kotlinconf.Conference
import org.jetbrains.kotlinconf.Flags
import org.jetbrains.kotlinconf.NewsItem
import org.jetbrains.kotlinconf.NotificationSettings
import org.jetbrains.kotlinconf.SessionId
import org.jetbrains.kotlinconf.Theme
import org.jetbrains.kotlinconf.VoteInfo

interface ApplicationStorage {
    fun getUserId(): Flow<String?>
    suspend fun setUserId(value: String?)

    fun getPendingUserId(): Flow<String?>
    suspend fun setPendingUserId(value: String?)

    fun isOnboardingComplete(): Flow<Boolean>
    suspend fun setOnboardingComplete(value: Boolean)

    fun getTheme(): Flow<Theme>
    suspend fun setTheme(value: Theme)

    fun getConferenceCache(): Flow<Conference?>
    suspend fun setConferenceCache(value: Conference)

    fun getFavorites(): Flow<Set<SessionId>>
    suspend fun setFavorites(value: Set<SessionId>)

    fun getNews(): Flow<List<NewsItem>>
    suspend fun setNews(value: List<NewsItem>)

    fun getNotificationSettings(): Flow<NotificationSettings?>
    suspend fun setNotificationSettings(value: NotificationSettings)

    fun getVotes(): Flow<List<VoteInfo>>
    suspend fun setVotes(value: List<VoteInfo>)

    fun getFlagsBlocking(): Flags?
    fun getFlags(): Flow<Flags?>
    suspend fun setFlags(value: Flags)

    fun ensureCurrentVersion()
}
