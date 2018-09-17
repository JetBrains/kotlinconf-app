package org.jetbrains.kotlinconf.presentation

import org.jetbrains.kotlinconf.*
import org.jetbrains.kotlinconf.data.*
import kotlin.coroutines.*
import kotlin.properties.Delegates.observable

class SessionDetailsPresenter(
    private val uiContext: CoroutineContext,
    private val view: SessionDetailsView,
    private val sessionId: String,
    private val repository: DataRepository
) {
    private lateinit var session: SessionModel
    private var isFavorite: Boolean by observable(false) { _, _, isFavorite ->
        view.setIsFavorite(isFavorite)
    }
    private var rating: SessionRating? = null
    private val onRefreshListener: () -> Unit = this::refreshDataFromRepo

    fun onCreate() {
        refreshDataFromRepo()
        repository.onRefreshListeners += onRefreshListener
    }

    fun onDestroy() {
        repository.onRefreshListeners -= onRefreshListener
    }

    fun rateSessionClicked(newRating: SessionRating) {
        launchAndCatch(uiContext, view::showError) {
            view.setRatingClickable(false)
            if (rating != newRating) {
                rating = newRating
                repository.addRating(sessionId, newRating)
            } else {
                rating = null
                repository.removeRating(sessionId)
            }
            view.setupRatingButtons(rating)
        } finally {
            view.setRatingClickable(true)
        }
    }

    fun onFavoriteButtonClicked() {
        launchAndCatch(uiContext, view::showError) {
            isFavorite = !isFavorite
            repository.setFavorite(session.id, isFavorite)
        }
    }

    private fun refreshDataFromRepo() {
        session = repository.sessions?.firstOrNull { it.id == sessionId } ?: return
        view.updateView(repository.loggedIn, session)
        rating = repository.getRating(sessionId)
        view.setupRatingButtons(rating)
        isFavorite = repository.favorites?.any { it.id == sessionId } ?: false
        rating = repository.getRating(sessionId)
    }
}
