package org.jetbrains.kotlinconf.presentation

import org.jetbrains.kotlinconf.*
import org.jetbrains.kotlinconf.data.*

interface DataRepository {
    val sessions: List<SessionModel>
    val favorites: List<SessionModel>
    val votes: List<Vote>

    fun getSessionById(id: String): SessionModel

    suspend fun update()

    /**
     * Rating
     */
    fun getRating(sessionId: String): SessionRating
    suspend fun addRating(sessionId: String, rating: SessionRating)
    suspend fun removeRating(sessionId: String)

    /**
     * Favorites
     */
    fun isFavorite(sessionId: String): Boolean
    suspend fun setFavorite(sessionId: String, isFavorite: Boolean)

    /**
     * Native callback API
     */
    fun update(callback: NativeCallback<Unit>)
    fun addRating(sessionId: String, rating: SessionRating, callback: NativeCallback<Unit>)
    fun removeRating(sessionId: String, callback: NativeCallback<Unit>)
    fun setFavorite(sessionId: String, isFavorite: Boolean, callback: NativeCallback<Unit>)
}
