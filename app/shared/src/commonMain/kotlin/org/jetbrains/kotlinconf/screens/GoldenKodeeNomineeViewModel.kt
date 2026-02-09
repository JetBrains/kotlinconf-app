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
import kotlinx.coroutines.flow.stateIn
import org.jetbrains.kotlinconf.AwardCategoryId
import org.jetbrains.kotlinconf.FakeGoldenKodeeService
import org.jetbrains.kotlinconf.Nominee
import org.jetbrains.kotlinconf.NomineeId

@AssistedInject
class GoldenKodeeNomineeViewModel(
    service: FakeGoldenKodeeService,
    @Assisted("categoryId") private val categoryId: AwardCategoryId,
    @Assisted("nomineeId") private val nomineeId: NomineeId,
) : ViewModel() {
    val nominee: StateFlow<Nominee?> = service.getNominee(categoryId, nomineeId)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    @AssistedFactory
    @ManualViewModelAssistedFactoryKey(Factory::class)
    @ContributesIntoMap(AppScope::class)
    fun interface Factory : ManualViewModelAssistedFactory {
        fun create(
            @Assisted("categoryId") categoryId: AwardCategoryId,
            @Assisted("nomineeId") nomineeId: NomineeId,
        ): GoldenKodeeNomineeViewModel
    }
}
