package org.jetbrains.kotlinconf.screens


import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinconfapp.shared.generated.resources.Res
import kotlinconfapp.shared.generated.resources.nav_destination_schedule
import kotlinconfapp.shared.generated.resources.schedule_action_filter_bookmarked
import kotlinconfapp.shared.generated.resources.schedule_action_search
import kotlinconfapp.ui_components.generated.resources.bookmark_24
import kotlinconfapp.ui_components.generated.resources.search_24
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.kotlinconf.ConferenceService
import org.jetbrains.kotlinconf.SessionId
import org.jetbrains.kotlinconf.ui.components.DayHeader
import org.jetbrains.kotlinconf.ui.components.Divider
import org.jetbrains.kotlinconf.ui.components.Emotion
import org.jetbrains.kotlinconf.ui.components.Filters
import org.jetbrains.kotlinconf.ui.components.MainHeaderContainer
import org.jetbrains.kotlinconf.ui.components.MainHeaderContainerState
import org.jetbrains.kotlinconf.ui.components.MainHeaderSearchBar
import org.jetbrains.kotlinconf.ui.components.MainHeaderTitleBar
import org.jetbrains.kotlinconf.ui.components.StyledText
import org.jetbrains.kotlinconf.ui.components.Switcher
import org.jetbrains.kotlinconf.ui.components.TalkCard
import org.jetbrains.kotlinconf.ui.components.TalkStatus
import org.jetbrains.kotlinconf.ui.components.TopMenuButton
import org.jetbrains.kotlinconf.ui.theme.KotlinConfTheme
import org.jetbrains.kotlinconf.utils.time
import kotlinconfapp.ui_components.generated.resources.Res as UiRes

@Composable
fun ScheduleScreen(
    service: ConferenceService,
    onSession: (SessionId) -> Unit,
    viewModel: ScheduleViewModel = viewModel { ScheduleViewModel(service) },
) {
    val scope = rememberCoroutineScope()
    var bookmarkFilterEnabled by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }
    val listState = rememberLazyListState()

    val days by viewModel.agenda.collectAsState()
    val items by viewModel.items.collectAsState()

    // Day switcher selection state calculated from the scroll state
    val computedDayIndex by derivedStateOf {
        items.asSequence()
            .take(listState.firstVisibleItemIndex + 1)
            .findLast { it is DayHeaderItem }
            .let {
                (it as? DayHeaderItem)?.value?.day?.ordinal ?: 0
            }
    }
    // Override for the day switcher selection
    var targetDayIndex by remember { mutableStateOf<Int?>(null) }
    val selectedDayIndex = targetDayIndex ?: computedDayIndex

    var headerState by remember { mutableStateOf(MainHeaderContainerState.Title) }
    val isSearch = remember(headerState) {
        headerState == MainHeaderContainerState.Search
    }

    val params = ScheduleSearchParams(
        searchQuery = searchQuery,
        isSearch = isSearch,
        isBookmarkedOnly = bookmarkFilterEnabled,
    )
    LaunchedEffect(params) {
        viewModel.setSearchParams(params)
    }

    Column(modifier = Modifier.fillMaxSize()) {
        Header(
            headerState = headerState,
            onHeaderStateChange = { headerState = it },
            bookmarkFilterEnabled = bookmarkFilterEnabled,
            onBookmarkFilter = { bookmarkFilterEnabled = it },
            searchQuery = searchQuery,
            onSearchQueryChange = { searchQuery = it },
            onClearSearch = { viewModel.resetFilters() }
        )
        Divider(
            thickness = 1.dp,
            color = KotlinConfTheme.colors.strokePale,
        )

        AnimatedContent(
            isSearch,
            transitionSpec = {
                fadeIn(animationSpec = tween(90, delayMillis = 40)).togetherWith(fadeOut(animationSpec = tween(40)))
            },
        ) { isSearch ->
            if (isSearch) {
                val tags by viewModel.filterItems.collectAsState()
                Filters(
                    tags = tags,
                    toggleItem = { item, selected -> viewModel.toggleFilter(item, selected) },
                    modifier = Modifier.padding(vertical = 16.dp, horizontal = 12.dp),
                )
            } else {
                Switcher(
                    items = days.map {
                        // TODO proper date handling
                        "May ${it.day.ordinal + 22}"
                    },
                    selectedIndex = selectedDayIndex,
                    onSelect = { index ->
                        scope.launch {
                            val dayItemIndex = items.indexOf(DayHeaderItem(days[index]))
                            // Temporarily override the scroll state based selection
                            targetDayIndex = index
                            // Scroll to the item
                            listState.animateScrollToItem(dayItemIndex)
                            // Remove override, let scroll state determine the selection
                            targetDayIndex = null
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 8.dp)
                )
            }
        }

        ScheduleList(
            scheduleItems = items,
            onSession = onSession,
            listState = listState,
            onSubmitFeedback = { sessionId, emotion ->
                viewModel.onSubmitFeedback(sessionId, emotion)
            },
            onSubmitFeedbackWithComment = { sessionId, emotion, comment ->
                viewModel.onSubmitFeedbackWithComment(sessionId, emotion, comment)
            },
            onBookmark = { sessionId, isBookmarked ->
                viewModel.onBookmark(sessionId, isBookmarked)
            },
        )
    }
}

@Composable
private fun Header(
    headerState: MainHeaderContainerState,
    onHeaderStateChange: (MainHeaderContainerState) -> Unit,
    bookmarkFilterEnabled: Boolean,
    onBookmarkFilter: (Boolean) -> Unit,
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    onClearSearch: () -> Unit,
) {
    MainHeaderContainer(
        state = headerState,
        titleContent = {
            MainHeaderTitleBar(
                title = stringResource(Res.string.nav_destination_schedule),
                startContent = {
                    // TODO Now Button
                },
                endContent = {
                    TopMenuButton(
                        icon = UiRes.drawable.bookmark_24,
                        selected = bookmarkFilterEnabled,
                        onToggle = { onBookmarkFilter(it) },
                        contentDescription = stringResource(Res.string.schedule_action_filter_bookmarked),
                    )
                    TopMenuButton(
                        icon = UiRes.drawable.search_24,
                        onClick = { onHeaderStateChange(MainHeaderContainerState.Search) },
                        contentDescription = stringResource(Res.string.schedule_action_search),
                    )
                }
            )
        },
        searchContent = {
            MainHeaderSearchBar(
                searchValue = searchQuery,
                // clearing the input should also reset tags
                onSearchValueChange = { onSearchQueryChange(it) },
                onClose = {
                    onHeaderStateChange(MainHeaderContainerState.Title)
                    onSearchQueryChange("")
                },
                onClear = onClearSearch,
            )
        }
    )
}

@Composable
fun ScheduleList(
    scheduleItems: List<ScheduleListItem>,
    onSession: (SessionId) -> Unit,
    listState: LazyListState,
    onSubmitFeedback: (SessionId, Emotion?) -> Unit,
    onSubmitFeedbackWithComment: (SessionId, Emotion, String) -> Unit,
    onBookmark: (SessionId, Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyColumn(
        state = listState,
        modifier = modifier,
    ) {
        items(scheduleItems) { item ->
            when (item) {
                is DayHeaderItem -> {
                    val day = item.value
                    // TODO pack these pieces of text into DayHeaderItem
                    DayHeader(
                        month = "MAY",
                        day = (day.day.ordinal + 22).toString(),
                        line1 = "Conference",
                        line2 = "Day ${day.day.ordinal + 1}",
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 16.dp)
                    )
                }

                is TimeSlotTitleItem -> {
                    val timeSlot = item.value
                    StyledText(
                        text = "${timeSlot.startsAt.time()} - ${timeSlot.endsAt.time()}",
                        style = KotlinConfTheme.typography.h2,
                        modifier = Modifier
                            .padding(horizontal = 12.dp, vertical = 8.dp)
                    )
                }

                is SessionItem -> {
                    val session = item.value
                    TalkCard(
                        title = session.title,
                        titleHighlights = item.titleHighlights,
                        bookmarked = session.isFavorite,
                        onBookmark = { isBookmarked -> onBookmark(session.id, isBookmarked) },
                        tags = session.tags,
                        tagHighlights = item.tagMatches,
                        speakers = session.speakerLine,
                        speakerHighlights = item.speakerHighlights,
                        location = session.locationLine,
                        lightning = session.isLightning,
                        time = session.badgeTimeLine,
                        timeNote = null,
                        status = when {
                            // TODO time handling
                            session.isFinished -> TalkStatus.Past
//                            timeSlot.isLive -> TalkStatus.Now
                            else -> TalkStatus.Upcoming
                        },
                        onSubmitFeedback = { emotion ->
                            onSubmitFeedback(session.id, emotion)
                        },
                        onSubmitFeedbackWithComment = { emotion, comment ->
                            onSubmitFeedbackWithComment(session.id, emotion, comment)
                        },
                        onClick = { onSession(session.id) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp, vertical = 8.dp)
                    )
                }
            }
        }
    }
}
