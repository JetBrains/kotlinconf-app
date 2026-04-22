package org.jetbrains.kotlinconf.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.map
import org.jetbrains.kotlinconf.AwardCategory
import org.jetbrains.kotlinconf.ConferenceService
import org.koin.core.annotation.KoinViewModel

@KoinViewModel
class GoldenKodeeViewModel(
    conferenceService: ConferenceService,
) : ViewModel() {
    val categories: StateFlow<List<AwardCategory>> = conferenceService.goldenKodeeData
        .map { it?.categories ?: emptyList() }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
}
