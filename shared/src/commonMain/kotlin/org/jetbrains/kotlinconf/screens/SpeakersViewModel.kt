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
import org.jetbrains.kotlinconf.utils.containsDiacritics
import org.jetbrains.kotlinconf.utils.removeDiacritics

data class SpeakerWithHighlights(
    val speaker: Speaker,
    val nameHighlights: List<IntRange>,
    val titleHighlights: List<IntRange>,
)

sealed class SpeakersUiState {
    data object Loading : SpeakersUiState()
    data object Error : SpeakersUiState()
    data class Content(val speakers: List<SpeakerWithHighlights>) : SpeakersUiState()
    data object NoSearchResults : SpeakersUiState()
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
                val allSpeakers = speakers.map { SpeakerWithHighlights(it, emptyList(), emptyList()) }
                if (allSpeakers.isNotEmpty()) {
                    SpeakersUiState.Content(allSpeakers)
                } else {
                    SpeakersUiState.Error
                }
            }

            else -> {
                val searchResults = speakers.mapNotNull {
                    // Look for exact matches if diacritics are present, ignore all diacritics otherwise
                    val diacriticsSearch = searchText.containsDiacritics()
                    val targetName = if (diacriticsSearch) it.name else it.name.removeDiacritics()
                    val targetPosition = if (diacriticsSearch) it.position else it.position.removeDiacritics()
                    val searchPattern = searchText.toRegex(RegexOption.IGNORE_CASE)

                    val nameMatches = searchPattern.findAll(targetName).map { it.range }.toList()
                    val titleMatches = searchPattern.findAll(targetPosition).map { it.range }.toList()

                    if (nameMatches.isNotEmpty() || titleMatches.isNotEmpty()) {
                        SpeakerWithHighlights(it, nameMatches, titleMatches)
                    } else {
                        null
                    }
                }
                if (searchResults.isNotEmpty()) {
                    SpeakersUiState.Content(searchResults)
                } else {
                    SpeakersUiState.NoSearchResults
                }
            }
        }
    }
        .flowOn(Dispatchers.Default)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), SpeakersUiState.Loading)
}
