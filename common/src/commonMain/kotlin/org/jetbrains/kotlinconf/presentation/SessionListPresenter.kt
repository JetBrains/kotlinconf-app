package org.jetbrains.kotlinconf.presentation

import kotlinx.coroutines.*
import org.jetbrains.kotlinconf.*
import kotlin.coroutines.*

class SessionListPresenter(
    private val uiContext: CoroutineContext,
    private val view: SessionListView,
    private val repository: DataRepository,
    private val navigationManager: NavigationManager,
    private val searchQueryProvider: SearchQueryProvider
) : CoroutinePresenter(uiContext, view) {

    private var searchQuery: String = searchQueryProvider.searchQuery
    private val onRefreshListener: () -> Unit = this::showData

    fun onCreate() {
        searchQueryProvider.addOnQueryChangedListener(this::onSearchQueryChanged)
        repository.onRefreshListeners -= onRefreshListener
        view.isUpdating = isFirstDataLoading()

        showData()
        updateData()
    }

    override fun onDestroy() {
        super.onDestroy()
        repository.onRefreshListeners -= onRefreshListener
    }

    fun showSessionDetails(session: SessionModel) {
        navigationManager.showSessionDetails(session.id)
    }

    fun onPullRefresh() {
        updateData()
    }

    fun showData() {
        val displayedSessions = repository.sessions.orEmpty()
            .filter(searchQuery)
        val displayedFavorites = repository.favorites.orEmpty()
            .filter(searchQuery)
        view.onUpdate(displayedSessions, displayedFavorites)
    }

    private fun updateData() {
        launch {
            repository.update()
            showData()
        }.invokeOnCompletion {
            view.isUpdating = false
        }
    }

    private fun isFirstDataLoading() = repository.sessions == null

    private fun onSearchQueryChanged(query: String) {
        searchQuery = query
        showData()
    }
}