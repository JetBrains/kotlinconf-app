package org.jetbrains.kotlinconf.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
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
import org.jetbrains.kotlinconf.ui.components.ServiceEventData
import org.jetbrains.kotlinconf.utils.containsDiacritics
import org.jetbrains.kotlinconf.utils.removeDiacritics

sealed interface ScheduleListItem

data class DayHeaderItem(val value: Day) : ScheduleListItem

data class TimeSlotTitleItem(val value: TimeSlot) : ScheduleListItem

data class SessionItem(
    val value: SessionCardView,
    val tagMatches: List<String> = emptyList(),
    val titleHighlights: List<IntRange> = emptyList(),
    val speakerHighlights: List<IntRange> = emptyList(),
) : ScheduleListItem

data class ServiceEventItem(
    val value: ServiceEventData,
) : ScheduleListItem

data class WorkshopItem(
    val workshops: List<SessionCardView>,
) : ScheduleListItem

fun ScheduleListItem.isLive(): Boolean =
    (this is SessionItem && this.value.isLive) ||
        (this is WorkshopItem && this.workshops.first().isLive) ||
        (this is TimeSlotTitleItem && this.value.isLive)

fun ScheduleListItem.isUpcoming(): Boolean =
    (this is SessionItem && this.value.isUpcoming) ||
        (this is WorkshopItem && this.workshops.first().isUpcoming) ||
        (this is TimeSlotTitleItem && this.value.isUpcoming)

fun ScheduleListItem.isUpcomingSoon(): Boolean =
    (this is WorkshopItem && this.workshops.first().startsInMinutes != null) ||
        (this is SessionItem && this.value.isUpcoming && this.value.startsInMinutes != null)

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
    private val _navigateToPrivacyPolicy = MutableStateFlow(false)
    val navigateToPrivacyPolicy: StateFlow<Boolean> = _navigateToPrivacyPolicy.asStateFlow()

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

    private fun buildItems(
        days: List<Day>,
        searchParams: ScheduleSearchParams,
        tags: List<FilterItem>,
    ): List<ScheduleListItem> {
        val tagValues = tags.filter { it.isSelected }.map { it.value }

        return if (searchParams.isSearch) {
            buildSearchItems(days, searchParams.searchQuery, tagValues)
        } else {
            buildNonSearchItems(days, searchParams.isBookmarkedOnly)
        }
    }

    private fun buildSearchItems(
        days: List<Day>,
        searchQuery: String,
        tagValues: List<String>,
    ): List<ScheduleListItem> = buildList {
        days.forEach { day ->
            day.timeSlots.forEach { timeSlot ->
                timeSlot.sessions.forEach { session ->
                    val result = match(
                        session = session,
                        searchQuery = searchQuery,
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
                }
            }
        }
    }

    // TODO add ServiceEventItems to this list https://github.com/JetBrains/kotlinconf-app/issues/269
    private fun buildNonSearchItems(
        days: List<Day>,
        isBookmarkedOnly: Boolean,
    ): List<ScheduleListItem> = buildList {
        days.forEach { day ->
            add(DayHeaderItem(day))

            day.timeSlots.forEach { timeSlot ->
                add(TimeSlotTitleItem(timeSlot))

                val sessions = if (isBookmarkedOnly) {
                    timeSlot.sessions.filter { it.isFavorite }
                } else {
                    timeSlot.sessions
                }

                val (workshops, talks) = sessions.partition { it.tags.contains("Workshop") }
                if (workshops.isNotEmpty()) {
                    add(WorkshopItem(workshops))
                }
                talks.forEach { session ->
                    add(SessionItem(session))
                }

                // Remove empty time slots
                if (last() is TimeSlotTitleItem) {
                    removeLast()
                }
            }

            // Remove empty days
            if (last() is DayHeaderItem) {
                removeLast()
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

        if (tags.isNotEmpty()) {
            if (session.tags.containsAll(tags)) {
                tagMatches.addAll(tags)
            } else {
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
            if (!service.vote(sessionId, score)) {
                _navigateToPrivacyPolicy.value = true
            }
        }
    }

    fun onSubmitFeedbackWithComment(sessionId: SessionId, emotion: Emotion, comment: String) {
        val score = when (emotion) {
            Emotion.Positive -> Score.GOOD
            Emotion.Neutral -> Score.OK
            Emotion.Negative -> Score.BAD
        }
        viewModelScope.launch {
            if (service.vote(sessionId, score)) {
                service.sendFeedback(sessionId, comment)
            } else {
                _navigateToPrivacyPolicy.value = true
            }
        }
    }

    fun onNavigatedToPrivacyPolicy() {
        _navigateToPrivacyPolicy.value = false
    }

    fun onBookmark(sessionId: SessionId, bookmarked: Boolean) {
        viewModelScope.launch {
            service.setFavorite(sessionId, bookmarked)
        }
    }
}
