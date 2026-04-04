package org.jetbrains.kotlinconf.screens


import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigationevent.NavigationEventInfo
import androidx.navigationevent.compose.NavigationBackHandler
import androidx.navigationevent.compose.rememberNavigationEventState
import dev.zacsweers.metrox.viewmodel.metroViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDate
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.pluralStringResource
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.kotlinconf.Day
import org.jetbrains.kotlinconf.DayInfo
import org.jetbrains.kotlinconf.HideKeyboardOnDragHandler
import org.jetbrains.kotlinconf.ScrollToTopHandler
import org.jetbrains.kotlinconf.SessionCardView
import org.jetbrains.kotlinconf.SessionId
import org.jetbrains.kotlinconf.SessionState
import org.jetbrains.kotlinconf.TimeSlot
import org.jetbrains.kotlinconf.generated.resources.Res
import org.jetbrains.kotlinconf.generated.resources.nav_destination_schedule
import org.jetbrains.kotlinconf.generated.resources.schedule_action_filter_bookmarked
import org.jetbrains.kotlinconf.generated.resources.schedule_action_search
import org.jetbrains.kotlinconf.generated.resources.schedule_error_no_data
import org.jetbrains.kotlinconf.generated.resources.schedule_in_x_minutes
import org.jetbrains.kotlinconf.generated.resources.schedule_label_no_bookmarks
import org.jetbrains.kotlinconf.generated.resources.schedule_number_of_results
import org.jetbrains.kotlinconf.isLive
import org.jetbrains.kotlinconf.isServiceEvent
import org.jetbrains.kotlinconf.navigation.TopLevelRoute
import org.jetbrains.kotlinconf.ui.components.DayHeader
import org.jetbrains.kotlinconf.ui.components.FilterItem
import org.jetbrains.kotlinconf.ui.components.Filters
import org.jetbrains.kotlinconf.ui.components.HeaderToggleButton
import org.jetbrains.kotlinconf.ui.components.HeaderToggleOption
import org.jetbrains.kotlinconf.ui.components.HorizontalDivider
import org.jetbrains.kotlinconf.ui.components.Icon
import org.jetbrains.kotlinconf.ui.components.LargeMainHeader
import org.jetbrains.kotlinconf.ui.components.LargeSearchBar
import org.jetbrains.kotlinconf.ui.components.LargeSwitcher
import org.jetbrains.kotlinconf.ui.components.LargeSwitcherOption
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
import org.jetbrains.kotlinconf.ui.generated.resources.arrow_left_24
import org.jetbrains.kotlinconf.ui.generated.resources.arrow_right_24
import org.jetbrains.kotlinconf.ui.generated.resources.bookmark_24
import org.jetbrains.kotlinconf.ui.generated.resources.search_24
import org.jetbrains.kotlinconf.ui.generated.resources.view_grid_24
import org.jetbrains.kotlinconf.ui.generated.resources.view_list_24
import org.jetbrains.kotlinconf.ui.theme.KotlinConfTheme
import org.jetbrains.kotlinconf.ui.theme.LocalAnimatedContentScope
import org.jetbrains.kotlinconf.utils.DateTimeFormatting
import org.jetbrains.kotlinconf.utils.ErrorLoadingContent
import org.jetbrains.kotlinconf.utils.ErrorLoadingState
import org.jetbrains.kotlinconf.utils.LocalWindowSize
import org.jetbrains.kotlinconf.utils.WindowSize
import org.jetbrains.kotlinconf.utils.bottomInsetPadding
import org.jetbrains.kotlinconf.utils.topInsetPadding

@Composable
fun ScheduleScreen(
    onSession: (SessionId) -> Unit,
    onPrivacyNoticeNeeded: () -> Unit,
    tabReselections: Flow<TopLevelRoute>,
    viewModel: ScheduleViewModel = metroViewModel(),
) {
    val scope = rememberCoroutineScope()
    val windowSize = LocalWindowSize.current
    val isGridViewPreferred by viewModel.isGridViewPreferred.collectAsStateWithLifecycle()
    val isGridView = isGridViewPreferred && windowSize == WindowSize.Large
    var gridSelectedDayIndex by rememberSaveable { mutableStateOf(0) }
    var bookmarkFilterEnabled by rememberSaveable { mutableStateOf(false) }
    var searchQuery by rememberSaveable { mutableStateOf("") }
    val listState = rememberLazyListState()

    val state = viewModel.uiState.collectAsStateWithLifecycle().value

    var headerState by rememberSaveable { mutableStateOf(MainHeaderContainerState.Title) }
    val isSearch = if (windowSize == WindowSize.Large) {
        searchQuery.isNotEmpty()
    } else {
        headerState == MainHeaderContainerState.Search
    }

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

    LaunchedEffect(tabReselections, state) {
        tabReselections.collect {
            val content = (state as? ErrorLoadingState.Content)?.data
            if (content != null && content.firstActiveIndex != -1) {
                listState.animateScrollToItem(content.firstActiveIndex)
            } else {
                listState.animateScrollToItem(0)
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
            viewModel = viewModel,
            isGridView = isGridView,
            onGridViewToggle = { viewModel.setGridViewPreferred(it) },
            showGridToggle = windowSize == WindowSize.Large,
        )

        ErrorLoadingContent(
            state = state,
            errorMessage = stringResource(Res.string.schedule_error_no_data),
            onRetry = { viewModel.refresh() },
            modifier = Modifier.fillMaxSize(),
        ) { content ->
            val days = content.days
            val items = content.items

            Column(
                Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                val dayInfoMap by viewModel.dayInfoMap.collectAsStateWithLifecycle()

                AnimatedVisibility(!isSearch || isGridView) {
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

                    // Sync day selection when switching between list and grid
                    LaunchedEffect(isGridView) {
                        if (isGridView) {
                            gridSelectedDayIndex = computedDayIndex
                        } else if (gridSelectedDayIndex < days.size && gridSelectedDayIndex != computedDayIndex) {
                            val dayItemIndex =
                                items.indexOf(DayHeaderItem(days[gridSelectedDayIndex]))
                            if (dayItemIndex >= 0) {
                                listState.scrollToItem(dayItemIndex)
                            }
                        }
                    }

                    AnimatedContent(
                        targetState = isGridView,
                        transitionSpec = { fadeIn(tween(300)) togetherWith fadeOut(tween(300)) },
                    ) { showGrid ->
                        CompositionLocalProvider(
                            LocalAnimatedContentScope provides this@AnimatedContent,
                        ) {
                            if (showGrid) {
                                LargeSwitcher(
                                    options = remember(conferenceDates, dayInfoMap) {
                                        conferenceDates.map { date ->
                                            val dayInfo = dayInfoMap[date]
                                            LargeSwitcherOption(
                                                label1 = DateTimeFormatting.date(date),
                                                label2 = dayInfo?.combinedLine ?: "",
                                            )
                                        }
                                    },
                                    selectedIndex = gridSelectedDayIndex,
                                    onSelect = { gridSelectedDayIndex = it },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 24.dp, vertical = 8.dp),
                                    sharedTransitionKey = "day-switcher",
                                )
                            } else {
                                Switcher(
                                    items = remember(conferenceDates) {
                                        conferenceDates.map { DateTimeFormatting.date(it) }
                                    },
                                    shortItems = null,
                                    selectedIndex = selectedDayIndex,
                                    onSelect = { index ->
                                        scope.launch {
                                            val dayItemIndex =
                                                items.indexOf(DayHeaderItem(days[index]))
                                            targetDayIndex = index
                                            listState.animateScrollToItem(dayItemIndex)
                                            targetDayIndex = null
                                        }
                                    },
                                    modifier = Modifier
                                        .then(
                                            if (windowSize != WindowSize.Compact)
                                                Modifier.widthIn(max = 640.dp)
                                            else Modifier
                                        )
                                        .fillMaxWidth()
                                        .padding(
                                            horizontal = 12.dp,
                                            vertical = 8.dp,
                                        ),
                                    sharedTransitionKey = "day-switcher",
                                )
                            }
                        }
                    }
                }

                Crossfade(
                    targetState = isGridView,
                    animationSpec = tween(300),
                    modifier = Modifier.fillMaxSize(),
                ) { showGrid ->
                    if (showGrid) {
                        val gridDay = days.getOrElse(gridSelectedDayIndex) { days.first() }
                        // When searching, build a map of matched sessions with highlights
                        val searchMatches: Map<SessionId, SessionItem>? = if (isSearch) {
                            items.filterIsInstance<SessionItem>()
                                .associateBy { it.value.id }
                        } else {
                            null
                        }
                        val venues = remember(gridDay, bookmarkFilterEnabled, searchMatches) {
                            gridDay.timeSlots
                                .flatMap { it.sessions }
                                .filter { !it.isServiceEvent }
                                .filter { !bookmarkFilterEnabled || it.isFavorite }
                                .filter { searchMatches == null || it.id in searchMatches }
                                .map { it.locationLine }
                                .distinct()
                        }
                        ScheduleGrid(
                            day = gridDay,
                            venues = venues,
                            bookmarkFilterEnabled = bookmarkFilterEnabled,
                            searchMatches = searchMatches,
                            onSession = onSession,
                            onBookmark = { sessionId, isBookmarked ->
                                viewModel.onBookmark(sessionId, isBookmarked)
                            },
                        )
                    } else {
                        val tags by viewModel.filterItems.collectAsStateWithLifecycle()
                        ScheduleList(
                            scheduleItems = items,
                            onSession = onSession,
                            listState = listState,
                            isSearch = isSearch,
                            dayInfoMap = dayInfoMap,
                            onBookmark = { sessionId, isBookmarked ->
                                viewModel.onBookmark(sessionId, isBookmarked)
                            },
                            onPrivacyNoticeNeeded = onPrivacyNoticeNeeded,
                            filterItems = tags,
                            onToggleFilter = { item, selected ->
                                viewModel.toggleFilter(
                                    item,
                                    selected
                                )
                            },
                            modifier = Modifier.fillMaxSize(),
                            maxItemWidth = if (windowSize != WindowSize.Compact) 640.dp else null,
                        )
                    }
                }
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
private fun ColumnScope.Header(
    startContent: @Composable RowScope.() -> Unit,
    headerState: MainHeaderContainerState,
    onHeaderStateChange: (MainHeaderContainerState) -> Unit,
    bookmarkFilterEnabled: Boolean,
    onBookmarkFilter: (Boolean) -> Unit,
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    onClearSearch: () -> Unit,
    viewModel: ScheduleViewModel,
    isGridView: Boolean,
    onGridViewToggle: (Boolean) -> Unit,
    showGridToggle: Boolean,
) {
    if (showGridToggle) {
        LargeHeader(
            bookmarkFilterEnabled = bookmarkFilterEnabled,
            onBookmarkFilter = onBookmarkFilter,
            searchQuery = searchQuery,
            onSearchQueryChange = onSearchQueryChange,
            onClearSearch = onClearSearch,
            viewModel = viewModel,
            isGridView = isGridView,
            onGridViewToggle = onGridViewToggle,
        )
    } else {
        SmallHeader(
            startContent = startContent,
            headerState = headerState,
            onHeaderStateChange = onHeaderStateChange,
            bookmarkFilterEnabled = bookmarkFilterEnabled,
            onBookmarkFilter = onBookmarkFilter,
            searchQuery = searchQuery,
            onSearchQueryChange = onSearchQueryChange,
            onClearSearch = onClearSearch,
            viewModel = viewModel,
        )
        HorizontalDivider(
            thickness = 1.dp,
            color = KotlinConfTheme.colors.strokePale,
        )
    }
}

@Composable
private fun LargeHeader(
    bookmarkFilterEnabled: Boolean,
    onBookmarkFilter: (Boolean) -> Unit,
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    onClearSearch: () -> Unit,
    viewModel: ScheduleViewModel,
    isGridView: Boolean,
    onGridViewToggle: (Boolean) -> Unit,
) {
    val filterItems by viewModel.filterItems.collectAsStateWithLifecycle()

    LargeMainHeader(
        title = stringResource(Res.string.nav_destination_schedule),
        endContent = {
            TopMenuButton(
                icon = UiRes.drawable.bookmark_24,
                selected = bookmarkFilterEnabled,
                onToggle = onBookmarkFilter,
                contentDescription = stringResource(Res.string.schedule_action_filter_bookmarked),
                large = true,
            )

            LargeSearchBar(
                searchValue = searchQuery,
                onSearchValueChange = onSearchQueryChange,
                onClear = onClearSearch,
                hasAdditionalInputs = filterItems.any { it.isSelected },
                modifier = Modifier.width(370.dp),
            )

            HeaderToggleButton(
                options = listOf(
                    HeaderToggleOption(UiRes.drawable.view_list_24, "List view"),
                    HeaderToggleOption(UiRes.drawable.view_grid_24, "Grid view"),
                ),
                selectedIndex = if (isGridView) 1 else 0,
                onSelect = { onGridViewToggle(it == 1) },
            )
        }
    )
}

@Composable
private fun SmallHeader(
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
    onBookmark: (SessionId, Boolean) -> Unit,
    onPrivacyNoticeNeeded: () -> Unit,
    filterItems: List<FilterItem> = emptyList(),
    onToggleFilter: (FilterItem, Boolean) -> Unit = { _, _ -> },
    modifier: Modifier = Modifier,
    maxItemWidth: Dp? = null,
) {
    ScrollToTopHandler(listState)
    HideKeyboardOnDragHandler(listState)

    LazyColumn(
        state = listState,
        modifier = modifier,
        contentPadding = bottomInsetPadding(),
        horizontalAlignment = if (maxItemWidth != null) Alignment.CenterHorizontally else Alignment.Start,
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
            Box(
                Modifier
                    .then(if (maxItemWidth != null) Modifier.widthIn(max = maxItemWidth) else Modifier)
                    .fillMaxWidth()
                    .animateItem()
            ) {
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
                            fullWidth = LocalWindowSize.current == WindowSize.Compact,
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
                            onSession = onSession,
                            onPrivacyNoticeNeeded = onPrivacyNoticeNeeded,
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
    onSession: (SessionId) -> Unit,
    onPrivacyNoticeNeeded: () -> Unit,
    isSearch: Boolean,
    modifier: Modifier = Modifier,
    titleHighlights: List<IntRange> = emptyList(),
    tagHighlights: List<String> = emptyList(),
    speakerHighlights: List<IntRange> = emptyList(),
) {
    val status = session.state.toTalkStatus()
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
        status = status,
        onClick = { onSession(session.id) },
        modifier = modifier,
        feedbackContent = if (!isSearch && session.state != SessionState.Upcoming) {
            {
                FeedbackBlock(
                    sessionId = session.id,
                    tags = session.tags,
                    status = status,
                    onPrivacyNoticeNeeded = onPrivacyNoticeNeeded,
                )
            }
        } else null,
    )
}

private fun SessionState.toTalkStatus(): TalkStatus = when (this) {
    SessionState.Live -> TalkStatus.Live
    SessionState.Past -> TalkStatus.Past
    SessionState.Upcoming -> TalkStatus.Upcoming
}

private val TimeLabelWidth = 96.dp
private val ColumnWidth = 250.dp
private val ScrollColumns = 2

@Composable
private fun GridTimeLabel(timeSlot: TimeSlot) {
    Text(
        text = "${DateTimeFormatting.time(timeSlot.startsAt)} –\n${DateTimeFormatting.time(timeSlot.endsAt)}",
        modifier = Modifier.width(TimeLabelWidth).padding(10.dp).padding(start = 14.dp),
        style = KotlinConfTheme.typography.text2,
        color = KotlinConfTheme.colors.secondaryText,
    )
}

@Composable
private fun ScheduleGrid(
    day: Day,
    venues: List<String>,
    bookmarkFilterEnabled: Boolean,
    searchMatches: Map<SessionId, SessionItem>?,
    onSession: (SessionId) -> Unit,
    onBookmark: (SessionId, Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    val horizontalScrollState = rememberScrollState()
    val venuesWidth = ColumnWidth * venues.size

    val canScrollLeft by remember { derivedStateOf { horizontalScrollState.value > 0 } }
    val canScrollRight by remember { derivedStateOf { horizontalScrollState.value < horizontalScrollState.maxValue } }
    val scope = rememberCoroutineScope()
    val density = LocalDensity.current
    val scrollAmount = with(density) { (ColumnWidth * ScrollColumns).toPx().toInt() }

    Column(modifier.fillMaxSize()) {
        // Venue header row (sticky, outside LazyColumn) with inverted background
        Row(
            Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp)
                .background(KotlinConfTheme.colors.tooltipBackground),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            // Left arrow button
            Icon(
                painter = painterResource(UiRes.drawable.arrow_left_24),
                contentDescription = "Scroll left",
                tint = KotlinConfTheme.colors.primaryTextInverted,
                modifier = Modifier
                    .clickable(enabled = canScrollLeft) {
                        scope.launch {
                            horizontalScrollState.animateScrollTo(
                                (horizontalScrollState.value - scrollAmount).coerceAtLeast(0)
                            )
                        }
                    }
                    .background(KotlinConfTheme.colors.mainBackgroundInverted)
                    .alpha(if (canScrollLeft) 1f else 0.3f)
                    .padding(8.dp)
                    .size(24.dp),
            )
            Spacer(Modifier.width(72.dp))
            Row(
                Modifier
                    .weight(1f)
                    .horizontalScroll(horizontalScrollState, overscrollEffect = null)
                    .padding(vertical = 10.dp)
                    .width(venuesWidth)
            ) {
                venues.forEach { venue ->
                    Text(
                        text = venue,
                        style = KotlinConfTheme.typography.h4,
                        color = KotlinConfTheme.colors.primaryTextInverted,
                        modifier = Modifier
                            .width(ColumnWidth)
                            .padding(horizontal = 4.dp),
                    )
                }
            }
            // Right arrow button
            Spacer(Modifier.width(72.dp))
            Icon(
                painter = painterResource(UiRes.drawable.arrow_right_24),
                contentDescription = "Scroll right",
                tint = KotlinConfTheme.colors.primaryTextInverted,
                modifier = Modifier
                    .clickable(enabled = canScrollRight) {
                        scope.launch {
                            horizontalScrollState.animateScrollTo(
                                (horizontalScrollState.value + scrollAmount)
                                    .coerceAtMost(horizontalScrollState.maxValue)
                            )
                        }
                    }
                    .background(KotlinConfTheme.colors.mainBackgroundInverted)
                    .alpha(if (canScrollRight) 1f else 0.3f)
                    .padding(8.dp)
                    .size(24.dp),
            )
        }

        // Scrollable content
        LazyColumn(
            contentPadding = bottomInsetPadding(),
        ) {
            items(day.timeSlots, key = { it.startsAt.toString() }) { timeSlot ->
                Column(Modifier.animateItem()) {
                    val (serviceEvents, allTalks) = timeSlot.sessions.partition { it.isServiceEvent }
                    val talks = allTalks
                        .filter { !bookmarkFilterEnabled || it.isFavorite }
                        .filter { searchMatches == null || it.id in searchMatches }
                    val isServiceSlot = allTalks.isEmpty()

                    if (isServiceSlot) {
                        ServiceEventGridRow(timeSlot, serviceEvents, horizontalScrollState)
                    } else if (bookmarkFilterEnabled && talks.isEmpty()) {
                        Row(Modifier.fillMaxWidth().padding(vertical = 8.dp)) {
                            GridTimeLabel(timeSlot)
                            Text(
                                stringResource(Res.string.schedule_label_no_bookmarks),
                                color = KotlinConfTheme.colors.noteText,
                                modifier = Modifier.padding(12.dp),
                            )
                        }
                    } else {
                        TalkGridRow(
                            timeSlot = timeSlot,
                            talks = talks,
                            serviceEvents = serviceEvents,
                            venues = venues,
                            searchMatches = searchMatches,
                            onSession = onSession,
                            onBookmark = onBookmark,
                            horizontalScrollState = horizontalScrollState,
                        )
                    }
                    HorizontalDivider(
                        thickness = 1.dp,
                        color = KotlinConfTheme.colors.strokePale,
                    )
                }
            }
        }
    }
}

@Composable
private fun ServiceEventGridRow(
    timeSlot: TimeSlot,
    events: List<SessionCardView>,
    horizontalScrollState: ScrollState,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth(),
    ) {
        GridTimeLabel(timeSlot)
        BoxWithConstraints(Modifier.weight(1f)) {
            val availableWidth = maxWidth
            Row(
                Modifier
                    .fillMaxWidth()
                    .horizontalScroll(horizontalScrollState, overscrollEffect = null)
                    .padding(vertical = 8.dp)
                    .padding(end = 12.dp),
            ) {
                val eventModifier = Modifier.widthIn(min = availableWidth - 12.dp)
                if (events.size == 1) {
                    val event = events.first()
                    ServiceEvent(
                        event = ServiceEventData(
                            title = event.title,
                            now = event.isLive,
                            note = event.startsInMinutes?.let { count ->
                                stringResource(Res.string.schedule_in_x_minutes, count)
                            },
                        ),
                        modifier = eventModifier,
                    )
                } else {
                    ServiceEvents(
                        events = events.map {
                            ServiceEventData(
                                title = it.title,
                                now = it.isLive,
                                time = it.shortTimeline,
                                note = it.startsInMinutes?.let { count ->
                                    stringResource(Res.string.schedule_in_x_minutes, count)
                                },
                            )
                        },
                        modifier = eventModifier,
                    )
                }
            }
        }
    }
}

@Composable
private fun TalkGridRow(
    timeSlot: TimeSlot,
    talks: List<SessionCardView>,
    serviceEvents: List<SessionCardView>,
    venues: List<String>,
    searchMatches: Map<SessionId, SessionItem>?,
    onSession: (SessionId) -> Unit,
    onBookmark: (SessionId, Boolean) -> Unit,
    horizontalScrollState: ScrollState,
) {
    val talksByVenue = talks.groupBy { it.locationLine }
    val venuesWidth = ColumnWidth * venues.size

    Row(Modifier.fillMaxWidth()) {
        GridTimeLabel(timeSlot)

        Row(
            Modifier
                .horizontalScroll(horizontalScrollState, overscrollEffect = null)
                .width(venuesWidth)
                .height(IntrinsicSize.Max)
                .padding(vertical = 8.dp)
        ) {
            venues.forEach { venue ->
                val sessionsInVenue = talksByVenue[venue] ?: emptyList()
                Column(
                    modifier = Modifier.width(ColumnWidth).fillMaxHeight()
                        .padding(horizontal = 4.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    sessionsInVenue.forEach { session ->
                        val match = searchMatches?.get(session.id)
                        GridSessionCard(
                            session = session,
                            titleHighlights = match?.titleHighlights ?: emptyList(),
                            speakerHighlights = match?.speakerHighlights ?: emptyList(),
                            onSession = onSession,
                            onBookmark = onBookmark,
                            modifier = Modifier.weight(1f),
                        )
                    }
                }
            }
        }
    }

    if (serviceEvents.isNotEmpty()) {
        ServiceEventGridRow(timeSlot, serviceEvents, horizontalScrollState)
    }
}

@Composable
private fun GridSessionCard(
    session: SessionCardView,
    titleHighlights: List<IntRange>,
    speakerHighlights: List<IntRange>,
    onSession: (SessionId) -> Unit,
    onBookmark: (SessionId, Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    val status = session.state.toTalkStatus()
    TalkCard(
        title = session.title,
        titleHighlights = titleHighlights,
        bookmarked = session.isFavorite,
        onBookmark = { isBookmarked -> onBookmark(session.id, isBookmarked) },
        tags = session.tags,
        tagHighlights = emptyList(),
        speakers = session.speakerLine,
        speakerHighlights = speakerHighlights,
        location = "",
        lightning = session.isLightning,
        time = session.shortTimeline,
        timeNote = null,
        status = status,
        onClick = { onSession(session.id) },
        feedbackContent = null,
        modifier = modifier.fillMaxWidth().fillMaxHeight(),
        stretchContent = true,
    )
}
