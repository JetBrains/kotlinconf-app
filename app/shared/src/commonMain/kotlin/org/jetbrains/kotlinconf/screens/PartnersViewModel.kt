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
import org.jetbrains.kotlinconf.PartnerGroup
import org.jetbrains.kotlinconf.Theme

@ContributesIntoMap(AppScope::class)
@ViewModelKey(PartnersViewModel::class)
class PartnersViewModel(
    service: ConferenceService,
) : ViewModel() {
    val partnerGroups: StateFlow<List<PartnerGroup>> = service.conferenceInfo
        .map { it?.partners ?: emptyList() }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val theme: StateFlow<Theme> = service.getTheme()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), Theme.SYSTEM)
}
