package org.jetbrains.kotlinconf.ui

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MediatorLiveData
import org.jetbrains.kotlinconf.KotlinConfApplication
import org.jetbrains.kotlinconf.SessionModel
import org.jetbrains.kotlinconf.model.KotlinConfDataRepository
import org.jetbrains.anko.AnkoLogger

class SessionListViewModel(app: Application) : AndroidViewModel(app), AnkoLogger {
    private val repository: KotlinConfDataRepository =
            getApplication<KotlinConfApplication>().repository

    private lateinit var navigationManager: NavigationManager

    private var _searchQuery: String = ""

    private val _sessions = MediatorLiveData<List<SessionModel>>().apply {
        addSource(repository.sessions) { sessions -> value = sessions?.filter(_searchQuery) }
    }

    val sessions: LiveData<List<SessionModel>> = _sessions

    private val _favorites = MediatorLiveData<List<SessionModel>>().apply {
        addSource(repository.favorites) { sessions -> value = sessions?.filter(_searchQuery) }
    }

    val favorites: LiveData<List<SessionModel>> = _favorites

    val isUpdating: LiveData<Boolean> = repository.isUpdating

    fun showSessionDetails(session: SessionModel) {
        navigationManager.showSessionDetails(session.id)
    }

    suspend fun updateData() {
        repository.update()
    }

    fun setNavigationManager(navigationManager: NavigationManager) {
        this.navigationManager = navigationManager
    }

    fun setSearchQueryProvider(searchQueryProvider: SearchQueryProvider) {
        _searchQuery = searchQueryProvider.searchQuery
        searchQueryProvider.addOnQueryChangedListener(this::onSearchQueryChanged)
    }

    private fun List<SessionModel>.filter(searchQuery: String?): List<SessionModel> {
        if (searchQuery == null || searchQuery.isEmpty()) {
            return this
        }

        return filter { session ->
            session.title.toLowerCase().contains(searchQuery)
                    || session.speakers.any { speaker -> (speaker.fullName ?: "").toLowerCase().contains(searchQuery) }
        }
    }

    private fun onSearchQueryChanged(query: String) {
        _searchQuery = query
        _sessions.value = repository.sessions.value?.filter(query)
        _favorites.value = repository.favorites.value?.filter(query)
    }
}