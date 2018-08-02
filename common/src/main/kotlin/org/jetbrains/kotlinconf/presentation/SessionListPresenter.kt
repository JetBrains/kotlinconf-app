package org.jetbrains.kotlinconf.presentation

import kotlinx.coroutines.experimental.*
import org.jetbrains.kotlinconf.*
import kotlin.coroutines.experimental.*

class SessionListPresenter(
    private val uiContext: CoroutineContext,
    private val view: SessionListView,
    private val repository: KotlinConfDataRepository,
    private val navigationManager: NavigationManager,
    private val searchQueryProvider: SearchQueryProvider

) {
    private val onRefreshListener: () -> Unit = this::refreshDataFromRepo

    private var searchQuery: String = searchQueryProvider.searchQuery

    fun onCreate() {
        repository.onUpdateListeners += onRefreshListener
        searchQueryProvider.addOnQueryChangedListener(this::onSearchQueryChanged)
        updateData()
    }

    fun onDestroy() {
        repository.onUpdateListeners -= onRefreshListener
    }

    fun showSessionDetails(session: SessionModel) {
        navigationManager.showSessionDetails(session.id)
    }

    fun updateData() {
        launch(uiContext) {
            view.isUpdating = true
            repository.update()
            view.isUpdating = false
        }
    }

    private fun refreshDataFromRepo() {
        val displayedSessions = repository.sessions.filter(searchQuery)
        val displayedFavorites = repository.favorites.filter(searchQuery)
        view.onUpdate(displayedSessions, displayedFavorites)
    }

    private fun onSearchQueryChanged(query: String) {
        searchQuery = query
        refreshDataFromRepo()
    }
}

private fun List<SessionModel>.filter(searchQuery: String?): List<SessionModel> {
    searchQuery?.takeUnless { it.isEmpty() } ?: return this
    val searchQueryLower = searchQuery.toLowerCase()
    return filter { session ->
        searchQueryLower in session.title.toLowerCase() || session.speakers.mapNotNull { it.fullName }.any { fullName ->
            searchQueryLower in fullName.toLowerCase()
        }
    }
}