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
    private val onRefreshListener: () -> Unit = this::showData

    fun onCreate() {
        searchQueryProvider.addOnQueryChangedListener(this::onSearchQueryChanged)
        repository.onRefreshListeners -= onRefreshListener
        updateData()
        showData()
    }

    fun onDestroy() {
        repository.onRefreshListeners -= onRefreshListener
    }

    fun showSessionDetails(session: SessionModel) {
        navigationManager.showSessionDetails(session.id)
    }

    fun updateData() {
        launch(uiContext) {
            try {
                view.isUpdating = isFirstDataLoading()
                repository.update()
                showData()
            } finally {
                view.isUpdating = false
            }
        }
    }

    private fun isFirstDataLoading() = repository.sessions == null

    private fun onSearchQueryChanged(query: String) {
        searchQuery = query
        showData()
    }

    private fun showData() {
        val displayedSessions = repository.sessions?.filter(searchQuery).orEmpty()
        val displayedFavorites = repository.favorites?.filter(searchQuery).orEmpty()
        view.onUpdate(displayedSessions, displayedFavorites)
    }
}