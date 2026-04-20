package org.jetbrains.kotlinconf.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.flow.stateIn
import org.jetbrains.kotlinconf.ConferenceService
import org.jetbrains.kotlinconf.PartnerGroup
import org.koin.core.annotation.KoinViewModel

@KoinViewModel
class PartnersViewModel(
    service: ConferenceService,
) : ViewModel() {
    val partnerGroups: StateFlow<List<PartnerGroup>> = service.conferenceInfo
        .mapNotNull { it?.partners }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
}
