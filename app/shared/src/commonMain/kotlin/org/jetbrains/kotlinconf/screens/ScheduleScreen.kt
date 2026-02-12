package org.jetbrains.kotlinconf.screens


import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigationevent.NavigationEventInfo
import androidx.navigationevent.compose.NavigationBackHandler
import androidx.navigationevent.compose.rememberNavigationEventState
import dev.zacsweers.metrox.viewmodel.metroViewModel
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDate
import org.jetbrains.compose.resources.pluralStringResource
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.kotlinconf.DayInfo
import org.jetbrains.kotlinconf.HideKeyboardOnDragHandler
import org.jetbrains.kotlinconf.ScrollToTopHandler
import org.jetbrains.kotlinconf.SessionCardView
import org.jetbrains.kotlinconf.SessionId
import org.jetbrains.kotlinconf.SessionState
import org.jetbrains.kotlinconf.generated.resources.Res
import org.jetbrains.kotlinconf.generated.resources.nav_destination_schedule
import org.jetbrains.kotlinconf.generated.resources.schedule_action_filter_bookmarked
import org.jetbrains.kotlinconf.generated.resources.schedule_action_search
import org.jetbrains.kotlinconf.generated.resources.schedule_error_no_data
import org.jetbrains.kotlinconf.generated.resources.schedule_in_x_minutes
import org.jetbrains.kotlinconf.generated.resources.schedule_label_no_bookmarks
import org.jetbrains.kotlinconf.generated.resources.schedule_number_of_results
import org.jetbrains.kotlinconf.generated.resources.session_feedback_sent
import org.jetbrains.kotlinconf.isLive
import org.jetbrains.kotlinconf.toEmotion
import org.jetbrains.kotlinconf.ui.components.DayHeader
import org.jetbrains.kotlinconf.ui.components.Emotion
import org.jetbrains.kotlinconf.ui.components.FilterItem
import org.jetbrains.kotlinconf.ui.components.Filters
import org.jetbrains.kotlinconf.ui.components.HorizontalDivider
import org.jetbrains.kotlinconf.ui.components.MainHeaderContainer
import org.jetbrains.kotlinconf.ui.components.MainHeaderContainerState
import org.jetbrains.kotlinconf.ui.components.MainHeaderSearchBar
import org.jetbrains.kotlinconf.ui.components.MainHeaderTitleBar
import org.jetbrains.kotlinconf.ui.components.NowButton
import org.jetbrains.kotlinconf.ui.components.NowButtonState
import org.jetbrains.kotlinconf.ui.components.ServiceEvent
import org.jetbrains.kotlinconf.ui.components.ServiceEventData
import org.jetbrains.kotlinconf.ui.components.ServiceEvents
import org.jetbrains.kotlinconf.ui.components.Switcher
import org.jetbrains.kotlinconf.ui.components.TalkCard
import org.jetbrains.kotlinconf.ui.components.TalkStatus
import org.jetbrains.kotlinconf.ui.components.Text
import org.jetbrains.kotlinconf.ui.components.TopMenuButton
import org.jetbrains.kotlinconf.ui.generated.resources.UiRes
import org.jetbrains.kotlinconf.ui.generated.resources.bookmark_24
import org.jetbrains.kotlinconf.ui.generated.resources.search_24
import org.jetbrains.kotlinconf.ui.theme.KotlinConfTheme
import org.jetbrains.kotlinconf.utils.DateTimeFormatting
import org.jetbrains.kotlinconf.utils.ErrorLoadingContent
import org.jetbrains.kotlinconf.utils.ErrorLoadingState
import org.jetbrains.kotlinconf.utils.LocalNotificationBar
import org.jetbrains.kotlinconf.utils.bottomInsetPadding
import org.jetbrains.kotlinconf.utils.topInsetPadding

@Composable
fun ScheduleScreen(
    onSession: (SessionId) -> Unit,
    onPrivacyNoticeNeeded: () -> Unit,
    viewModel: ScheduleViewModel = metroViewModel(),
) {
    val scope = rememberCoroutineScope()
    var bookmarkFilterEnabled by rememberSaveable { mutableStateOf(false) }
    var searchQuery by rememberSaveable { mutableStateOf("") }
    val listState = rememberLazyListState()

    val state = viewModel.uiState.collectAsStateWithLifecycle().value
    val shouldNavigateToPrivacyNotice by viewModel.navigateToPrivacyNotice.collectAsStateWithLifecycle()

    LaunchedEffect(shouldNavigateToPrivacyNotice) {
        if (shouldNavigateToPrivacyNotice) {
            onPrivacyNoticeNeeded()
            viewModel.onNavigatedToPrivacyNotice()
        }
    }

    var headerState by rememberSaveable { mutableStateOf(MainHeaderContainerState.Title) }
    val isSearch = rememberSaveable(headerState) { headerState == MainHeaderContainerState.Search }

    var firstScrollPerformed by rememberSaveable(isSearch, searchQuery) { mutableStateOf(false) }

    if (!firstScrollPerformed) {
        if (isSearch) {
            LaunchedEffect(searchQuery) {
                if (listState.firstVisibleItemIndex > 1) {
                    listState.scrollToItem(0)
                } else {
                    listState.animateScrollToItem(0)
                }
                firstScrollPerformed = true
            }
        } else {
            LaunchedEffect(state) {
                val content = (state as? ErrorLoadingState.Content)?.data
                if (content != null && content.firstActiveIndex != -1) {
                    listState.scrollToItem(content.firstActiveIndex)
                    firstScrollPerformed = true
                }
            }
        }
    }

    val params = ScheduleSearchParams(
        searchQuery = searchQuery,
        isSearch = isSearch,
        isBookmarkedOnly = bookmarkFilterEnabled,
    )
    LaunchedEffect(params) {
        viewModel.setSearchParams(params)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(color = KotlinConfTheme.colors.mainBackground)
            .padding(topInsetPadding())
    ) {
        Header(
            startContent = {
                if (state is ErrorLoadingState.Content) {
                    NowButtonContent(state.data, listState)
                }
            },
            headerState = headerState,
            onHeaderStateChange = { headerState = it },
            bookmarkFilterEnabled = bookmarkFilterEnabled,
            onBookmarkFilter = { bookmarkFilterEnabled = it },
            searchQuery = searchQuery,
            onSearchQueryChange = { searchQuery = it },
            onClearSearch = { viewModel.resetFilters() },
            viewModel = viewModel
        )
        HorizontalDivider(
            thickness = 1.dp,
            color = KotlinConfTheme.colors.strokePale,
        )

        ErrorLoadingContent(
            state = state,
            errorMessage = stringResource(Res.string.schedule_error_no_data),
            onRetry = { viewModel.refresh() },
            modifier = Modifier.fillMaxSize(),
        ) { content ->
            val days = content.days
            val items = content.items

            Column(Modifier.fillMaxSize()) {
                AnimatedVisibility(!isSearch) {
                    // Day switcher selection state calculated from the scroll state
                    val conferenceDates = days.map { it.date }
                    val computedDayIndex by derivedStateOf {
                        items.asSequence()
                            .take(listState.firstVisibleItemIndex + 1)
                            .findLast { it is DayHeaderItem }
                            ?.let {
                                val visibleDate = (it as DayHeaderItem).value.date
                                conferenceDates.indexOf(visibleDate)
                            } ?: 0
                    }
                    // Override for the day switcher selection
                    var targetDayIndex by remember { mutableStateOf<Int?>(null) }
                    val selectedDayIndex = targetDayIndex ?: computedDayIndex

                    Switcher(
                        items = remember(conferenceDates) {
                            conferenceDates.map { DateTimeFormatting.date(it) }
                        },
                        shortItems = null,
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

                val tags by viewModel.filterItems.collectAsStateWithLifecycle()
                val dayInfoMap by viewModel.dayInfoMap.collectAsStateWithLifecycle()
                ScheduleList(
                    scheduleItems = items,
                    onSession = onSession,
                    listState = listState,
                    isSearch = isSearch,
                    dayInfoMap = dayInfoMap,
                    onSubmitFeedback = { sessionId, emotion ->
                        viewModel.onSubmitFeedback(sessionId, emotion)
                    },
                    onSubmitFeedbackWithComment = { sessionId, emotion, comment ->
                        viewModel.onSubmitFeedbackWithComment(sessionId, emotion, comment)
                    },
                    onBookmark = { sessionId, isBookmarked ->
                        viewModel.onBookmark(sessionId, isBookmarked)
                    },
                    filterItems = tags,
                    onToggleFilter = { item, selected -> viewModel.toggleFilter(item, selected) },
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
    }
}

@Composable
private fun NowButtonContent(content: ScheduleContent, listState: LazyListState) {
    val scope = rememberCoroutineScope()
    var nowScrolling by remember { mutableStateOf(false) }
    val nowButtonState =
        derivedStateOf { computeNowButtonState(content, listState, nowScrolling) }.value

    if (nowButtonState != null) {
        NowButton(
            time = nowButtonState,
            onClick = {
                scope.launch {
                    nowScrolling = true
                    try {
                        listState.animateScrollToItem(content.firstActiveIndex)
                    } finally {
                        nowScrolling = false
                    }
                }
            }
        )
    }
}

private fun computeNowButtonState(
    state: ScheduleContent,
    listState: LazyListState,
    nowScrolling: Boolean,
): NowButtonState? {
    if (nowScrolling) return NowButtonState.Current

    val firstActiveIndex = state.firstActiveIndex
    val lastActiveIndex = state.lastActiveIndex

    if (firstActiveIndex == -1 || lastActiveIndex == -1) return null

    val firstVisible = listState.firstVisibleItemIndex
    val lastVisible = listState.lastVisibleItemIndex

    if (firstVisible == -1 || lastVisible == -1) return null

    val lastFullyVisible = lastVisible - 1
    if (lastFullyVisible < firstActiveIndex) {
        return NowButtonState.Before
    }

    val firstMostlyVisible =
        firstVisible + (if (listState.firstVisibleItemScrollOffset > 50) 1 else 0)
    if (firstMostlyVisible > lastActiveIndex) {
        return NowButtonState.After
    }

    return NowButtonState.Current
}

private val LazyListState.lastVisibleItemIndex
    get() = layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: -1

@Composable
private fun Header(
    startContent: @Composable RowScope.() -> Unit,
    headerState: MainHeaderContainerState,
    onHeaderStateChange: (MainHeaderContainerState) -> Unit,
    bookmarkFilterEnabled: Boolean,
    onBookmarkFilter: (Boolean) -> Unit,
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    onClearSearch: () -> Unit,
    viewModel: ScheduleViewModel,
) {
    MainHeaderContainer(
        state = headerState,
        titleContent = {
            MainHeaderTitleBar(
                title = stringResource(Res.string.nav_destination_schedule),
                startContent = startContent,
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
            NavigationBackHandler(
                state = rememberNavigationEventState(NavigationEventInfo.None),
                isBackEnabled = true,
                onBackCompleted = {
                    onHeaderStateChange(MainHeaderContainerState.Title)
                    onSearchQueryChange("")
                },
            )

            val filterItems by viewModel.filterItems.collectAsStateWithLifecycle()

            MainHeaderSearchBar(
                searchValue = searchQuery,
                // clearing the input should also reset tags
                onSearchValueChange = { onSearchQueryChange(it) },
                onClose = {
                    onHeaderStateChange(MainHeaderContainerState.Title)
                    onSearchQueryChange("")
                    onClearSearch()
                },
                onClear = onClearSearch,
                hasAdditionalInputs = filterItems.any { it.isSelected },
            )
        }
    )
}

@Composable
private fun ScheduleList(
    scheduleItems: List<ScheduleListItem>,
    onSession: (SessionId) -> Unit,
    listState: LazyListState,
    isSearch: Boolean,
    dayInfoMap: Map<LocalDate, DayInfo>,
    onSubmitFeedback: (SessionId, Emotion?) -> Unit,
    onSubmitFeedbackWithComment: (SessionId, Emotion, String) -> Unit,
    onBookmark: (SessionId, Boolean) -> Unit,
    filterItems: List<FilterItem> = emptyList(),
    onToggleFilter: (FilterItem, Boolean) -> Unit = { _, _ -> },
    modifier: Modifier = Modifier,
) {
    ScrollToTopHandler(listState)
    HideKeyboardOnDragHandler(listState)

    LazyColumn(
        state = listState,
        modifier = modifier,
        contentPadding = bottomInsetPadding(),
    ) {
        if (isSearch) {
            item(key = "filters") {
                Filters(
                    tags = filterItems,
                    toggleItem = onToggleFilter,
                    modifier = Modifier.padding(vertical = 16.dp, horizontal = 12.dp),
                )
            }
            item(key = "item-count") {
                Text(
                    text = pluralStringResource(
                        Res.plurals.schedule_number_of_results,
                        scheduleItems.size,
                        scheduleItems.size
                    ),
                    color = KotlinConfTheme.colors.secondaryText,
                    style = KotlinConfTheme.typography.text2,
                    modifier = Modifier
                        .padding(horizontal = 12.dp)
                        .padding(bottom = 4.dp)
                        .semantics { liveRegion = LiveRegionMode.Polite }
                        .animateItem()
                )
            }
        }

        items(
            scheduleItems,
            key = {
                when (it) {
                    is DaySeparatorItem -> it.id
                    is DayHeaderItem -> it.value.date.toString()
                    is ServiceEventGroupItem -> it.value.map { it.id.id }
                    is ServiceEventItem -> it.value.id.id
                    is SessionItem -> it.value.id.id
                    is TimeSlotTitleItem -> it.value.startsAt.toString()
                    is NoBookmarksItem -> it.id
                }
            },
        ) { item ->
            Box(Modifier.animateItem()) {
                when (item) {
                    is DaySeparatorItem -> {
                        Spacer(modifier = Modifier.height(48.dp))
                    }

                    is DayHeaderItem -> {
                        val date = item.value.date
                        val dayInfo = dayInfoMap[date]
                        DayHeader(
                            month = DateTimeFormatting.month(date).uppercase(),
                            day = date.day.toString(),
                            line1 = dayInfo?.line1 ?: "",
                            line2 = dayInfo?.line2 ?: "",
                            fullWidth = true,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 16.dp)
                                .semantics { heading() }
                        )
                    }

                    is TimeSlotTitleItem -> {
                        Text(
                            text = item.value.title,
                            style = KotlinConfTheme.typography.h2,
                            modifier = Modifier
                                .padding(horizontal = 12.dp)
                                .padding(top = 24.dp, bottom = 8.dp)
                                .semantics { heading() }
                        )
                    }

                    is SessionItem -> {
                        SessionCard(
                            session = item.value,
                            isSearch = isSearch,
                            titleHighlights = item.titleHighlights,
                            tagHighlights = item.tagMatches,
                            speakerHighlights = item.speakerHighlights,
                            onBookmark = onBookmark,
                            onSubmitFeedback = onSubmitFeedback,
                            onSubmitFeedbackWithComment = onSubmitFeedbackWithComment,
                            onSession = onSession,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 12.dp, vertical = 8.dp)
                        )
                    }

                    is ServiceEventItem -> {
                        val event = item.value
                        ServiceEvent(
                            event = ServiceEventData(
                                title = event.title,
                                now = event.isLive,
                                note = event.startsInMinutes?.let { count ->
                                    stringResource(Res.string.schedule_in_x_minutes, count)
                                },
                            ),
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                        )
                    }

                    is ServiceEventGroupItem -> {
                        val events = item.value
                        ServiceEvents(
                            events = events.map { event ->
                                ServiceEventData(
                                    title = event.title,
                                    now = event.isLive,
                                    time = event.shortTimeline,
                                    note = event.startsInMinutes?.let { count ->
                                        stringResource(Res.string.schedule_in_x_minutes, count)
                                    },
                                )
                            },
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                        )
                    }

                    is NoBookmarksItem -> {
                        Text(
                            stringResource(Res.string.schedule_label_no_bookmarks),
                            color = KotlinConfTheme.colors.noteText,
                            modifier = modifier.padding(12.dp),
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun SessionCard(
    session: SessionCardView,
    onBookmark: (SessionId, Boolean) -> Unit,
    onSubmitFeedback: (SessionId, Emotion?) -> Unit,
    onSubmitFeedbackWithComment: (SessionId, Emotion, String) -> Unit,
    onSession: (SessionId) -> Unit,
    isSearch: Boolean,
    modifier: Modifier = Modifier,
    titleHighlights: List<IntRange> = emptyList(),
    tagHighlights: List<String> = emptyList(),
    speakerHighlights: List<IntRange> = emptyList(),
) {
    val notificationBar = LocalNotificationBar.current
    val feedbackSentMessage = stringResource(Res.string.session_feedback_sent)
    TalkCard(
        title = session.title,
        titleHighlights = titleHighlights,
        bookmarked = session.isFavorite,
        onBookmark = { isBookmarked -> onBookmark(session.id, isBookmarked) },
        tags = session.tags,
        tagHighlights = tagHighlights,
        speakers = session.speakerLine,
        speakerHighlights = speakerHighlights,
        location = session.locationLine,
        lightning = session.isLightning,
        time = if (isSearch) session.fullTimeline else session.shortTimeline,
        timeNote = session.startsInMinutes?.let { count ->
            stringResource(Res.string.schedule_in_x_minutes, count)
        },
        status = when (session.state) {
            SessionState.Live -> TalkStatus.Live
            SessionState.Past -> TalkStatus.Past
            SessionState.Upcoming -> TalkStatus.Upcoming
        },
        initialEmotion = session.vote?.toEmotion(),
        onSubmitFeedback = { emotion ->
            onSubmitFeedback(session.id, emotion)
        },
        onSubmitFeedbackWithComment = { emotion, comment ->
            onSubmitFeedbackWithComment(session.id, emotion, comment)
            notificationBar.show(feedbackSentMessage)
        },
        onClick = { onSession(session.id) },
        modifier = modifier,
        feedbackEnabled = !isSearch && session.state != SessionState.Upcoming,
    )
}
