package org.jetbrains.kotlinconf.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import org.jetbrains.kotlinconf.ConferenceService
import org.jetbrains.kotlinconf.SessionCardView
import org.jetbrains.kotlinconf.SessionId
import org.jetbrains.kotlinconf.Speaker
import org.jetbrains.kotlinconf.SpeakerId
import org.jetbrains.kotlinconf.utils.ErrorLoadingState
import org.koin.core.annotation.InjectedParam
import org.koin.core.annotation.KoinViewModel

@KoinViewModel
class SpeakerDetailViewModel(
    private val service: ConferenceService,
    @InjectedParam speakerId: SpeakerId,
) : ViewModel() {
    fun onBookmark(sessionId: SessionId, bookmarked: Boolean) {
        viewModelScope.launch {
            service.setFavorite(sessionId, bookmarked)
        }
    }

    val speaker: StateFlow<ErrorLoadingState<Speaker>> = service.speakerByIdFlow(speakerId)
        .map { speaker ->
            if (speaker != null) ErrorLoadingState.Content(speaker)
            else ErrorLoadingState.Error
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), ErrorLoadingState.Loading)

    val sessions: StateFlow<List<SessionCardView>> = service.sessionsForSpeakerFlow(speakerId)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
}
