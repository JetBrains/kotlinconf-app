package org.jetbrains.kotlinconf.ui

import android.app.*
import android.arch.lifecycle.*
import org.jetbrains.anko.*
import org.jetbrains.kotlinconf.*
import org.jetbrains.kotlinconf.model.*
import org.jetbrains.kotlinconf.presentation.*

class SessionListViewModel(app: Application) : AndroidViewModel(app), AnkoLogger {
    private val viewModel: KotlinConfViewModel = getApplication<KotlinConfApplication>().viewModel

    private lateinit var navigationManager: NavigationManager

    private var _searchQuery: String = ""

    private val _sessions = MediatorLiveData<List<SessionModel>>().apply {
        addSource(viewModel.sessions) { sessions -> value = sessions?.filter(_searchQuery) }
    }

    val sessions: LiveData<List<SessionModel>> = _sessions

    private val _favorites = MediatorLiveData<List<SessionModel>>().apply {
        addSource(viewModel.favorites) { sessions -> value = sessions?.filter(_searchQuery) }
    }

    val favorites: LiveData<List<SessionModel>> = _favorites

    val isUpdating: LiveData<Boolean> = viewModel.isUpdating

    fun showSessionDetails(session: SessionModel) {
        navigationManager.showSessionDetails(session.id)
    }

    suspend fun updateData() {
        viewModel.update()
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
                    || session.speakers.any { speaker -> speaker.fullName.toLowerCase().contains(searchQuery) }
        }
    }

    private fun onSearchQueryChanged(query: String) {
        _searchQuery = query
        _sessions.value = viewModel.sessions.value?.filter(query)
        _favorites.value = viewModel.favorites.value?.filter(query)
    }
}