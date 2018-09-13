package org.jetbrains.kotlinconf.presentation

import kotlinx.coroutines.launch
import org.jetbrains.kotlinconf.*
import kotlin.coroutines.CoroutineContext

class SessionListPresenter(
        private val uiContext: CoroutineContext,
        private val view: SessionListView,
        private val repository: DataRepository,
        private val navigationManager: NavigationManager,
        private val searchQueryProvider: SearchQueryProvider

) {
    private var searchQuery: String = searchQueryProvider.searchQuery

    fun onCreate() {
        searchQueryProvider.addOnQueryChangedListener(this::onSearchQueryChanged)
        updateData()
    }

    fun showSessionDetails(session: SessionModel) {
        navigationManager.showSessionDetails(session.id)
    }

    fun updateData() {
        launch(uiContext) {
            try {
                view.isUpdating = true
                repository.update()
            } finally {
                view.isUpdating = false
            }
        }
    }

    private fun onSearchQueryChanged(query: String) {
        searchQuery = query
        val displayedSessions = repository.sessions.filter(searchQuery)
        val displayedFavorites = repository.favorites.filter(searchQuery)
        view.onUpdate(displayedSessions, displayedFavorites)
    }
}