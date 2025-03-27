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
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.jetbrains.kotlinconf.ConferenceService
import org.jetbrains.kotlinconf.Day
import org.jetbrains.kotlinconf.Score
import org.jetbrains.kotlinconf.SessionCardView
import org.jetbrains.kotlinconf.SessionId
import org.jetbrains.kotlinconf.TagValues
import org.jetbrains.kotlinconf.TimeSlot
import org.jetbrains.kotlinconf.isLive
import org.jetbrains.kotlinconf.isServiceEvent
import org.jetbrains.kotlinconf.ui.components.Emotion
import org.jetbrains.kotlinconf.ui.components.FilterItem
import org.jetbrains.kotlinconf.ui.components.FilterItemType
import org.jetbrains.kotlinconf.utils.containsDiacritics
import org.jetbrains.kotlinconf.utils.removeDiacritics

sealed interface ScheduleListItem

data class DayHeaderItem(val value: Day) : ScheduleListItem

data class TimeSlotTitleItem(val value: TimeSlot) : ScheduleListItem

data class NoBookmarksItem(val id: String) : ScheduleListItem

data class SessionItem(
    val value: SessionCardView,
    val tagMatches: List<String> = emptyList(),
    val titleHighlights: List<IntRange> = emptyList(),
    val speakerHighlights: List<IntRange> = emptyList(),
) : ScheduleListItem

data class ServiceEventItem(
    val value: SessionCardView,
) : ScheduleListItem

data class ServiceEventGroupItem(
    val value: List<SessionCardView>,
) : ScheduleListItem

data class WorkshopItem(
    val workshops: List<SessionCardView>,
) : ScheduleListItem

fun ScheduleListItem.isLive(): Boolean {
    return when (this) {
        is DayHeaderItem -> false
        is ServiceEventGroupItem -> this.value.any { it.isLive }
        is ServiceEventItem -> this.value.isLive
        is SessionItem -> this.value.isLive
        is TimeSlotTitleItem -> this.value.isLive
        is WorkshopItem -> this.workshops.any { it.isLive }
        is NoBookmarksItem -> false
    }
}

data class ScheduleSearchParams(
    val searchQuery: String = "",
    val isSearch: Boolean = false,
    val isBookmarkedOnly: Boolean = false,
)

sealed class ScheduleUiState {
    data object Loading : ScheduleUiState()
    data object Error : ScheduleUiState()

    data class Content(
        val days: List<Day>,
        val items: List<ScheduleListItem>,
        val userSignedIn: Boolean,
    ) : ScheduleUiState() {
        val firstLiveIndex: Int = items.indexOfFirst { it.isLive() }
        val lastLiveIndex: Int = items.indexOfLast { it.isLive() }
    }
}

class ScheduleViewModel(
    private val service: ConferenceService,
) : ViewModel() {
    private val _navigateToPrivacyPolicy = MutableStateFlow(false)
    val navigateToPrivacyPolicy: StateFlow<Boolean> = _navigateToPrivacyPolicy.asStateFlow()

    private val searchParams = MutableStateFlow(ScheduleSearchParams())

    private fun List<String>.toTags(type: FilterItemType): List<FilterItem> {
        return map { FilterItem(type = type, value = it, isSelected = false) }
    }

    val filterItems = MutableStateFlow<List<FilterItem>>(
        TagValues.categories.toTags(FilterItemType.Category) +
                TagValues.levels.toTags(FilterItemType.Level) +
                TagValues.formats.toTags(FilterItemType.Format)
    )

    private var loading = MutableStateFlow(false)

    fun toggleFilter(item: FilterItem, selected: Boolean) {
        filterItems.update {
            val list = it.toMutableList()

            if (item.type == FilterItemType.Level || item.type == FilterItemType.Format) {
                // Remove previous format or level selection, if there is one
                val prevSelectedIndex = list.indexOfFirst { it.type == item.type && it.isSelected }
                if (prevSelectedIndex >= 0) {
                    list[prevSelectedIndex] = list[prevSelectedIndex].copy(isSelected = false)
                }
            }

            val index = list.indexOf(item)
            if (index >= 0) {
                list[index] = item.copy(isSelected = selected)
            }

            list
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

    val uiState: StateFlow<ScheduleUiState> = combine(
        service.agenda,
        service.userId,
        searchParams,
        filterItems,
        loading,
    ) { agenda, userId, searchParams, tags, loading ->
        when {
            loading -> ScheduleUiState.Loading

            searchParams.isSearch -> {
                val searchItems = buildSearchItems(
                    days = agenda,
                    searchQuery = searchParams.searchQuery,
                    tagValues = tags.filter { it.isSelected }.map { it.value },
                )
                ScheduleUiState.Content(agenda, searchItems, userId != null)
            }

            else -> {
                val items = buildNonSearchItems(
                    days = agenda,
                    isBookmarkedOnly = searchParams.isBookmarkedOnly,
                )
                if (items.isEmpty()) {
                    ScheduleUiState.Error
                } else {
                    ScheduleUiState.Content(agenda, items, userId != null)
                }
            }
        }
    }
        .flowOn(Dispatchers.Default)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = ScheduleUiState.Loading
        )

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

                val (allWorkshops, notWorkshops) = timeSlot.sessions.partition { it.tags.contains("Workshop") }
                val validWorkshops = if (isBookmarkedOnly) allWorkshops.filter { it.isFavorite } else allWorkshops
                if (validWorkshops.isNotEmpty()) {
                    add(WorkshopItem(validWorkshops))
                }

                val (serviceEvents, allTalks) = notWorkshops.partition { it.isServiceEvent }
                if (serviceEvents.size == 1) {
                    add(ServiceEventItem(serviceEvents.first()))
                } else if (serviceEvents.size > 1) {
                    add(ServiceEventGroupItem(serviceEvents))
                }

                val validTalks = if (isBookmarkedOnly) allTalks.filter { it.isFavorite } else allTalks
                validTalks.forEach { session ->
                    add(SessionItem(session))
                }

                // If nothing was added to this slot, but there were workshops or talks,
                // they must have been filtered out because none of them are bookmarked
                if (last() is TimeSlotTitleItem && (allWorkshops.isNotEmpty() || allTalks.isNotEmpty())) {
                    add(NoBookmarksItem(id = "empty-${timeSlot.startsAt}"))
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
        if (session.isServiceEvent) {
            return MatchResult(matched = false)
        }

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
            if (service.canVote()) {
                service.vote(sessionId, score)
            } else {
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
            if (service.canVote()) {
                service.vote(sessionId, score)
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

}
