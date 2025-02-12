package org.jetbrains.kotlinconf.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.stateIn
import org.jetbrains.kotlinconf.ConferenceService
import org.jetbrains.kotlinconf.utils.containsDiacritics
import org.jetbrains.kotlinconf.utils.removeDiacritics

class SpeakersViewModel(
    service: ConferenceService,
) : ViewModel() {
    private var searchText = MutableStateFlow("")

    fun setSearchText(searchText: String) {
        this.searchText.value = searchText
    }

    val filteredSpeakers = combine(service.speakers, searchText) { speakers, searchText ->
        if (searchText.isBlank()) {
            speakers.all
        } else {
            speakers.all.filter {
                // Look for exact matches if diacritics are present, ignore all diacritics otherwise
                val diacriticsSearch = searchText.containsDiacritics()
                val targetName = if (diacriticsSearch) it.name else it.name.removeDiacritics()
                val targetPosition = if (diacriticsSearch) it.position else it.position.removeDiacritics()
                val searchPattern = searchText.toRegex(RegexOption.IGNORE_CASE)

                searchPattern.containsMatchIn(targetName) || searchPattern.containsMatchIn(targetPosition)
            }
        }
    }
        .flowOn(Dispatchers.Default)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
}
