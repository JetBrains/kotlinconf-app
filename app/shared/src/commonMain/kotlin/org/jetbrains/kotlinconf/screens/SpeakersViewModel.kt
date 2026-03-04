package org.jetbrains.kotlinconf.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import org.jetbrains.kotlinconf.ConferenceService
import org.jetbrains.kotlinconf.Speaker
import org.jetbrains.kotlinconf.utils.performSearch

data class SpeakerWithHighlights(
    val speaker: Speaker,
    val nameHighlights: List<IntRange> = emptyList(),
    val titleHighlights: List<IntRange> = emptyList(),
)

sealed class SpeakersUiState {
    data object Loading : SpeakersUiState()
    data object Error : SpeakersUiState()
    data class Content(val speakers: List<SpeakerWithHighlights>) : SpeakersUiState()
}

class SpeakersViewModel(
    private val service: ConferenceService,
) : ViewModel() {
    private var loading = MutableStateFlow(false)
    private var searchText = MutableStateFlow("")

    fun setSearchText(searchText: String) {
        this.searchText.value = searchText
    }

    fun refresh() {
        viewModelScope.launch {
            loading.value = true
            try {
                service.loadConferenceData()
            } finally {
                loading.value = false
            }
        }
    }

    val speakers: StateFlow<SpeakersUiState> = combine(
        service.speakers, searchText, loading
    ) { speakers, searchText, loading ->
        when {
            loading -> SpeakersUiState.Loading

            searchText.isBlank() -> {
                val allSpeakers = speakers.map { SpeakerWithHighlights(it) }
                if (allSpeakers.isNotEmpty()) {
                    SpeakersUiState.Content(allSpeakers)
                } else {
                    SpeakersUiState.Error
                }
            }

            else -> {
                val searchResults = speakers.performSearch(
                    searchText = searchText,
                    produceResult = { speaker, (nameMatches, titleMatches) ->
                        SpeakerWithHighlights(speaker, nameMatches, titleMatches)
                    },
                    selectors = listOf({ it.name }, { it.position }),
                )
                SpeakersUiState.Content(searchResults)
            }
        }
    }
        .flowOn(Dispatchers.Default)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), SpeakersUiState.Loading)
}
