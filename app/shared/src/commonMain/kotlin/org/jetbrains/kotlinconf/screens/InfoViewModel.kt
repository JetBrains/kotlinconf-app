package org.jetbrains.kotlinconf.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import org.jetbrains.kotlinconf.ConferenceImages
import org.jetbrains.kotlinconf.ConferenceService
import org.koin.core.annotation.KoinViewModel

@KoinViewModel
class InfoViewModel(
    service: ConferenceService,
) : ViewModel() {
    val venueAddress: StateFlow<String?> = service.conferenceInfo
        .map { it?.mapData?.venueAddress }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val images: StateFlow<ConferenceImages?> = service.conferenceInfo
        .map { it?.images }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)
}
