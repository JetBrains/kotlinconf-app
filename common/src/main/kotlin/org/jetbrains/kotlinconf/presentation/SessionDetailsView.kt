package org.jetbrains.kotlinconf.presentation

import org.jetbrains.kotlinconf.*
import org.jetbrains.kotlinconf.data.*

interface SessionDetailsView : BaseView {
    fun updateView(loggedIn: Boolean, session: SessionModel)
    fun setupRatingButtons(rating: SessionRating?)
    fun setIsFavorite(isFavorite: Boolean)
    fun setRatingClickable(clickable: Boolean)
}
