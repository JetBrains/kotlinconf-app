package org.jetbrains.kotlinconf.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import org.jetbrains.kotlinconf.ConferenceService
import org.jetbrains.kotlinconf.Score
import org.jetbrains.kotlinconf.SessionCardView
import org.jetbrains.kotlinconf.SessionId
import org.jetbrains.kotlinconf.Speaker
import org.jetbrains.kotlinconf.ui.components.Emotion

sealed class SessionUiState {
    data object Loading : SessionUiState()
    data object Error : SessionUiState()
    data class Content(val session: SessionCardView, val speakers: List<Speaker>) : SessionUiState()
}

class SessionViewModel(
    private val service: ConferenceService,
    private val sessionId: SessionId,
) : ViewModel() {

    private val _navigateToPrivacyPolicy = MutableStateFlow(false)
    val navigateToPrivacyPolicy: StateFlow<Boolean> = _navigateToPrivacyPolicy.asStateFlow()

    val uiState: StateFlow<SessionUiState> = combine(
        service.sessionByIdFlow(sessionId),
        service.speakersBySessionId(sessionId),
    ) { session, speakers ->
        when {
            session == null -> SessionUiState.Error
            else -> SessionUiState.Content(session, speakers)
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), SessionUiState.Loading)

    fun toggleFavorite(isBookmarked: Boolean) {
        viewModelScope.launch {
            service.setFavorite(sessionId, isBookmarked)
        }
    }

    fun submitFeedback(emotion: Emotion?) {
        viewModelScope.launch {
            if (service.canVote()) {
                service.vote(sessionId, emotion?.toScore())
            } else {
                _navigateToPrivacyPolicy.value = true
            }
        }
    }

    fun submitFeedbackWithComment(emotion: Emotion, comment: String) {
        viewModelScope.launch {
            if (service.canVote()) {
                service.vote(sessionId, emotion.toScore())
                service.sendFeedback(sessionId, comment)
            } else {
                _navigateToPrivacyPolicy.value = true
            }
        }
    }

    fun onNavigatedToPrivacyPolicy() {
        _navigateToPrivacyPolicy.value = false
    }

    private fun Emotion.toScore() = when (this) {
        Emotion.Positive -> Score.GOOD
        Emotion.Neutral -> Score.OK
        Emotion.Negative -> Score.BAD
    }
}
