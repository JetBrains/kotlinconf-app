package org.jetbrains.kotlinconf.ui

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MediatorLiveData
import android.arch.lifecycle.MutableLiveData
import org.jetbrains.kotlinconf.KotlinConfApplication
import org.jetbrains.kotlinconf.SessionModel
import org.jetbrains.kotlinconf.model.KotlinConfDataRepository
import org.jetbrains.kotlinconf.model.SessionRating
import org.jetbrains.anko.AnkoLogger

class SessionDetailsViewModel(app: Application): AndroidViewModel(app), AnkoLogger {
    private val repository: KotlinConfDataRepository =
            getApplication<KotlinConfApplication>().repository

    private val _session: MutableLiveData<SessionModel> = MutableLiveData()
    val session: LiveData<SessionModel> = _session

    val isFavorite: LiveData<Boolean> = MediatorLiveData<Boolean>().apply {
        addSource(session) { session ->
            value = repository.favorites.value?.contains(session)
        }

        addSource(repository.favorites) { favorites ->
            value = favorites?.contains(session.value)
        }
    }

    val rating: LiveData<SessionRating?> = MediatorLiveData<SessionRating?>().apply {
        addSource(session) { session ->
            value = session?.id?.let { repository.ratings.value?.get(it) }
        }

        addSource(repository.ratings) { ratings ->
            value = session.value?.id?.let { ratings?.get(it) }
        }
    }

    suspend fun toggleFavorite() {
        val isFavorite = !(isFavorite.value ?: false)
        _session.value?.id?.let { repository.setFavorite(it, isFavorite) }
    }

    suspend fun setRating(rating: SessionRating) {
        _session.value?.id?.let { sessionId -> repository.addRating(sessionId, rating) }
    }

    suspend fun removeRating() {
        _session.value?.id?.let { sessionId -> repository.removeRating(sessionId) }
    }

    fun setSession(sessionId: String) {
        this._session.value = repository.sessions.value?.find { it.id == sessionId }
    }
}