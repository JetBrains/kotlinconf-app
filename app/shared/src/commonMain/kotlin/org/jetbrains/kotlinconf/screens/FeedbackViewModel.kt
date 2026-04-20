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
import org.jetbrains.kotlinconf.SessionId
import org.jetbrains.kotlinconf.toEmotion
import org.jetbrains.kotlinconf.ui.components.Emotion
import org.jetbrains.kotlinconf.utils.toScore
import org.koin.core.annotation.InjectedParam
import org.koin.core.annotation.KoinViewModel

@KoinViewModel
class FeedbackViewModel(
    private val service: ConferenceService,
    @InjectedParam private val sessionId: SessionId,
) : ViewModel() {

    val selectedEmotion: StateFlow<Emotion?> = service.votes
        .map { vote -> vote.find { it.sessionId == sessionId }?.score?.toEmotion() }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    private val _feedbackExpanded = MutableStateFlow(false)
    val feedbackExpanded: StateFlow<Boolean> = _feedbackExpanded.asStateFlow()

    private val _navigateToPrivacyNotice = MutableStateFlow(false)
    val navigateToPrivacyNotice: StateFlow<Boolean> = _navigateToPrivacyNotice.asStateFlow()

    private val pendingEmotion = MutableStateFlow<Emotion?>(null)

    private val _feedbackSent = MutableStateFlow(false)
    val feedbackSent: StateFlow<Boolean> = _feedbackSent.asStateFlow()

    fun selectEmotion(emotion: Emotion) {
        val newEmotion = if (emotion == selectedEmotion.value) null else emotion

        viewModelScope.launch {
            if (service.isPolicySigned()) {
                service.vote(sessionId, newEmotion?.toScore())
                _feedbackExpanded.value = newEmotion != null
            } else {
                pendingEmotion.value = newEmotion
                _navigateToPrivacyNotice.value = true
            }
        }
    }

    fun submitFeedbackWithComment(comment: String) {
        viewModelScope.launch {
            if (service.isPolicySigned()) {
                service.sendFeedback(sessionId, comment)
                _feedbackExpanded.value = false
                _feedbackSent.value = true
            }
        }
    }

    fun skipComment() {
        _feedbackExpanded.value = false
    }

    fun onNavigatedToPrivacyNotice() {
        _navigateToPrivacyNotice.value = false
    }

    fun onReturnedFromPrivacyNotice() {
        val newEmotion = pendingEmotion.value ?: return
        pendingEmotion.value = null
        viewModelScope.launch {
            if (service.isPolicySigned()) {
                service.vote(sessionId, newEmotion.toScore())
                _feedbackExpanded.value = true
            } else {
                _feedbackExpanded.value = false
            }
        }
    }

    fun onFeedbackSentHandled() {
        _feedbackSent.value = false
    }
}
