package org.jetbrains.kotlinconf.ui

import android.app.*
import android.arch.lifecycle.*
import org.jetbrains.anko.*
import org.jetbrains.kotlinconf.*
import org.jetbrains.kotlinconf.data.*
import org.jetbrains.kotlinconf.model.*

class SessionDetailsViewModel(app: Application) : AndroidViewModel(app), AnkoLogger {
    private val viewModel: KotlinConfViewModel = getApplication<KotlinConfApplication>().viewModel

    private val _session: MutableLiveData<SessionModel> = MutableLiveData()

    val session: LiveData<SessionModel> = _session

    val isFavorite: LiveData<Boolean> = MediatorLiveData<Boolean>().apply {
        addSource(session) { session ->
            value = viewModel.favorites.value?.contains(session)
        }

        addSource(viewModel.favorites) { favorites ->
            value = favorites?.contains(session.value)
        }
    }

    val rating: LiveData<SessionRating?> = MediatorLiveData<SessionRating?>().apply {
        addSource(session) { session ->
            value = session?.id?.let { viewModel.ratings.value?.get(it) }
        }

        addSource(viewModel.ratings) { ratings ->
            value = session.value?.id?.let { ratings?.get(it) }
        }
    }

    suspend fun toggleFavorite() {
        val isFavorite = !(isFavorite.value ?: false)
        _session.value?.id?.let { viewModel.setFavorite(it, isFavorite) }
    }

    suspend fun setRating(rating: SessionRating) {
        _session.value?.id?.let { sessionId -> viewModel.addRating(sessionId, rating) }
    }

    suspend fun removeRating() {
        _session.value?.id?.let { sessionId -> viewModel.removeRating(sessionId) }
    }

    fun setSession(sessionId: String) {
        _session.value = viewModel.sessions.value?.find { it.id == sessionId }
    }
}