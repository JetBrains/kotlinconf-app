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
import org.jetbrains.kotlinconf.Speaker
import org.jetbrains.kotlinconf.SpeakerId
import org.jetbrains.kotlinconf.utils.containsDiacritics
import org.jetbrains.kotlinconf.utils.removeDiacritics

data class SpeakerWithHighlights(
    val speaker: Speaker,
    val nameHighlights: List<IntRange>,
    val titleHighlights: List<IntRange>,
) {
    val id: SpeakerId get() = speaker.id
    val name: String get() = speaker.name
    val position: String get() = speaker.position
    val photoUrl: String get() = speaker.photoUrl
}

class SpeakersViewModel(
    service: ConferenceService,
) : ViewModel() {
    private var searchText = MutableStateFlow("")

    fun setSearchText(searchText: String) {
        this.searchText.value = searchText
    }

    val speakers = combine(service.speakers, searchText) { speakers, searchText ->
        if (searchText.isBlank()) {
            speakers.all.map { SpeakerWithHighlights(it, emptyList(), emptyList()) }
        } else {
            speakers.all.mapNotNull {
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
        }
    }
        .flowOn(Dispatchers.Default)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
}
