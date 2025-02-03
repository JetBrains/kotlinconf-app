package org.jetbrains.kotlinconf.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.jetbrains.kotlinconf.ConferenceService
import org.jetbrains.kotlinconf.Day
import org.jetbrains.kotlinconf.Score
import org.jetbrains.kotlinconf.SessionCardView
import org.jetbrains.kotlinconf.SessionId
import org.jetbrains.kotlinconf.TimeSlot
import org.jetbrains.kotlinconf.ui.components.Emotion
import org.jetbrains.kotlinconf.ui.components.FilterItem
import org.jetbrains.kotlinconf.ui.components.FilterItemType
import org.jetbrains.kotlinconf.utils.containsDiacritics
import org.jetbrains.kotlinconf.utils.removeDiacritics

sealed interface ScheduleListItem

// TODO add service events
// TODO separate workshop events from the rest
data class DayHeaderItem(val value: Day) : ScheduleListItem
data class TimeSlotTitleItem(val value: TimeSlot) : ScheduleListItem
data class SessionItem(
    val value: SessionCardView,
    val tagMatches: List<String> = emptyList(),
    val titleHighlights: List<IntRange> = emptyList(),
    val speakerHighlights: List<IntRange> = emptyList(),
) : ScheduleListItem

// TODO get set of tags from the service
private val categoryTags = listOf(
    "Server-side",
    "Multiplatform",
    "Android",
    "Extensibility/Tooling",
    "Languages and Best Practices",
    "Other",
)
private val levelTags = listOf(
    "Introductory and overview",
    "Intermediate",
    "Advanced",
)
private val formatTags = listOf(
    "Workshop",
    "Regular Session",
    "Lightning Session",
)

data class ScheduleSearchParams(
    val searchQuery: String = "",
    val isSearch: Boolean = false,
    val isBookmarkedOnly: Boolean = false,
)

class ScheduleViewModel(
    private val service: ConferenceService,
) : ViewModel() {
    val agenda: StateFlow<List<Day>> = service.agenda.map { it.days }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val searchParams = MutableStateFlow(ScheduleSearchParams())

    private fun List<String>.toTags(type: FilterItemType): List<FilterItem> {
        return map { FilterItem(type = type, value = it, isSelected = false) }
    }

    val filterItems = MutableStateFlow<List<FilterItem>>(
        categoryTags.toTags(FilterItemType.Category) +
            levelTags.toTags(FilterItemType.Level) +
            formatTags.toTags(FilterItemType.Format)
    )

    private fun <T> MutableList<T>.replace(old: T, new: T) {
        val index = indexOf(old)
        if (index >= 0) {
            set(index, new)
        }
    }

    fun toggleFilter(item: FilterItem, selected: Boolean) {
        val newItem = item.copy(isSelected = selected)
        filterItems.update {
            it.toMutableList().apply {
                replace(item, newItem)
            }
        }
    }

    fun resetFilters() {
        filterItems.update {
            it.map { filter -> filter.copy(isSelected = false) }
        }
    }

    fun setSearchParams(searchParams: ScheduleSearchParams) {
        this.searchParams.value = searchParams
    }

    val items = combine(
        agenda,
        searchParams,
        filterItems,
    ) { days, searchParams, tags ->
        buildItems(
            days = days,
            searchParams = searchParams,
            tags = tags,
        )
    }.flowOn(Dispatchers.Default)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    /**
     * Builds a flat list of items for the UI, taking into account the current
     * filtering values and active filters.
     */
    private fun buildItems(
        days: List<Day>,
        searchParams: ScheduleSearchParams,
        tags: List<FilterItem>,
    ): List<ScheduleListItem> {
        val tagValues = tags.filter { it.isSelected }.map { it.value }

        return buildList {
            days.forEach { day ->
                if (!searchParams.isSearch) add(DayHeaderItem(day))

                day.timeSlots.forEach { timeSlot ->
                    if (!searchParams.isSearch) {
                        add(TimeSlotTitleItem(timeSlot))
                    }

                    timeSlot.sessions.forEach { session ->
                        if (searchParams.isSearch) {
                            val result = match(
                                session = session,
                                searchQuery = searchParams.searchQuery,
                                tags = tagValues,
                            )
                            if (result.matched) {
                                add(
                                    SessionItem(
                                        value = session,
                                        tagMatches = result.tagMatches,
                                        titleHighlights = result.titleHighlights,
                                        speakerHighlights = result.speakerHighlights,
                                    )
                                )
                            }
                        } else {
                            if (!searchParams.isBookmarkedOnly || session.isFavorite) {
                                add(SessionItem(session))
                            }
                        }
                    }

                    // If the timeslot doesn't have any sessions, remove its title
                    if (!searchParams.isSearch && last() is TimeSlotTitleItem) {
                        removeLast()
                    }
                }
            }
        }
    }

    private class MatchResult(
        val matched: Boolean,
        val tagMatches: List<String> = emptyList<String>(),
        val titleHighlights: List<IntRange> = emptyList<IntRange>(),
        val speakerHighlights: List<IntRange> = emptyList<IntRange>(),
    )

    private fun match(
        session: SessionCardView,
        searchQuery: String,
        tags: List<String>,
    ): MatchResult {
        // Result variables
        val tagMatches = mutableListOf<String>()
        val titleHighlights = mutableListOf<IntRange>()
        val speakerHighlights = mutableListOf<IntRange>()

        // TODO clarify requirements for tag filtering
        if (tags.isNotEmpty()) {
            tagMatches.addAll(session.tags.filter { it in tags })
            if (tagMatches.isEmpty()) {
                return MatchResult(matched = false)
            }
        }

        // Look for exact matches if diacritics are present, ignore all diacritics otherwise
        val diacriticsSearch = searchQuery.containsDiacritics()
        val targetTitle =
            if (diacriticsSearch) session.title
            else session.title.removeDiacritics()
        val targetSpeakers =
            if (diacriticsSearch) session.speakerLine
            else session.speakerLine.removeDiacritics()

        titleHighlights.addAll(
            searchQuery.toRegex(RegexOption.IGNORE_CASE).findAll(targetTitle).map { it.range })

        speakerHighlights.addAll(
            searchQuery.toRegex(RegexOption.IGNORE_CASE).findAll(targetSpeakers).map { it.range })

        return MatchResult(
            matched = titleHighlights.isNotEmpty() || speakerHighlights.isNotEmpty(),
            tagMatches = tagMatches,
            titleHighlights = titleHighlights,
            speakerHighlights = speakerHighlights,
        )
    }

    fun onSubmitFeedback(sessionId: SessionId, emotion: Emotion?) {
        val score = when (emotion) {
            Emotion.Positive -> Score.GOOD
            Emotion.Neutral -> Score.OK
            Emotion.Negative -> Score.BAD
            null -> null
        }
        viewModelScope.launch {
            service.vote(sessionId, score)
        }
    }

    fun onSubmitFeedbackWithComment(sessionId: SessionId, emotion: Emotion, comment: String) {
        val score = when (emotion) {
            Emotion.Positive -> Score.GOOD
            Emotion.Neutral -> Score.OK
            Emotion.Negative -> Score.BAD
        }
        viewModelScope.launch {
            service.vote(sessionId, score)
            service.sendFeedback(sessionId, comment)
        }
    }

    fun onBookmark(sessionId: SessionId, bookmarked: Boolean) {
        viewModelScope.launch {
            service.toggleFavorite(sessionId, bookmarked)
        }
    }
}
