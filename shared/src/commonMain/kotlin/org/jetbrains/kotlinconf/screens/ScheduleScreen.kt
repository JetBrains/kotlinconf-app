package org.jetbrains.kotlinconf.screens


import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.backhandler.BackHandler
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinconfapp.shared.generated.resources.Res
import kotlinconfapp.shared.generated.resources.nav_destination_schedule
import kotlinconfapp.shared.generated.resources.schedule_action_filter_bookmarked
import kotlinconfapp.shared.generated.resources.schedule_action_search
import kotlinconfapp.shared.generated.resources.schedule_error_no_data
import kotlinconfapp.shared.generated.resources.schedule_error_no_results
import kotlinconfapp.shared.generated.resources.schedule_in_x_minutes
import kotlinconfapp.shared.generated.resources.schedule_label_no_bookmarks
import kotlinconfapp.ui_components.generated.resources.bookmark_24
import kotlinconfapp.ui_components.generated.resources.search_24
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.kotlinconf.DayValues
import org.jetbrains.kotlinconf.HideKeyboardOnDragHandler
import org.jetbrains.kotlinconf.LocalFlags
import org.jetbrains.kotlinconf.ScrollToTopHandler
import org.jetbrains.kotlinconf.SessionCardView
import org.jetbrains.kotlinconf.SessionId
import org.jetbrains.kotlinconf.SessionState
import org.jetbrains.kotlinconf.TimeProvider
import org.jetbrains.kotlinconf.isLive
import org.jetbrains.kotlinconf.isProd
import org.jetbrains.kotlinconf.toEmotion
import org.jetbrains.kotlinconf.ui.components.DayHeader
import org.jetbrains.kotlinconf.ui.components.Divider
import org.jetbrains.kotlinconf.ui.components.Emotion
import org.jetbrains.kotlinconf.ui.components.Filters
import org.jetbrains.kotlinconf.ui.components.MainHeaderContainer
import org.jetbrains.kotlinconf.ui.components.MainHeaderContainerState
import org.jetbrains.kotlinconf.ui.components.MainHeaderSearchBar
import org.jetbrains.kotlinconf.ui.components.MainHeaderTitleBar
import org.jetbrains.kotlinconf.ui.components.MinorError
import org.jetbrains.kotlinconf.ui.components.NormalErrorWithLoading
import org.jetbrains.kotlinconf.ui.components.NowButton
import org.jetbrains.kotlinconf.ui.components.NowButtonState
import org.jetbrains.kotlinconf.ui.components.ScrollIndicator
import org.jetbrains.kotlinconf.ui.components.ServiceEvent
import org.jetbrains.kotlinconf.ui.components.ServiceEventData
import org.jetbrains.kotlinconf.ui.components.ServiceEvents
import org.jetbrains.kotlinconf.ui.components.StyledText
import org.jetbrains.kotlinconf.ui.components.Switcher
import org.jetbrains.kotlinconf.ui.components.TalkCard
import org.jetbrains.kotlinconf.ui.components.TalkStatus
import org.jetbrains.kotlinconf.ui.components.TopMenuButton
import org.jetbrains.kotlinconf.ui.theme.KotlinConfTheme
import org.jetbrains.kotlinconf.utils.DateTimeFormatting
import org.jetbrains.kotlinconf.utils.FadingAnimationSpec
import org.koin.compose.koinInject
import org.koin.compose.viewmodel.koinViewModel
import kotlinconfapp.ui_components.generated.resources.Res as UiRes

@Composable
fun ScheduleScreen(
    onSession: (SessionId) -> Unit,
    onPrivacyPolicyNeeded: () -> Unit,
    onRequestFeedbackWithComment: (SessionId) -> Unit,
    viewModel: ScheduleViewModel = koinViewModel(),
) {
    val scope = rememberCoroutineScope()
    var bookmarkFilterEnabled by rememberSaveable { mutableStateOf(false) }
    var searchQuery by rememberSaveable { mutableStateOf("") }
    val listState = rememberLazyListState()

    val state = viewModel.uiState.collectAsStateWithLifecycle().value
    val shouldNavigateToPrivacyPolicy by viewModel.navigateToPrivacyPolicy.collectAsStateWithLifecycle()

    LaunchedEffect(shouldNavigateToPrivacyPolicy) {
        if (shouldNavigateToPrivacyPolicy) {
            onPrivacyPolicyNeeded()
            viewModel.onNavigatedToPrivacyPolicy()
        }
    }

    var headerState by rememberSaveable { mutableStateOf(MainHeaderContainerState.Title) }
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

    // Scroll to first live event on first content load
    var firstScrollPerformed by rememberSaveable { mutableStateOf(false) }
    if (!firstScrollPerformed) {
        LaunchedEffect(state) {
            if (state is ScheduleUiState.Content && state.firstLiveIndex != -1) {
                listState.scrollToItem(state.firstLiveIndex)
                firstScrollPerformed = true
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(color = KotlinConfTheme.colors.mainBackground)
    ) {
        Header(
            startContent = { NowButtonContent(state, listState) },
            headerState = headerState,
            onHeaderStateChange = { headerState = it },
            bookmarkFilterEnabled = bookmarkFilterEnabled,
            onBookmarkFilter = { bookmarkFilterEnabled = it },
            searchQuery = searchQuery,
            onSearchQueryChange = { searchQuery = it },
            onClearSearch = { viewModel.resetFilters() },
            viewModel = viewModel
        )
        Divider(
            thickness = 1.dp,
            color = KotlinConfTheme.colors.strokePale,
        )

        AnimatedContent(
            targetState = state,
            modifier = Modifier.fillMaxSize().clipToBounds(),
            contentKey = {
                when (state) {
                    is ScheduleUiState.Content -> 1
                    ScheduleUiState.Error, ScheduleUiState.Loading -> 2
                }
            },
            transitionSpec = { FadingAnimationSpec },
            contentAlignment = Alignment.Center,
        ) { targetState ->
            when (targetState) {
                is ScheduleUiState.Content -> {
                    val days = targetState.days
                    val items = targetState.items

                    Column(Modifier.fillMaxSize()) {
                        AnimatedContent(
                            isSearch,
                            transitionSpec = {
                                fadeIn(
                                    animationSpec = tween(
                                        90,
                                        delayMillis = 40
                                    )
                                ).togetherWith(fadeOut(animationSpec = tween(40)))
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
                                // Day switcher selection state calculated from the scroll state
                                val conferenceDates = days.map { it.date }
                                val computedDayIndex by derivedStateOf {
                                    val i = items.asSequence()
                                        .take(listState.firstVisibleItemIndex + 1)
                                        .findLast { it is DayHeaderItem }
                                        ?.let {
                                            val visibleDate = (it as DayHeaderItem).value.date
                                            conferenceDates.indexOf(visibleDate)
                                        } ?: 0
                                    i
                                }
                                // Override for the day switcher selection
                                var targetDayIndex by remember { mutableStateOf<Int?>(null) }
                                val selectedDayIndex = targetDayIndex ?: computedDayIndex

                                Switcher(
                                    items = remember(conferenceDates) {
                                        conferenceDates.map { DateTimeFormatting.date(it) }
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

                        AnimatedContent(
                            targetState = items.isNotEmpty(),
                            transitionSpec = { FadingAnimationSpec },
                            contentAlignment = Alignment.Center,
                            modifier = Modifier.weight(1f),
                        ) { hasItems ->
                            if (hasItems) {
                                ScheduleList(
                                    scheduleItems = items,
                                    onSession = onSession,
                                    listState = listState,
                                    feedbackEnabled = !isSearch,
                                    userSignedIn = targetState.userSignedIn,
                                    onSubmitFeedback = { sessionId, emotion ->
                                        viewModel.onSubmitFeedback(sessionId, emotion)
                                    },
                                    onSubmitFeedbackWithComment = { sessionId, emotion, comment ->
                                        viewModel.onSubmitFeedbackWithComment(sessionId, emotion, comment)
                                    },
                                    onRequestFeedbackWithComment = if (LocalFlags.current.redirectFeedbackToSessionPage) {
                                        onRequestFeedbackWithComment
                                    } else {
                                        null
                                    },
                                    onBookmark = { sessionId, isBookmarked ->
                                        viewModel.onBookmark(sessionId, isBookmarked)
                                    },
                                    modifier = Modifier.fillMaxSize().clipToBounds()
                                )
                            } else {
                                MinorError(
                                    message = stringResource(Res.string.schedule_error_no_results),
                                    modifier = Modifier.fillMaxSize(),
                                )
                            }
                        }
                    }
                }

                ScheduleUiState.Error, ScheduleUiState.Loading -> {
                    NormalErrorWithLoading(
                        message = stringResource(Res.string.schedule_error_no_data),
                        isLoading = targetState is ScheduleUiState.Loading,
                        modifier = Modifier.fillMaxSize(),
                        onRetry = { viewModel.refresh() },
                    )
                }
            }
        }
    }
}

@Composable
private fun NowButtonContent(state: ScheduleUiState, listState: LazyListState) {
    if (state !is ScheduleUiState.Content) return

    val scope = rememberCoroutineScope()

    val firstLiveIndex = state.firstLiveIndex
    val lastLiveIndex = state.lastLiveIndex
    var nowScrolling by remember { mutableStateOf(false) }

    val nowButtonState = derivedStateOf {
        when {
            nowScrolling -> NowButtonState.Current

            firstLiveIndex == -1 || lastLiveIndex == -1 -> null

            else -> {
                val firstMostlyVisible = listState.firstVisibleItemIndex +
                        (if (listState.firstVisibleItemScrollOffset > 50) 1 else 0)
                val lastFullyVisible = listState.lastVisibleItemIndex - 1

                when {
                    lastFullyVisible < firstLiveIndex -> NowButtonState.Before
                    firstMostlyVisible > lastLiveIndex -> NowButtonState.After
                    else -> NowButtonState.Current
                }
            }
        }
    }.value

    if (nowButtonState != null) {
        NowButton(
            time = nowButtonState,
            onClick = {
                scope.launch {
                    nowScrolling = true
                    try {
                        listState.animateScrollToItem(firstLiveIndex)
                    } finally {
                        nowScrolling = false
                    }
                }
            }
        )
    }
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
    viewModel: ScheduleViewModel = koinViewModel(),
) {
    MainHeaderContainer(
        state = headerState,
        titleContent = {
            MainHeaderTitleBar(
                title = if (!isProd() && LocalFlags.current.useFakeTime) {
                    val dateTime by koinInject<TimeProvider>().time.collectAsStateWithLifecycle()
                    "Fake time: ${DateTimeFormatting.dateAndTime(dateTime)}"
                } else {
                    stringResource(Res.string.nav_destination_schedule)
                },
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
            @OptIn(ExperimentalComposeUiApi::class)
            BackHandler(true) {
                onHeaderStateChange(MainHeaderContainerState.Title)
                onSearchQueryChange("")
            }
            val filterItems by viewModel.filterItems.collectAsState()

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
    feedbackEnabled: Boolean,
    userSignedIn: Boolean,
    onSubmitFeedback: (SessionId, Emotion?) -> Unit,
    onSubmitFeedbackWithComment: (SessionId, Emotion, String) -> Unit,
    onRequestFeedbackWithComment: ((SessionId) -> Unit)?,
    onBookmark: (SessionId, Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    ScrollToTopHandler(listState)
    HideKeyboardOnDragHandler(listState)

    LazyColumn(
        state = listState,
        modifier = modifier,
    ) {
        items(
            scheduleItems,
            key = {
                when (it) {
                    is DayHeaderItem -> it.value.date.toString()
                    is ServiceEventGroupItem -> it.value.map { it.id.id }
                    is ServiceEventItem -> it.value.id.id
                    is SessionItem -> it.value.id.id
                    is TimeSlotTitleItem -> it.value.startsAt.toString()
                    is WorkshopItem -> "workshops"
                    is NoBookmarksItem -> it.id
                }
            },
        ) { item ->
            Box(Modifier.animateItem()) {
                when (item) {
                    is DayHeaderItem -> {
                        val date = item.value.date
                        val dayValues = DayValues.map[date]
                        DayHeader(
                            month = DateTimeFormatting.month(date).uppercase(),
                            day = date.dayOfMonth.toString(),
                            line1 = dayValues?.line1 ?: "",
                            line2 = dayValues?.line2 ?: "",
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 8.dp)
                        )
                    }

                    is TimeSlotTitleItem -> {
                        StyledText(
                            text = item.value.title,
                            style = KotlinConfTheme.typography.h2,
                            modifier = Modifier
                                .padding(horizontal = 12.dp)
                                .padding(top = 24.dp, bottom = 8.dp)
                        )
                    }

                    is WorkshopItem -> {
                        val workshops = item.workshops
                        Column(modifier = Modifier.fillMaxWidth()) {
                            if (workshops.size == 1) {
                                SessionCard(
                                    session = workshops[0],
                                    feedbackEnabled = feedbackEnabled,
                                    userSignedIn = userSignedIn,
                                    onBookmark = onBookmark,
                                    onSubmitFeedback = onSubmitFeedback,
                                    onSubmitFeedbackWithComment = onSubmitFeedbackWithComment,
                                    onRequestFeedbackWithComment = onRequestFeedbackWithComment,
                                    onSession = onSession,
                                    modifier = Modifier
                                        .padding(horizontal = 32.dp)
                                        .padding(top = 16.dp, bottom = 16.dp)
                                )
                            } else {
                                val pagerState = rememberPagerState(
                                    pageCount = { Int.MAX_VALUE }, // Pretend we have "infinite" pages
                                    initialPage = Int.MAX_VALUE / 2, // Start from the middle
                                )
                                HorizontalPager(
                                    state = pagerState,
                                    modifier = Modifier.fillMaxWidth(),
                                    beyondViewportPageCount = 1,
                                    contentPadding = PaddingValues(horizontal = 24.dp),
                                ) { pageIndex ->
                                    SessionCard(
                                        session = workshops[pageIndex % workshops.size],
                                        feedbackEnabled = feedbackEnabled,
                                        userSignedIn = userSignedIn,
                                        onBookmark = onBookmark,
                                        onSubmitFeedback = onSubmitFeedback,
                                        onSubmitFeedbackWithComment = onSubmitFeedbackWithComment,
                                        onRequestFeedbackWithComment = onRequestFeedbackWithComment,
                                        onSession = onSession,
                                        modifier = Modifier
                                            .padding(horizontal = 8.dp, vertical = 8.dp)
                                    )
                                }
                                ScrollIndicator(
                                    pageCount = workshops.size,
                                    selectedPage = pagerState.currentPage % workshops.size,
                                    modifier = Modifier
                                        .align(Alignment.CenterHorizontally)
                                        .padding(vertical = 8.dp),
                                )
                            }
                        }
                    }

                    is SessionItem -> {
                        SessionCard(
                            session = item.value,
                            feedbackEnabled = feedbackEnabled,
                            userSignedIn = userSignedIn,
                            titleHighlights = item.titleHighlights,
                            tagHighlights = item.tagMatches,
                            speakerHighlights = item.speakerHighlights,
                            onBookmark = onBookmark,
                            onSubmitFeedback = onSubmitFeedback,
                            onSubmitFeedbackWithComment = onSubmitFeedbackWithComment,
                            onRequestFeedbackWithComment = onRequestFeedbackWithComment,
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
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
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
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 16.dp),
                        )
                    }

                    is NoBookmarksItem -> {
                        StyledText(
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
    onRequestFeedbackWithComment: ((SessionId) -> Unit)?,
    onSubmitFeedbackWithComment: (SessionId, Emotion, String) -> Unit,
    onSession: (SessionId) -> Unit,
    feedbackEnabled: Boolean,
    userSignedIn: Boolean,
    modifier: Modifier = Modifier,
    titleHighlights: List<IntRange> = emptyList(),
    tagHighlights: List<String> = emptyList(),
    speakerHighlights: List<IntRange> = emptyList(),
) {
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
        time = session.shortTimeline,
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
        onRequestFeedbackWithComment = if (onRequestFeedbackWithComment != null) {
            { onRequestFeedbackWithComment(session.id) }
        } else {
            null
        },
        onSubmitFeedbackWithComment = { emotion, comment ->
            onSubmitFeedbackWithComment(session.id, emotion, comment)
        },
        onClick = { onSession(session.id) },
        modifier = modifier,
        feedbackEnabled = feedbackEnabled && session.state != SessionState.Upcoming,
        userSignedIn = userSignedIn,
    )
}
