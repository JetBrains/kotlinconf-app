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
import org.jetbrains.kotlinconf.utils.ErrorLoadingState
import org.koin.core.annotation.InjectedParam
import org.koin.core.annotation.KoinViewModel

@KoinViewModel
class SessionViewModel(
    private val service: ConferenceService,
    @InjectedParam private val sessionId: SessionId,
) : ViewModel() {

    val session: StateFlow<ErrorLoadingState<SessionCardView>> = service.sessionByIdFlow(sessionId)
        .map { session ->
            if (session != null) ErrorLoadingState.Content(session)
            else ErrorLoadingState.Error
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), ErrorLoadingState.Loading)

    val speakers: StateFlow<List<Speaker>> = service.speakersBySessionId(sessionId)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun toggleFavorite(isBookmarked: Boolean) {
        viewModelScope.launch {
            service.setFavorite(sessionId, isBookmarked)
        }
    }
}
