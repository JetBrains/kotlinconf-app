package org.jetbrains.kotlinconf.screens


import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinconfapp.shared.generated.resources.Res
import kotlinconfapp.shared.generated.resources.nav_destination_schedule
import kotlinconfapp.shared.generated.resources.schedule_action_filter_bookmarked
import kotlinconfapp.shared.generated.resources.schedule_action_search
import kotlinconfapp.shared.generated.resources.schedule_in_x_minutes
import kotlinconfapp.ui_components.generated.resources.bookmark_24
import kotlinconfapp.ui_components.generated.resources.search_24
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.kotlinconf.DayValues
import org.jetbrains.kotlinconf.ScrollToTopHandler
import org.jetbrains.kotlinconf.SessionCardView
import org.jetbrains.kotlinconf.SessionId
import org.jetbrains.kotlinconf.SessionState
import org.jetbrains.kotlinconf.isLive
import org.jetbrains.kotlinconf.ui.components.DayHeader
import org.jetbrains.kotlinconf.ui.components.Divider
import org.jetbrains.kotlinconf.ui.components.Emotion
import org.jetbrains.kotlinconf.ui.components.Filters
import org.jetbrains.kotlinconf.ui.components.MainHeaderContainer
import org.jetbrains.kotlinconf.ui.components.MainHeaderContainerState
import org.jetbrains.kotlinconf.ui.components.MainHeaderSearchBar
import org.jetbrains.kotlinconf.ui.components.MainHeaderTitleBar
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
import org.koin.compose.viewmodel.koinViewModel
import kotlinconfapp.ui_components.generated.resources.Res as UiRes

@Composable
fun ScheduleScreen(
    onSession: (SessionId) -> Unit,
    onPrivacyPolicyNeeded: () -> Unit,
    viewModel: ScheduleViewModel = koinViewModel(),
) {
    val scope = rememberCoroutineScope()
    var bookmarkFilterEnabled by rememberSaveable { mutableStateOf(false) }
    var searchQuery by rememberSaveable { mutableStateOf("") }
    val listState = rememberLazyListState()

    val days by viewModel.agenda.collectAsStateWithLifecycle()
    val items by viewModel.items.collectAsStateWithLifecycle()
    val shouldNavigateToPrivacyPolicy by viewModel.navigateToPrivacyPolicy.collectAsStateWithLifecycle()

    val firstLiveIndex by viewModel.firstLiveIndex.collectAsStateWithLifecycle()
    val lastLiveIndex by viewModel.lastLiveIndex.collectAsStateWithLifecycle()

    LaunchedEffect(shouldNavigateToPrivacyPolicy) {
        if (shouldNavigateToPrivacyPolicy) {
            onPrivacyPolicyNeeded()
            viewModel.onNavigatedToPrivacyPolicy()
        }
    }

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

    var nowScrolling by remember { mutableStateOf(false) }
    val nowButtonState: NowButtonState? by derivedStateOf {
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

    if (items.isNotEmpty() && firstLiveIndex != -1) {
        LaunchedEffect(Unit) {
            listState.scrollToItem(firstLiveIndex)
        }
    }

    Column(modifier = Modifier.fillMaxSize().background(color = KotlinConfTheme.colors.mainBackground)) {
        Header(
            nowButtonState = nowButtonState,
            onNowClick = {
                scope.launch {
                    nowScrolling = true
                    try {
                        listState.animateScrollToItem(firstLiveIndex)
                    } finally {
                        nowScrolling = false
                    }
                }
            },
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

        ScheduleList(
            scheduleItems = items,
            onSession = onSession,
            listState = listState,
            feedbackEnabled = !isSearch,
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

private val LazyListState.lastVisibleItemIndex
    get() = layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: -1

@Composable
private fun Header(
    nowButtonState: NowButtonState?,
    onNowClick: () -> Unit,
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
                    if (nowButtonState != null) {
                        NowButton(nowButtonState, onNowClick)
                    }
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
    feedbackEnabled: Boolean,
    onSubmitFeedback: (SessionId, Emotion?) -> Unit,
    onSubmitFeedbackWithComment: (SessionId, Emotion, String) -> Unit,
    onBookmark: (SessionId, Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    ScrollToTopHandler(listState)
    LazyColumn(
        state = listState,
        modifier = modifier,
    ) {
        items(scheduleItems) { item ->
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
                        val pagerState = rememberPagerState(pageCount = { workshops.size })
                        HorizontalPager(
                            state = pagerState,
                            modifier = Modifier.fillMaxWidth(),
                            beyondViewportPageCount = 20,
                        ) { page ->
                            SessionCard(
                                session = workshops[page],
                                feedbackEnabled = feedbackEnabled,
                                onBookmark = onBookmark,
                                onSubmitFeedback = onSubmitFeedback,
                                onSubmitFeedbackWithComment = onSubmitFeedbackWithComment,
                                onSession = onSession,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .wrapContentHeight(Alignment.Top)
                                    .padding(horizontal = 12.dp, vertical = 8.dp)
                            )
                        }
                        ScrollIndicator(
                            pageCount = workshops.size,
                            selectedPage = pagerState.currentPage,
                            modifier = Modifier
                                .align(Alignment.CenterHorizontally)
                                .padding(vertical = 8.dp),
                        )
                    }
                }

                is SessionItem -> {
                    SessionCard(
                        session = item.value,
                        feedbackEnabled = feedbackEnabled,
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
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                    )
                }

                is ServiceEventGroupItem -> {
                    val events = item.value
                    ServiceEvents(
                        events = events.map {
                            ServiceEventData(
                                title = it.title,
                                now = it.isLive,
                                time = it.badgeTimeLine,
                                note = it.startsInMinutes?.let { count ->
                                    stringResource(Res.string.schedule_in_x_minutes, count)
                                })
                        },
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 16.dp),
                    )
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
    feedbackEnabled: Boolean,
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
        time = session.badgeTimeLine,
        timeNote = session.startsInMinutes?.let { count ->
            stringResource(Res.string.schedule_in_x_minutes, count)
        },
        status = when (session.state) {
            SessionState.Live -> TalkStatus.Live
            SessionState.Past -> TalkStatus.Past
            SessionState.Upcoming -> TalkStatus.Upcoming
        },
        onSubmitFeedback = { emotion ->
            onSubmitFeedback(session.id, emotion)
        },
        onSubmitFeedbackWithComment = { emotion, comment ->
            onSubmitFeedbackWithComment(session.id, emotion, comment)
        },
        onClick = { onSession(session.id) },
        modifier = modifier,
        feedbackEnabled = feedbackEnabled && session.state != SessionState.Upcoming,
    )
}
