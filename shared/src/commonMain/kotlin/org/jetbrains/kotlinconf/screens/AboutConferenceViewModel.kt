package org.jetbrains.kotlinconf.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import org.jetbrains.kotlinconf.ABOUT_CONFERENCE_BLOCKS
import org.jetbrains.kotlinconf.ConferenceService
import org.jetbrains.kotlinconf.SessionCardView
import org.jetbrains.kotlinconf.Speaker

data class AboutConferenceEvent(
    val sessionCard: SessionCardView?,
    val speakers: List<Speaker>?,
    val month: String,
    val day: String,
    val title1: String,
    val title2: String,
    val description: String?,
)

class AboutConferenceViewModel(
    service: ConferenceService,
) : ViewModel() {
    val events: StateFlow<List<AboutConferenceEvent>> =
        combine(service.agenda, service.speakers) { agenda, speakers ->
            val sessionsById = agenda
                .flatMap { it.timeSlots.flatMap { it.sessions } }
                .associateBy { it.id }
            val speakersById = speakers.associateBy { it.id }

            ABOUT_CONFERENCE_BLOCKS.map { block ->
                val session = block.sessionId?.let { sessionsById[it] }
                val speakers = session?.speakerIds?.mapNotNull { speakersById[it] }
                AboutConferenceEvent(
                    sessionCard = session,
                    speakers = speakers,
                    month = block.month,
                    day = block.day,
                    title1 = block.title1,
                    title2 = block.title2,
                    description = block.description,
                )
            }
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
}
