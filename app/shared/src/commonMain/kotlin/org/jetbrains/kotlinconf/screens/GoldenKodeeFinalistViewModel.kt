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
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.map
import org.jetbrains.kotlinconf.AwardCategoryId
import org.jetbrains.kotlinconf.ConferenceService
import org.jetbrains.kotlinconf.Nominee
import org.jetbrains.kotlinconf.NomineeId

@AssistedInject
class GoldenKodeeFinalistViewModel(
    conferenceService: ConferenceService,
    @Assisted("categoryId") private val categoryId: AwardCategoryId,
    @Assisted("nomineeId") private val nomineeId: NomineeId,
) : ViewModel() {
    val nominee: StateFlow<Nominee?> = conferenceService.goldenKodeeData
        .map { data ->
            data?.categories?.find { it.id == categoryId }?.nominees?.find { it.id == nomineeId }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val year: StateFlow<String> = conferenceService.currentYear
        .filterNotNull()
        .map { it.toString() }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "")

    @AssistedFactory
    @ManualViewModelAssistedFactoryKey(Factory::class)
    @ContributesIntoMap(AppScope::class)
    fun interface Factory : ManualViewModelAssistedFactory {
        fun create(
            @Assisted("categoryId") categoryId: AwardCategoryId,
            @Assisted("nomineeId") nomineeId: NomineeId,
        ): GoldenKodeeFinalistViewModel
    }
}
