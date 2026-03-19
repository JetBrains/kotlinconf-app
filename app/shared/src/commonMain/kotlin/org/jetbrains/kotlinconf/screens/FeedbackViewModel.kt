package org.jetbrains.kotlinconf.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.Assisted
import dev.zacsweers.metro.AssistedFactory
import dev.zacsweers.metro.AssistedInject
import dev.zacsweers.metro.ContributesIntoMap
import dev.zacsweers.metrox.viewmodel.ManualViewModelAssistedFactory
import dev.zacsweers.metrox.viewmodel.ManualViewModelAssistedFactoryKey
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.jetbrains.kotlinconf.ConferenceService
import org.jetbrains.kotlinconf.SessionId
import org.jetbrains.kotlinconf.ui.components.Emotion
import org.jetbrains.kotlinconf.utils.toScore

@AssistedInject
class FeedbackViewModel(
    private val service: ConferenceService,
    @Assisted private val sessionId: SessionId,
    @Assisted initialEmotion: Emotion?,
) : ViewModel() {

    private val _selectedEmotion = MutableStateFlow(initialEmotion)
    val selectedEmotion: StateFlow<Emotion?> = _selectedEmotion.asStateFlow()

    private val _feedbackExpanded = MutableStateFlow(false)
    val feedbackExpanded: StateFlow<Boolean> = _feedbackExpanded.asStateFlow()

    private val _navigateToPrivacyNotice = MutableStateFlow(false)
    val navigateToPrivacyNotice: StateFlow<Boolean> = _navigateToPrivacyNotice.asStateFlow()

    private val _feedbackSent = MutableStateFlow(false)
    val feedbackSent: StateFlow<Boolean> = _feedbackSent.asStateFlow()

    fun selectEmotion(emotion: Emotion) {
        val newEmotion = if (emotion == _selectedEmotion.value) null else emotion
        _selectedEmotion.value = newEmotion
        _feedbackExpanded.value = newEmotion != null
        submitVote(newEmotion)
    }

    fun submitFeedbackWithComment(comment: String) {
        val emotion = _selectedEmotion.value ?: return
        viewModelScope.launch {
            if (service.isPolicySigned()) {
                service.vote(sessionId, emotion.toScore())
                service.sendFeedback(sessionId, comment)
                _feedbackExpanded.value = false
                _feedbackSent.value = true
            } else {
                _navigateToPrivacyNotice.value = true
            }
        }
    }

    fun skipComment() {
        _feedbackExpanded.value = false
    }

    fun onNavigatedToPrivacyNotice() {
        _navigateToPrivacyNotice.value = false
    }

    fun onFeedbackSentHandled() {
        _feedbackSent.value = false
    }

    private fun submitVote(emotion: Emotion?) {
        viewModelScope.launch {
            if (service.isPolicySigned()) {
                service.vote(sessionId, emotion?.toScore())
            } else {
                _navigateToPrivacyNotice.value = true
            }
        }
    }

    @AssistedFactory
    @ManualViewModelAssistedFactoryKey(Factory::class)
    @ContributesIntoMap(AppScope::class)
    fun interface Factory : ManualViewModelAssistedFactory {
        fun create(sessionId: SessionId, initialEmotion: Emotion?): FeedbackViewModel
    }
}
