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
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import org.jetbrains.kotlinconf.ConferenceService
import org.jetbrains.kotlinconf.PartnerId
import org.jetbrains.kotlinconf.PartnerInfo
import org.jetbrains.kotlinconf.utils.ErrorLoadingState

@AssistedInject
class PartnerDetailViewModel(
    service: ConferenceService,
    @Assisted private val partnerId: PartnerId,
) : ViewModel() {
    val partner: StateFlow<ErrorLoadingState<PartnerInfo>> = service.getPartner(partnerId)
        .map { partner ->
            if (partner != null) ErrorLoadingState.Content(partner)
            else ErrorLoadingState.Error
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), ErrorLoadingState.Loading)

    @AssistedFactory
    @ManualViewModelAssistedFactoryKey
    @ContributesIntoMap(AppScope::class)
    fun interface Factory : ManualViewModelAssistedFactory {
        fun create(partnerId: PartnerId): PartnerDetailViewModel
    }
}
