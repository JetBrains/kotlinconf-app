package org.jetbrains.kotlinconf.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
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

    private val _navigateToPrivacyNotice = MutableStateFlow(false)
    val navigateToPrivacyNotice: StateFlow<Boolean> = _navigateToPrivacyNotice.asStateFlow()

    val session: StateFlow<SessionCardView?> = service.sessionByIdFlow(sessionId)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val speakers: StateFlow<List<Speaker>> = service.speakersBySessionId(sessionId)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val userSignedIn: StateFlow<Boolean> = service.userId
        .map { it != null }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

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
                _navigateToPrivacyNotice.value = true
            }
        }
    }

    fun submitFeedbackWithComment(emotion: Emotion, comment: String) {
        viewModelScope.launch {
            if (service.canVote()) {
                service.vote(sessionId, emotion.toScore())
                service.sendFeedback(sessionId, comment)
            } else {
                _navigateToPrivacyNotice.value = true
            }
        }
    }

    fun onNavigatedToPrivacyNotice() {
        _navigateToPrivacyNotice.value = false
    }

    private fun Emotion.toScore() = when (this) {
        Emotion.Positive -> Score.GOOD
        Emotion.Neutral -> Score.OK
        Emotion.Negative -> Score.BAD
    }
}
