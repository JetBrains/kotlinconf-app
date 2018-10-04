package org.jetbrains.kotlinconf.presentation

import org.jetbrains.kotlinconf.*
import org.jetbrains.kotlinconf.data.*
import kotlin.coroutines.*

class SessionDetailsPresenter(
    private val uiContext: CoroutineContext,
    private val view: SessionDetailsView,
    private val sessionId: String,
    private val repository: DataRepository
) {
    private lateinit var session: SessionModel
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
            val isFavorite = isFavorite()
            repository.setFavorite(session.id, !isFavorite)
        } finally {
            refreshDataFromRepo()
        }
    }

    private fun refreshDataFromRepo() {
        session = repository.sessions?.firstOrNull { it.id == sessionId } ?: return
        view.updateView(isFavorite(), session)
        rating = repository.getRating(sessionId)
        view.setupRatingButtons(rating)
        rating = repository.getRating(sessionId)
    }

    private fun isFavorite() =
        repository.favorites?.any { it.id == sessionId } ?: false
}
