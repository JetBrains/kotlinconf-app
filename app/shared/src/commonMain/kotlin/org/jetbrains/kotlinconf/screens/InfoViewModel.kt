package org.jetbrains.kotlinconf.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesIntoMap
import dev.zacsweers.metrox.viewmodel.ViewModelKey
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import org.jetbrains.kotlinconf.ConferenceService

@ContributesIntoMap(AppScope::class)
@ViewModelKey(InfoViewModel::class)
class InfoViewModel(
    service: ConferenceService,
) : ViewModel() {
    val venueAddress: StateFlow<String?> = service.conferenceInfo
        .map { it?.mapData?.venueAddress }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)
}
