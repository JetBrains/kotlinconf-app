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
import kotlinx.datetime.LocalDateTime
import org.jetbrains.kotlinconf.ConferenceService
import org.jetbrains.kotlinconf.Day
import org.jetbrains.kotlinconf.Score
import org.jetbrains.kotlinconf.SessionCardView
import org.jetbrains.kotlinconf.SessionId
import org.jetbrains.kotlinconf.TagValues
import org.jetbrains.kotlinconf.TimeProvider
import org.jetbrains.kotlinconf.TimeSlot
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
        val firstActiveIndex: Int = -1,
        val lastActiveIndex: Int = -1,
    ) : ScheduleUiState()
}

class ScheduleViewModel(
    private val service: ConferenceService,
    private val timeProvider: TimeProvider,
) : ViewModel() {
    private val _navigateToPrivacyNotice = MutableStateFlow(false)
    val navigateToPrivacyNotice: StateFlow<Boolean> = _navigateToPrivacyNotice.asStateFlow()

    private val searchParams = MutableStateFlow(ScheduleSearchParams())

    private fun List<String>.toTags(type: FilterItemType): List<FilterItem> {
        return map { FilterItem(type = type, value = it, isSelected = false) }
    }

    val filterItems = MutableStateFlow(
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
                    selectedTags = tags.filter { it.isSelected }.map { it.value },
                )
                ScheduleUiState.Content(agenda, searchItems, userId != null)
            }

            else -> {
                val (items, firstActiveIndex, lastActiveIndex) = buildNonSearchItems(
                    now = timeProvider.now(),
                    days = agenda,
                    isBookmarkedOnly = searchParams.isBookmarkedOnly,
                )
                if (items.isEmpty()) {
                    ScheduleUiState.Error
                } else {
                    ScheduleUiState.Content(
                        days = agenda,
                        items = items,
                        userSignedIn = userId != null,
                        firstActiveIndex = firstActiveIndex,
                        lastActiveIndex = lastActiveIndex,
                    )
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
        selectedTags: List<String>,
    ): List<ScheduleListItem> = buildList {
        for (day in days) {
            for (timeSlot in day.timeSlots) {
                for (session in timeSlot.sessions) {
                    val result = match(
                        session = session,
                        searchRegex = searchQuery.toRegex(RegexOption.IGNORE_CASE),
                        diacriticsSearch = searchQuery.containsDiacritics(),
                        tags = selectedTags,
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
        now: LocalDateTime,
        isBookmarkedOnly: Boolean,
    ): Triple<List<ScheduleListItem>, Int, Int> {
        var firstActiveIndex = -1
        var lastActiveIndex = -1
        var seenPastSlot = false

        val items = buildList {
            days.forEach { day ->
                add(DayHeaderItem(day))

                day.timeSlots.forEachIndexed { index, timeSlot ->
                    var activeTimeSlot = false

                    if (firstActiveIndex == -1) { // We didn't find the active slot yet
                        if (index == 0 && // This is the first slot of the day AND
                            ((seenPastSlot && now < timeSlot.startsAt) || // There was a slot in the past before and this one is still upcoming OR
                                    (day.date == now.date && now < timeSlot.startsAt)) // This is today and the day didn't start yet
                        ) {
                            firstActiveIndex = lastIndex // We'll consider the DayHeader and this first slot active
                            activeTimeSlot = true
                        } else if (
                            (seenPastSlot && now < timeSlot.startsAt) || // There was a slot in the past before and this one is still upcoming OR
                            (now in timeSlot.startsAt..<timeSlot.endsAt) // We're in this slot right now
                        ) {
                            firstActiveIndex = lastIndex + 1 // This is the active slot, starting with its title
                            activeTimeSlot = true
                        }
                    }

                    add(TimeSlotTitleItem(timeSlot))

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

                    if (last() is TimeSlotTitleItem && isBookmarkedOnly) {
                        add(NoBookmarksItem(id = "empty-${timeSlot.startsAt}"))
                    }

                    if (activeTimeSlot) { // This was the active slot
                        lastActiveIndex = lastIndex // Mark its end
                    }

                    if (!seenPastSlot) { // No slots were in the past yet
                        if (timeSlot.endsAt < now) { // This slot is in the past!
                            seenPastSlot = true
                        }
                    }
                }
            }
        }

        return Triple(items, firstActiveIndex, lastActiveIndex)
    }

    private class MatchResult(
        val matched: Boolean,
        val tagMatches: List<String> = emptyList(),
        val titleHighlights: List<IntRange> = emptyList(),
        val speakerHighlights: List<IntRange> = emptyList(),
    )

    private fun match(
        session: SessionCardView,
        searchRegex: Regex,
        diacriticsSearch: Boolean,
        tags: List<String>,
    ): MatchResult {
        if (session.isServiceEvent) {
            return MatchResult(matched = false)
        }

        if (!session.tags.containsAll(tags)) {
            return MatchResult(matched = false)
        }

        // Look for exact matches if diacritics are present, ignore all diacritics otherwise
        val title = session.title
        val targetTitle = if (diacriticsSearch) title else title.removeDiacritics()

        val speakerLine = session.speakerLine
        val targetSpeakers = if (diacriticsSearch) speakerLine else speakerLine.removeDiacritics()

        val titleHighlights = searchRegex.findAll(targetTitle).map { it.range }.toList()
        val speakerHighlights = searchRegex.findAll(targetSpeakers).map { it.range }.toList()

        return MatchResult(
            matched = titleHighlights.isNotEmpty() || speakerHighlights.isNotEmpty(),
            tagMatches = tags,
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
                _navigateToPrivacyNotice.value = true
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
                _navigateToPrivacyNotice.value = true
            }
        }
    }

    fun onNavigatedToPrivacyNotice() {
        _navigateToPrivacyNotice.value = false
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
