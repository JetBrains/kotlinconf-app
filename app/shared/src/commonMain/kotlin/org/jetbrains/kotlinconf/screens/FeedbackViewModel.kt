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
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import org.jetbrains.kotlinconf.ConferenceService
import org.jetbrains.kotlinconf.SessionId
import org.jetbrains.kotlinconf.toEmotion
import org.jetbrains.kotlinconf.ui.components.Emotion
import org.jetbrains.kotlinconf.utils.toScore

@AssistedInject
class FeedbackViewModel(
    private val service: ConferenceService,
    @Assisted private val sessionId: SessionId,
) : ViewModel() {

    val selectedEmotion: StateFlow<Emotion?> = service.votes
        .map { vote -> vote.find { it.sessionId == sessionId }?.score?.toEmotion() }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val feedbackExpanded: StateFlow<Boolean>
        field = MutableStateFlow(false)

    val navigateToPrivacyNotice: StateFlow<Boolean>
        field = MutableStateFlow(false)

    private val pendingEmotion = MutableStateFlow<Emotion?>(null)

    val feedbackSent: StateFlow<Boolean>
        field = MutableStateFlow(false)

    fun selectEmotion(emotion: Emotion) {
        val newEmotion = if (emotion == selectedEmotion.value) null else emotion

        viewModelScope.launch {
            if (service.isPolicySigned()) {
                service.vote(sessionId, newEmotion?.toScore())
                feedbackExpanded.value = newEmotion != null
            } else {
                pendingEmotion.value = newEmotion
                navigateToPrivacyNotice.value = true
            }
        }
    }

    fun submitFeedbackWithComment(comment: String) {
        viewModelScope.launch {
            if (service.isPolicySigned()) {
                service.sendFeedback(sessionId, comment)
                feedbackExpanded.value = false
                feedbackSent.value = true
            }
        }
    }

    fun skipComment() {
        feedbackExpanded.value = false
    }

    fun onNavigatedToPrivacyNotice() {
        navigateToPrivacyNotice.value = false
    }

    fun onReturnedFromPrivacyNotice() {
        val newEmotion = pendingEmotion.value ?: return
        pendingEmotion.value = null
        viewModelScope.launch {
            if (service.isPolicySigned()) {
                service.vote(sessionId, newEmotion.toScore())
                feedbackExpanded.value = true
            } else {
                feedbackExpanded.value = false
            }
        }
    }

    fun onFeedbackSentHandled() {
        feedbackSent.value = false
    }

    @AssistedFactory
    @ManualViewModelAssistedFactoryKey
    @ContributesIntoMap(AppScope::class)
    fun interface Factory : ManualViewModelAssistedFactory {
        fun create(sessionId: SessionId): FeedbackViewModel
    }
}
