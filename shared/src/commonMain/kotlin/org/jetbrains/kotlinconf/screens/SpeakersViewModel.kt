package org.jetbrains.kotlinconf.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.*
import org.jetbrains.kotlinconf.ConferenceService
import org.jetbrains.kotlinconf.Speakers
import org.jetbrains.kotlinconf.utils.containsDiacritics
import org.jetbrains.kotlinconf.utils.removeDiacritics

class SpeakersViewModel(
    service: ConferenceService,
) : ViewModel() {
    var searchText = MutableStateFlow("")

    private val speakers: StateFlow<Speakers> = service.speakers
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), Speakers())

    val filteredSpeakers = combine(speakers, searchText) { speakers, searchText ->
        if (searchText.isEmpty()) {
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
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), speakers.value.all)
}
