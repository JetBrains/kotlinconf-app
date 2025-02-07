package org.jetbrains.kotlinconf.storage

import kotlinx.coroutines.flow.Flow
import org.jetbrains.kotlinconf.Conference
import org.jetbrains.kotlinconf.NewsItem
import org.jetbrains.kotlinconf.SessionId
import org.jetbrains.kotlinconf.Theme

interface ApplicationStorage {
    fun getUserId(): Flow<String?>
    suspend fun setUserId(value: String?)

    fun isOnboardingComplete(): Flow<Boolean>
    suspend fun setOnboardingComplete(value: Boolean)

    fun getNotificationsAllowed(): Flow<Boolean>
    suspend fun setNotificationsAllowed(value: Boolean)

    fun getTheme(): Flow<Theme>
    suspend fun setTheme(value: Theme)

    fun getConferenceCache(): Flow<Conference>
    suspend fun setConferenceCache(value: Conference)

    fun getFavorites(): Flow<Set<SessionId>>
    suspend fun setFavorites(value: Set<SessionId>)

    fun getNews(): Flow<List<NewsItem>>
    suspend fun setNews(value: List<NewsItem>)
}
