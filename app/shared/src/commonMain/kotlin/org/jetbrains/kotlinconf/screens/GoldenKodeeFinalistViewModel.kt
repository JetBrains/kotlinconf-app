package org.jetbrains.kotlinconf.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.map
import org.jetbrains.kotlinconf.AwardCategoryId
import org.jetbrains.kotlinconf.ConferenceService
import org.jetbrains.kotlinconf.Nominee
import org.jetbrains.kotlinconf.NomineeId
import org.koin.core.annotation.InjectedParam
import org.koin.core.annotation.KoinViewModel

@KoinViewModel
class GoldenKodeeFinalistViewModel(
    conferenceService: ConferenceService,
    @InjectedParam private val categoryId: AwardCategoryId,
    @InjectedParam private val nomineeId: NomineeId,
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
}
