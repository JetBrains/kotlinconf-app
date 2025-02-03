package org.jetbrains.kotlinconf.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import org.jetbrains.kotlinconf.ConferenceService
import org.jetbrains.kotlinconf.Score
import org.jetbrains.kotlinconf.SessionCardView
import org.jetbrains.kotlinconf.SessionId
import org.jetbrains.kotlinconf.Speaker
import org.jetbrains.kotlinconf.ui.components.Emotion

class SessionViewModel(
    private val service: ConferenceService,
    private val sessionId: SessionId,
) : ViewModel() {

    val session: StateFlow<SessionCardView?> = service.sessionByIdFlow(sessionId)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val speakers: StateFlow<List<Speaker>> = service.speakersBySessionId(sessionId)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun toggleFavorite(isBookmarked: Boolean) {
        viewModelScope.launch {
            service.toggleFavorite(sessionId, isBookmarked)
        }
    }

    fun submitFeedback(emotion: Emotion?) {
        viewModelScope.launch {
            service.vote(sessionId, emotion?.toScore())
        }
    }

    fun submitFeedbackWithComment(emotion: Emotion, comment: String) {
        viewModelScope.launch {
            service.vote(sessionId, emotion.toScore())
            service.sendFeedback(sessionId, comment)
        }
    }

    private fun Emotion.toScore() = when (this) {
        Emotion.Positive -> Score.GOOD
        Emotion.Neutral -> Score.OK
        Emotion.Negative -> Score.BAD
    }
}
