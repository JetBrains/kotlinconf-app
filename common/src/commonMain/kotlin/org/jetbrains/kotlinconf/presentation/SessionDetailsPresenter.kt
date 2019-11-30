package org.jetbrains.kotlinconf.presentation

import kotlinx.coroutines.*
import org.jetbrains.kotlinconf.*
import org.jetbrains.kotlinconf.data.*
import kotlin.coroutines.*

class SessionDetailsPresenter(
    uiContext: CoroutineContext,
    private val view: SessionDetailsView,
    private val sessionId: String,
    private val repository: DataRepository
) : CoroutinePresenter(uiContext, view) {

    private lateinit var session: SessionModel
    private var rating: SessionRating? = null
    private val onRefreshListener: () -> Unit = this::refreshDataFromRepo

    fun onCreate() {
        refreshDataFromRepo()
        repository.onRefreshListeners += onRefreshListener
    }

    override fun onDestroy() {
        super.onDestroy()
        repository.onRefreshListeners -= onRefreshListener
    }

    fun rateSessionClicked(newRating: SessionRating) {
        launch {
            view.setRatingClickable(false)
            if (rating != newRating) {
                rating = newRating
                repository.addRating(sessionId, newRating)
            } else {
                rating = null
                repository.removeRating(sessionId)
            }
            view.setupRatingButtons(rating)
        }.invokeOnCompletion {
            view.setRatingClickable(true)
        }
    }

    fun onFavoriteButtonClicked() {
        launch {
            val isFavorite = isFavorite()
            repository.setFavorite(session.id, !isFavorite)
        }.invokeOnCompletion {
            refreshDataFromRepo()
        }
    }

    private fun refreshDataFromRepo() {
        session = repository.sessions?.firstOrNull { it.id == sessionId } ?: return
        view.updateView(isFavorite(), session)
        rating = repository.getRating(sessionId)
        view.setupRatingButtons(rating)
    }

    private fun isFavorite() =
        repository.favorites?.any { it.id == sessionId } ?: false
}
