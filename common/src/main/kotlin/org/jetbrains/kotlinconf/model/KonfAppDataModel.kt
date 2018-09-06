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
    var state = AllData()

    private val _sessions: MutableList<SessionModel> = mutableListOf()
    private val _favorites: MutableList<SessionModel> = mutableListOf()
    private val _votes: MutableList<Vote> = mutableListOf()

    override val sessions: List<SessionModel> get() = _sessions
    override val favorites: List<SessionModel> get() = _favorites
    override val votes: List<Vote> get() = _votes

    override fun getSessionById(id: String): SessionModel = SessionModel.forSession(state, id)

    override suspend fun update() {
        if (!signed) {
            api.createUser()
            signed = true
        }
        clear()

        state = api.getAll()
        _sessions.addAll(state.allSessions())
        _favorites.addAll(state.favoriteSessions())
        _votes.addAll(state.votes)
    }

    override fun getRating(sessionId: String): SessionRating =
        SessionRating.valueOf(state.votes.find { sessionId == it.sessionId }?.rating ?: 0)

    override suspend fun addRating(sessionId: String, rating: SessionRating) {
        val vote = Vote(sessionId, rating.value)
        api.postVote(vote)
        _votes.add(vote)
    }

    override suspend fun removeRating(sessionId: String) {
        api.postVote(Vote(sessionId, 0))
        _votes.removeAll { it.sessionId == sessionId }
    }

    override suspend fun setFavorite(sessionId: String, isFavorite: Boolean) {
        val favorite = Favorite(sessionId)
        if (isFavorite) {
            api.postFavorite(favorite)
            _favorites.add(getSessionById(sessionId))
        } else {
            api.deleteFavorite(favorite)
            _favorites.removeAll { it.id == sessionId }
        }
    }

    override fun isFavorite(sessionId: String): Boolean =
        state.favorites.contains(Favorite(sessionId))

    private fun clear() {
        _sessions.clear()
        _favorites.clear()
        _votes.clear()
    }
    /**
     * Native callback API
     */
    override fun update(callback: NativeCallback<Unit>) {
        wrapCallback(callback) {
            update()
        }
    }

    override fun addRating(sessionId: String, rating: SessionRating, callback: NativeCallback<Unit>) {
        wrapCallback(callback) {
            addRating(sessionId, rating)
        }
    }

    override fun removeRating(sessionId: String, callback: NativeCallback<Unit>) {
        wrapCallback(callback) {
            removeRating(sessionId)
        }
    }

    override fun setFavorite(sessionId: String, isFavorite: Boolean, callback: NativeCallback<Unit>) {
        wrapCallback(callback) {
            setFavorite(sessionId, isFavorite)
        }
    }
}
