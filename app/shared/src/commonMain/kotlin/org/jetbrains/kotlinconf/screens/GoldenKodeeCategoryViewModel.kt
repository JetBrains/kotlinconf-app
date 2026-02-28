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
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import org.jetbrains.kotlinconf.AwardCategory
import org.jetbrains.kotlinconf.AwardCategoryId
import org.jetbrains.kotlinconf.ConferenceService
import org.jetbrains.kotlinconf.Nominee

@AssistedInject
class GoldenKodeeCategoryViewModel(
    conferenceService: ConferenceService,
    @Assisted private val categoryId: AwardCategoryId,
) : ViewModel() {
    private val categoryFlow = conferenceService.goldenKodeeData
        .map { data -> data?.categories?.find { it.id == categoryId } }

    val category: StateFlow<AwardCategory?> = categoryFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val sortedNominees: StateFlow<List<Nominee>> = categoryFlow
        .filterNotNull()
        .map { category -> category.nominees.sortedByDescending { it.winner } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    @AssistedFactory
    @ManualViewModelAssistedFactoryKey(Factory::class)
    @ContributesIntoMap(AppScope::class)
    fun interface Factory : ManualViewModelAssistedFactory {
        fun create(categoryId: AwardCategoryId): GoldenKodeeCategoryViewModel
    }
}
