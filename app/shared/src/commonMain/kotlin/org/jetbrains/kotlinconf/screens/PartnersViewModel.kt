package org.jetbrains.kotlinconf.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesIntoMap
import dev.zacsweers.metrox.viewmodel.ViewModelKey
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.flow.stateIn
import org.jetbrains.kotlinconf.ConferenceService
import org.jetbrains.kotlinconf.PartnerGroup

@ContributesIntoMap(AppScope::class)
@ViewModelKey
class PartnersViewModel(
    service: ConferenceService,
) : ViewModel() {
    val partnerGroups: StateFlow<List<PartnerGroup>> = service.conferenceInfo
        .mapNotNull { it?.partners }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
}
