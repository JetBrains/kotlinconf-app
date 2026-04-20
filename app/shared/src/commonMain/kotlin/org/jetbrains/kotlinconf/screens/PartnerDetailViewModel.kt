package org.jetbrains.kotlinconf.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import org.jetbrains.kotlinconf.ConferenceService
import org.jetbrains.kotlinconf.PartnerId
import org.jetbrains.kotlinconf.PartnerInfo
import org.jetbrains.kotlinconf.utils.ErrorLoadingState
import org.koin.core.annotation.InjectedParam
import org.koin.core.annotation.KoinViewModel

@KoinViewModel
class PartnerDetailViewModel(
    service: ConferenceService,
    @InjectedParam private val partnerId: PartnerId,
) : ViewModel() {
    val partner: StateFlow<ErrorLoadingState<PartnerInfo>> = service.getPartner(partnerId)
        .map { partner ->
            if (partner != null) ErrorLoadingState.Content(partner)
            else ErrorLoadingState.Error
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), ErrorLoadingState.Loading)
}
