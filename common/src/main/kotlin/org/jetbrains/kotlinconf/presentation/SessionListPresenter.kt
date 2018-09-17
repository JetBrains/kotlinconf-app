package org.jetbrains.kotlinconf.presentation

import org.jetbrains.kotlinconf.*
import kotlin.coroutines.*

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
        view.isUpdating = isFirstDataLoading()

        showData()
        updateData()

        if (!repository.loggedIn && !repository.codePromptShown) {
            navigationManager.showVotingCodePromptDialog()
            repository.codePromptShown = true
        }
    }

    fun onDestroy() {
        repository.onRefreshListeners -= onRefreshListener
    }

    fun showSessionDetails(session: SessionModel) {
        navigationManager.showSessionDetails(session.id)
    }

    fun onPullRefresh() {
        updateData()
    }

    fun updateData() {
        launchAndCatch(uiContext, view::showError) {
            repository.update()
            showData()
        } finally {
            view.isUpdating = false
        }
    }

    fun showData() {
        val displayedSessions = repository.sessions?.filter(searchQuery).orEmpty()
        val displayedFavorites = repository.favorites?.filter(searchQuery).orEmpty()
        view.onUpdate(displayedSessions, displayedFavorites)
    }

    private fun isFirstDataLoading() = repository.sessions == null

    private fun onSearchQueryChanged(query: String) {
        searchQuery = query
        showData()
    }
}