package org.jetbrains.kotlinconf.presentation

import kotlinx.coroutines.experimental.*
import org.jetbrains.kotlinconf.*
import org.jetbrains.kotlinconf.model.*
import kotlin.coroutines.experimental.*
import kotlin.properties.Delegates.observable

class SessionDetailsPresenter(
    private val uiContext: CoroutineContext,
    private val view: SessionDetailsView,
    private val sessionId: String,
    private val repository: KotlinConfDataRepository
) {
    private lateinit var session: SessionModel
    private var isFavorite: Boolean by observable(false) { _, _, isFavorite ->
        view.setIsFavorite(isFavorite)
    }
    private var rating: SessionRating? = null
    private val onRefreshListener: () -> Unit = this::refreshDataFromRepo

    fun onCreate() {
        refreshDataFromRepo()
        repository.onUpdateListeners += onRefreshListener
    }

    fun onDestroy() {
        repository.onUpdateListeners -= onRefreshListener
    }

    private fun refreshDataFromRepo() {
        session = repository.getSessionById(sessionId)!!
        view.updateView(session)
        isFavorite = repository.isFavorite(sessionId)
        rating = repository.getRating(sessionId)
    }

    fun rateSession(newRating: SessionRating) {
        launch(uiContext) {
            view.setRatingClickable(false)
            if (rating != newRating) {
                rating = newRating
                repository.addRating(sessionId, newRating)
            } else {
                rating = null
                repository.removeRating(sessionId)
            }
            view.setupRatingButtons(rating)
            view.setRatingClickable(true)
        }
    }

    fun onFavoriteButtonClicked() {
        launch(uiContext) {
            isFavorite = !isFavorite
            repository.setFavorite(session.id, isFavorite)
        }
    }
}