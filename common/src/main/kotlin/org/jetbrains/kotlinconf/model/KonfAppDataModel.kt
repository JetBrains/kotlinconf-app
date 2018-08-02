package org.jetbrains.kotlinconf.model

import org.jetbrains.kotlinconf.*
import org.jetbrains.kotlinconf.api.*
import org.jetbrains.kotlinconf.data.*
import org.jetbrains.kotlinconf.presentation.*

class KonfAppDataModel(
    user: String
) : DataRepository {
    private val api = KotlinConfApi(user)
    private var signed: Boolean = false
    private var state = AllData()

    override var sessions: List<SessionModel> = listOf()
        private set

    override var favorites: List<SessionModel> = listOf()
        private set

    override var votes: List<Vote> = listOf()
        private set

    override fun getSessionById(id: String): SessionModel = SessionModel.forSession(state, id)

    override suspend fun update() {
        if (!signed) {
            api.createUser()
            signed = true
        }

        state = api.getAll()
        sessions = state.allSessions()
        favorites = state.favoriteSessions()
        votes = state.votes
    }

    override fun getRating(sessionId: String): SessionRating =
        SessionRating.valueOf(state.votes.find { sessionId == it.sessionId }?.rating ?: 0)

    override suspend fun addRating(sessionId: String, rating: SessionRating) {
        api.postVote(Vote(sessionId, rating.value))
    }

    override suspend fun removeRating(sessionId: String) {
        api.postVote(Vote(sessionId, 0))
    }

    override suspend fun setFavorite(sessionId: String, isFavorite: Boolean) {
        val favorite = Favorite(sessionId)
        if (isFavorite) api.postFavorite(favorite) else api.deleteFavorite(favorite)
    }

    override fun isFavorite(sessionId: String): Boolean =
        state.favorites.contains(Favorite(sessionId))
}