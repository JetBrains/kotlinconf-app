package com.jetbrains.kotlinconf.presentation

import org.jetbrains.kotlinconf.SessionModel
import org.jetbrains.kotlinconf.data.Session
import org.jetbrains.kotlinconf.model.SessionRating

interface KotlinConfDataRepository {
    var onUpdateListeners: List<()->Unit>
    val sessions: List<SessionModel>
    val favorites: List<SessionModel>
    suspend fun update()
    fun getSessionById(id: String): SessionModel?
    suspend fun addRating(sessionId: String, rating: SessionRating)
    suspend fun removeRating(sessionId: String)
    suspend fun setFavorite(sessionId: String, isFavorite: Boolean)
    fun isFavorite(sessionId: String): Boolean
    fun getRating(sessionId: String): SessionRating?
}