package org.jetbrains.kotlinconf.presentation

import org.jetbrains.kotlinconf.*
import org.jetbrains.kotlinconf.model.*

interface SessionDetailsView {
    fun updateView(session: SessionModel)
    fun setupRatingButtons(rating: SessionRating?)
    fun setIsFavorite(isFavorite: Boolean)
    fun setRatingClickable(clickable: Boolean)
}