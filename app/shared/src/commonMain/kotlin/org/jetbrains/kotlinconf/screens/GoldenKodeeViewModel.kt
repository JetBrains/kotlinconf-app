package org.jetbrains.kotlinconf.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesIntoMap
import dev.zacsweers.metrox.viewmodel.ViewModelKey
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import org.jetbrains.kotlinconf.AwardCategory
import org.jetbrains.kotlinconf.FakeGoldenKodeeService

@ContributesIntoMap(AppScope::class)
@ViewModelKey(GoldenKodeeViewModel::class)
class GoldenKodeeViewModel(
    service: FakeGoldenKodeeService,
) : ViewModel() {
    val categories: StateFlow<List<AwardCategory>> = service.getCategories()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
}
