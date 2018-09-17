package org.jetbrains.kotlinconf.presentation

import org.jetbrains.kotlinconf.*
import org.jetbrains.kotlinconf.data.*

interface DataRepository {
    val sessions: List<SessionModel>?
    val favorites: List<SessionModel>?
    val votes: List<Vote>?
    var onRefreshListeners: List<()->Unit>
    val loggedIn: Boolean

    suspend fun update()
    fun getRating(sessionId: String): SessionRating?
    suspend fun addRating(sessionId: String, rating: SessionRating)
    suspend fun removeRating(sessionId: String)
    suspend fun setFavorite(sessionId: String, isFavorite: Boolean)
    suspend fun verifyCode(code: VotingCode)
}
