package org.jetbrains.kotlinconf.screens

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dev.zacsweers.metrox.viewmodel.metroViewModel
import kotlinx.coroutines.delay
import org.jetbrains.compose.resources.pluralStringResource
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.kotlinconf.flags.LocalFlags
import org.jetbrains.kotlinconf.generated.resources.Res
import org.jetbrains.kotlinconf.generated.resources.close_24
import org.jetbrains.kotlinconf.generated.resources.wrapped_bookmarks_body
import org.jetbrains.kotlinconf.generated.resources.wrapped_bookmarks_count
import org.jetbrains.kotlinconf.generated.resources.wrapped_bookmarks_early
import org.jetbrains.kotlinconf.generated.resources.wrapped_bookmarks_lightning
import org.jetbrains.kotlinconf.generated.resources.wrapped_bookmarks_title
import org.jetbrains.kotlinconf.generated.resources.wrapped_close
import org.jetbrains.kotlinconf.generated.resources.wrapped_day_body
import org.jetbrains.kotlinconf.generated.resources.wrapped_day_title
import org.jetbrains.kotlinconf.generated.resources.wrapped_done
import org.jetbrains.kotlinconf.generated.resources.wrapped_duration
import org.jetbrains.kotlinconf.generated.resources.wrapped_empty_body
import org.jetbrains.kotlinconf.generated.resources.wrapped_empty_title
import org.jetbrains.kotlinconf.generated.resources.wrapped_event_days
import org.jetbrains.kotlinconf.generated.resources.wrapped_event_hours
import org.jetbrains.kotlinconf.generated.resources.wrapped_event_rooms
import org.jetbrains.kotlinconf.generated.resources.wrapped_event_speakers
import org.jetbrains.kotlinconf.generated.resources.wrapped_event_talks
import org.jetbrains.kotlinconf.generated.resources.wrapped_event_title
import org.jetbrains.kotlinconf.generated.resources.wrapped_event_top_topics
import org.jetbrains.kotlinconf.generated.resources.wrapped_intro_body
import org.jetbrains.kotlinconf.generated.resources.wrapped_intro_body_ongoing
import org.jetbrains.kotlinconf.generated.resources.wrapped_intro_eyebrow
import org.jetbrains.kotlinconf.generated.resources.wrapped_intro_title
import org.jetbrains.kotlinconf.generated.resources.wrapped_loading
import org.jetbrains.kotlinconf.generated.resources.wrapped_outro_body
import org.jetbrains.kotlinconf.generated.resources.wrapped_outro_bookmarked
import org.jetbrains.kotlinconf.generated.resources.wrapped_outro_rated
import org.jetbrains.kotlinconf.generated.resources.wrapped_outro_title
import org.jetbrains.kotlinconf.generated.resources.wrapped_personality_explorer
import org.jetbrains.kotlinconf.generated.resources.wrapped_personality_explorer_body
import org.jetbrains.kotlinconf.generated.resources.wrapped_personality_free_spirit
import org.jetbrains.kotlinconf.generated.resources.wrapped_personality_free_spirit_body
import org.jetbrains.kotlinconf.generated.resources.wrapped_personality_lightning_fan
import org.jetbrains.kotlinconf.generated.resources.wrapped_personality_lightning_fan_body
import org.jetbrains.kotlinconf.generated.resources.wrapped_personality_planner
import org.jetbrains.kotlinconf.generated.resources.wrapped_personality_planner_body
import org.jetbrains.kotlinconf.generated.resources.wrapped_personality_reviewer
import org.jetbrains.kotlinconf.generated.resources.wrapped_personality_reviewer_body
import org.jetbrains.kotlinconf.generated.resources.wrapped_personality_title
import org.jetbrains.kotlinconf.generated.resources.wrapped_share
import org.jetbrains.kotlinconf.generated.resources.wrapped_share_copied
import org.jetbrains.kotlinconf.generated.resources.wrapped_share_text
import org.jetbrains.kotlinconf.generated.resources.wrapped_speakers_count
import org.jetbrains.kotlinconf.generated.resources.wrapped_speakers_title
import org.jetbrains.kotlinconf.generated.resources.wrapped_tap_hint
import org.jetbrains.kotlinconf.generated.resources.wrapped_topic_body
import org.jetbrains.kotlinconf.generated.resources.wrapped_topic_title
import org.jetbrains.kotlinconf.generated.resources.wrapped_usage_launches
import org.jetbrains.kotlinconf.generated.resources.wrapped_usage_most_viewed
import org.jetbrains.kotlinconf.generated.resources.wrapped_usage_most_viewed_count
import org.jetbrains.kotlinconf.generated.resources.wrapped_usage_session_views
import org.jetbrains.kotlinconf.generated.resources.wrapped_usage_speaker_views
import org.jetbrains.kotlinconf.generated.resources.wrapped_usage_title
import org.jetbrains.kotlinconf.generated.resources.wrapped_usage_unique_sessions
import org.jetbrains.kotlinconf.generated.resources.wrapped_votes_bad
import org.jetbrains.kotlinconf.generated.resources.wrapped_votes_count
import org.jetbrains.kotlinconf.generated.resources.wrapped_votes_good
import org.jetbrains.kotlinconf.generated.resources.wrapped_votes_ok
import org.jetbrains.kotlinconf.generated.resources.wrapped_votes_positive
import org.jetbrains.kotlinconf.generated.resources.wrapped_votes_title
import org.jetbrains.kotlinconf.ui.components.Button
import org.jetbrains.kotlinconf.ui.components.Emotion
import org.jetbrains.kotlinconf.ui.components.KodeeEmotion
import org.jetbrains.kotlinconf.ui.components.KodeeIconSmall
import org.jetbrains.kotlinconf.ui.components.SpeakerAvatar
import org.jetbrains.kotlinconf.ui.components.Text
import org.jetbrains.kotlinconf.ui.components.TopMenuButton
import org.jetbrains.kotlinconf.ui.theme.KotlinConfDarkColors
import org.jetbrains.kotlinconf.ui.theme.KotlinConfTheme
import org.jetbrains.kotlinconf.utils.DateTimeFormatting
import org.jetbrains.kotlinconf.utils.bottomInsetPadding
import org.jetbrains.kotlinconf.utils.topInsetPadding
import org.jetbrains.kotlinconf.wrapped.AttendeePersonality
import org.jetbrains.kotlinconf.wrapped.WrappedStats
import kotlin.math.roundToInt
import kotlin.time.Duration
import kotlin.time.Duration.Companion.hours

private const val SLIDE_DURATION_MS = 8_000
private const val SWIPE_THRESHOLD_PX = 80f
private val CONTENT_MAX_WIDTH = 480.dp

/**
 * Fixed palette for the Wrapped story. The story always renders on dark, colorful
 * backgrounds regardless of the app theme, like the Golden Kodee screen.
 */
private object WrappedPalette {
    val night = Color(0xFF19191C)
    val deepPurple = Color(0xFF3A0057)
    val purple = Color(0xFF8F00E7)
    val magenta = Color(0xFFC202D7)
    val pink = Color(0xFFE00189)
    val orange = Color(0xFFFF5A13)

    val textPrimary = Color.White
    val textSecondary = Color(0xB2FFFFFF)
    val trackBackground = Color(0x4DFFFFFF)
    val chipBackground = Color(0x33FFFFFF)
}

private enum class WrappedSlide(val topColor: Color, val bottomColor: Color) {
    Intro(WrappedPalette.deepPurple, WrappedPalette.purple),
    EventNumbers(WrappedPalette.purple, WrappedPalette.magenta),
    Bookmarks(WrappedPalette.magenta, WrappedPalette.pink),
    TopTopic(WrappedPalette.pink, WrappedPalette.magenta),
    BusiestDay(WrappedPalette.purple, WrappedPalette.pink),
    TopSpeakers(WrappedPalette.deepPurple, WrappedPalette.magenta),
    Votes(WrappedPalette.pink, WrappedPalette.purple),
    Usage(WrappedPalette.purple, WrappedPalette.deepPurple),
    Empty(WrappedPalette.deepPurple, WrappedPalette.night),
    Personality(WrappedPalette.magenta, WrappedPalette.purple),
    Outro(WrappedPalette.night, WrappedPalette.deepPurple),
}

private fun buildSlides(stats: WrappedStats): List<WrappedSlide> = buildList {
    add(WrappedSlide.Intro)
    add(WrappedSlide.EventNumbers)
    if (stats.isEmpty) {
        add(WrappedSlide.Empty)
    } else {
        if (stats.bookmarks.count > 0) add(WrappedSlide.Bookmarks)
        if (stats.bookmarks.topTopic != null) add(WrappedSlide.TopTopic)
        if (stats.bookmarks.busiestDay != null && stats.event.dayCount > 1) add(WrappedSlide.BusiestDay)
        if (stats.bookmarks.topSpeakers.isNotEmpty()) add(WrappedSlide.TopSpeakers)
        if (stats.votes.total > 0) add(WrappedSlide.Votes)
        if (stats.usage.totalSessionViews > 0 || stats.usage.totalSpeakerViews > 0) add(WrappedSlide.Usage)
    }
    add(WrappedSlide.Personality)
    add(WrappedSlide.Outro)
}

@Composable
fun WrappedScreen(
    onBack: () -> Unit,
    viewModel: WrappedViewModel = metroViewModel(),
) {
    val stats = viewModel.stats.collectAsStateWithLifecycle().value

    KotlinConfTheme(
        colors = KotlinConfDarkColors,
        rippleEnabled = LocalFlags.current.rippleEnabled,
    ) {
        Box(
            Modifier
                .fillMaxSize()
                .background(WrappedPalette.night)
        ) {
            if (stats == null) {
                LoadingContent(onClose = onBack)
            } else {
                WrappedStory(stats = stats, onClose = onBack)
            }
        }
    }
}

@Composable
private fun LoadingContent(onClose: () -> Unit) {
    Box(Modifier.fillMaxSize()) {
        CloseButton(
            onClose = onClose,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(topInsetPadding())
                .padding(12.dp),
        )
        Text(
            text = stringResource(Res.string.wrapped_loading),
            style = KotlinConfTheme.typography.h3,
            color = WrappedPalette.textSecondary,
            modifier = Modifier.align(Alignment.Center),
        )
    }
}

@Composable
private fun CloseButton(onClose: () -> Unit, modifier: Modifier = Modifier) {
    TopMenuButton(
        icon = Res.drawable.close_24,
        contentDescription = stringResource(Res.string.wrapped_close),
        onClick = onClose,
        modifier = modifier,
    )
}

@Composable
private fun WrappedStory(
    stats: WrappedStats,
    onClose: () -> Unit,
) {
    val slides = remember(stats) { buildSlides(stats) }
    var page by rememberSaveable { mutableStateOf(0) }
    val currentPage = page.coerceIn(0, slides.lastIndex)
    val isLastPage = currentPage == slides.lastIndex

    val progress = remember { Animatable(0f) }
    var paused by remember { mutableStateOf(false) }
    var animatedPage by remember { mutableIntStateOf(-1) }

    fun goTo(index: Int) {
        page = index.coerceIn(0, slides.lastIndex)
    }

    LaunchedEffect(currentPage, paused, slides.size) {
        if (animatedPage != currentPage) {
            progress.snapTo(0f)
            animatedPage = currentPage
        }
        if (paused || isLastPage) return@LaunchedEffect

        val remainingMillis = ((1f - progress.value) * SLIDE_DURATION_MS).toInt().coerceAtLeast(1)
        progress.animateTo(1f, tween(remainingMillis, easing = LinearEasing))
        goTo(currentPage + 1)
    }

    val slide = slides[currentPage]

    Box(
        Modifier
            .fillMaxSize()
            .pointerInput(slides.size) {
                detectTapGestures(
                    onPress = {
                        paused = true
                        tryAwaitRelease()
                        paused = false
                    },
                    onTap = { offset ->
                        if (offset.x < size.width / 3f) goTo(page - 1) else goTo(page + 1)
                    },
                )
            }
            .pointerInput(slides.size) {
                var dragTotal = 0f
                detectHorizontalDragGestures(
                    onDragStart = { dragTotal = 0f },
                    onDragEnd = {
                        when {
                            dragTotal < -SWIPE_THRESHOLD_PX -> goTo(page + 1)
                            dragTotal > SWIPE_THRESHOLD_PX -> goTo(page - 1)
                        }
                    },
                    onHorizontalDrag = { _, dragAmount -> dragTotal += dragAmount },
                )
            }
    ) {
        AnimatedContent(
            targetState = currentPage,
            transitionSpec = {
                val forward = targetState > initialState
                val enter = fadeIn(tween(350)) +
                        slideInHorizontally(tween(350)) { if (forward) it / 10 else -it / 10 }
                val exit = fadeOut(tween(250)) +
                        slideOutHorizontally(tween(250)) { if (forward) -it / 10 else it / 10 }
                enter togetherWith exit
            },
            modifier = Modifier.fillMaxSize(),
        ) { index ->
            val target = slides.getOrNull(index) ?: slide
            SlideContent(
                slide = target,
                stats = stats,
                onDone = onClose,
                modifier = Modifier
                    .fillMaxSize()
                    .background(Brush.verticalGradient(listOf(target.topColor, target.bottomColor))),
            )
        }

        Column(
            Modifier
                .fillMaxWidth()
                .padding(topInsetPadding())
                .padding(horizontal = 12.dp, vertical = 8.dp)
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                slides.indices.forEach { index ->
                    val fraction = when {
                        index < currentPage -> 1f
                        index == currentPage -> if (isLastPage) 1f else progress.value
                        else -> 0f
                    }
                    ProgressSegment(fraction, Modifier.weight(1f))
                }
            }
            Row(
                Modifier.fillMaxWidth().padding(top = 4.dp),
                horizontalArrangement = Arrangement.End,
            ) {
                CloseButton(onClose = onClose)
            }
        }
    }
}

@Composable
private fun ProgressSegment(fraction: Float, modifier: Modifier = Modifier) {
    val shape = RoundedCornerShape(percent = 50)
    Box(
        modifier
            .height(3.dp)
            .clip(shape)
            .background(WrappedPalette.trackBackground)
    ) {
        Box(
            Modifier
                .fillMaxHeight()
                .fillMaxWidth(fraction.coerceIn(0f, 1f))
                .background(WrappedPalette.textPrimary)
        )
    }
}

@Composable
private fun SlideContent(
    slide: WrappedSlide,
    stats: WrappedStats,
    onDone: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier
            .padding(topInsetPadding())
            .padding(bottomInsetPadding())
            // Leave room for the progress bar and close button at the top
            .padding(top = 72.dp, bottom = 24.dp)
            .padding(horizontal = 24.dp),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            Modifier
                .widthIn(max = CONTENT_MAX_WIDTH)
                .fillMaxWidth()
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.Center,
        ) {
            when (slide) {
                WrappedSlide.Intro -> IntroSlide(stats)
                WrappedSlide.EventNumbers -> EventNumbersSlide(stats)
                WrappedSlide.Bookmarks -> BookmarksSlide(stats)
                WrappedSlide.TopTopic -> TopTopicSlide(stats)
                WrappedSlide.BusiestDay -> BusiestDaySlide(stats)
                WrappedSlide.TopSpeakers -> TopSpeakersSlide(stats)
                WrappedSlide.Votes -> VotesSlide(stats)
                WrappedSlide.Usage -> UsageSlide(stats)
                WrappedSlide.Empty -> EmptySlide()
                WrappedSlide.Personality -> PersonalitySlide(stats)
                WrappedSlide.Outro -> OutroSlide(stats, onDone)
            }
        }
    }
}

// Typography helpers

private val heroNumberStyle: TextStyle
    @Composable get() = KotlinConfTheme.typography.h1.copy(
        fontSize = 88.sp,
        lineHeight = 92.sp,
        fontWeight = FontWeight.Bold,
    )

private val heroTextStyle: TextStyle
    @Composable get() = KotlinConfTheme.typography.h1.copy(
        fontSize = 44.sp,
        lineHeight = 50.sp,
        fontWeight = FontWeight.Bold,
    )

@Composable
private fun SlideTitle(text: String) {
    Text(
        text = text,
        style = KotlinConfTheme.typography.h2,
        color = WrappedPalette.textSecondary,
    )
    Spacer(Modifier.height(16.dp))
}

@Composable
private fun SlideBody(text: String, modifier: Modifier = Modifier) {
    Text(
        text = text,
        style = KotlinConfTheme.typography.text1,
        color = WrappedPalette.textPrimary,
        modifier = modifier,
    )
}

@Composable
private fun SlideNote(text: String, modifier: Modifier = Modifier) {
    Text(
        text = text,
        style = KotlinConfTheme.typography.text2,
        color = WrappedPalette.textSecondary,
        modifier = modifier,
    )
}

/**
 * A number that counts up from zero when it first appears.
 */
@Composable
private fun CountUpText(
    target: Int,
    modifier: Modifier = Modifier,
    style: TextStyle = heroNumberStyle,
) {
    val animated = remember { Animatable(0f) }
    LaunchedEffect(target) {
        animated.animateTo(target.toFloat(), tween(1_200, easing = FastOutSlowInEasing))
    }
    Text(
        text = animated.value.roundToInt().toString(),
        style = style,
        color = WrappedPalette.textPrimary,
        modifier = modifier,
    )
}

@Composable
private fun StatTile(
    value: Int,
    label: String,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier
            .clip(KotlinConfTheme.shapes.roundedCornerMd)
            .background(WrappedPalette.chipBackground)
            .padding(16.dp)
    ) {
        CountUpText(
            target = value,
            style = KotlinConfTheme.typography.h1.copy(fontSize = 40.sp, lineHeight = 44.sp),
        )
        Text(
            text = label,
            style = KotlinConfTheme.typography.text2,
            color = WrappedPalette.textSecondary,
        )
    }
}

@Composable
private fun Chip(text: String, modifier: Modifier = Modifier) {
    Text(
        text = text,
        style = KotlinConfTheme.typography.h4,
        color = WrappedPalette.night,
        modifier = modifier
            .clip(RoundedCornerShape(percent = 50))
            .background(WrappedPalette.orange)
            .padding(horizontal = 12.dp, vertical = 6.dp),
    )
}

@Composable
private fun formatDuration(duration: Duration): String {
    val hours = duration.inWholeHours
    val minutes = (duration - hours.hours).inWholeMinutes
    return stringResource(Res.string.wrapped_duration, hours.toString(), minutes.toString())
}

@Composable
private fun personalityName(personality: AttendeePersonality): String = stringResource(
    when (personality) {
        AttendeePersonality.Planner -> Res.string.wrapped_personality_planner
        AttendeePersonality.Reviewer -> Res.string.wrapped_personality_reviewer
        AttendeePersonality.Explorer -> Res.string.wrapped_personality_explorer
        AttendeePersonality.LightningFan -> Res.string.wrapped_personality_lightning_fan
        AttendeePersonality.FreeSpirit -> Res.string.wrapped_personality_free_spirit
    }
)

@Composable
private fun personalityDescription(personality: AttendeePersonality): String = stringResource(
    when (personality) {
        AttendeePersonality.Planner -> Res.string.wrapped_personality_planner_body
        AttendeePersonality.Reviewer -> Res.string.wrapped_personality_reviewer_body
        AttendeePersonality.Explorer -> Res.string.wrapped_personality_explorer_body
        AttendeePersonality.LightningFan -> Res.string.wrapped_personality_lightning_fan_body
        AttendeePersonality.FreeSpirit -> Res.string.wrapped_personality_free_spirit_body
    }
)

// Slides

@Composable
private fun IntroSlide(stats: WrappedStats) {
    Text(
        text = stringResource(Res.string.wrapped_intro_eyebrow, stats.year.toString()),
        style = KotlinConfTheme.typography.h3,
        color = WrappedPalette.textSecondary,
    )
    Spacer(Modifier.height(12.dp))
    Text(
        text = stringResource(Res.string.wrapped_intro_title),
        style = heroTextStyle,
        color = WrappedPalette.textPrimary,
    )
    Spacer(Modifier.height(24.dp))
    SlideBody(
        if (stats.isConferenceOver) stringResource(Res.string.wrapped_intro_body)
        else stringResource(Res.string.wrapped_intro_body_ongoing)
    )
    Spacer(Modifier.height(48.dp))
    SlideNote(stringResource(Res.string.wrapped_tap_hint))
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun EventNumbersSlide(stats: WrappedStats) {
    val event = stats.event
    SlideTitle(stringResource(Res.string.wrapped_event_title))
    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        StatTile(event.talkCount, stringResource(Res.string.wrapped_event_talks), Modifier.weight(1f))
        StatTile(event.speakerCount, stringResource(Res.string.wrapped_event_speakers), Modifier.weight(1f))
    }
    Spacer(Modifier.height(12.dp))
    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        StatTile(event.dayCount, stringResource(Res.string.wrapped_event_days), Modifier.weight(1f))
        StatTile(event.roomCount, stringResource(Res.string.wrapped_event_rooms), Modifier.weight(1f))
    }
    Spacer(Modifier.height(24.dp))
    Row(verticalAlignment = Alignment.Bottom, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(
            text = formatDuration(event.totalContentDuration),
            style = KotlinConfTheme.typography.h1,
            color = WrappedPalette.textPrimary,
        )
        Text(
            text = stringResource(Res.string.wrapped_event_hours),
            style = KotlinConfTheme.typography.text1,
            color = WrappedPalette.textSecondary,
            modifier = Modifier.padding(bottom = 2.dp),
        )
    }
    if (event.topTopics.isNotEmpty()) {
        Spacer(Modifier.height(24.dp))
        SlideNote(stringResource(Res.string.wrapped_event_top_topics))
        Spacer(Modifier.height(8.dp))
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            event.topTopics.forEach { topic -> Chip(topic.tag) }
        }
    }
}

@Composable
private fun BookmarksSlide(stats: WrappedStats) {
    val bookmarks = stats.bookmarks
    SlideTitle(stringResource(Res.string.wrapped_bookmarks_title))
    CountUpText(bookmarks.count)
    Text(
        text = pluralStringResource(Res.plurals.wrapped_bookmarks_count, bookmarks.count),
        style = KotlinConfTheme.typography.h2,
        color = WrappedPalette.textPrimary,
    )
    Spacer(Modifier.height(24.dp))
    SlideBody(
        stringResource(
            Res.string.wrapped_bookmarks_body,
            (bookmarks.shareOfTalks * 100).roundToInt(),
            formatDuration(bookmarks.totalDuration),
        )
    )
    if (bookmarks.lightningTalkCount > 0) {
        Spacer(Modifier.height(12.dp))
        SlideNote(
            pluralStringResource(
                Res.plurals.wrapped_bookmarks_lightning,
                bookmarks.lightningTalkCount,
                bookmarks.lightningTalkCount,
            )
        )
    }
    val earliest = bookmarks.earliestStart
    if (earliest != null) {
        Spacer(Modifier.height(12.dp))
        SlideNote(stringResource(Res.string.wrapped_bookmarks_early, DateTimeFormatting.dateAndTime(earliest)))
    }
}

@Composable
private fun TopTopicSlide(stats: WrappedStats) {
    val topic = stats.bookmarks.topTopic ?: return
    SlideTitle(stringResource(Res.string.wrapped_topic_title))
    Text(
        text = topic.tag,
        style = heroTextStyle,
        color = WrappedPalette.textPrimary,
    )
    Spacer(Modifier.height(24.dp))
    SlideBody(pluralStringResource(Res.plurals.wrapped_topic_body, topic.count, topic.count))
}

@Composable
private fun BusiestDaySlide(stats: WrappedStats) {
    val day = stats.bookmarks.busiestDay ?: return
    SlideTitle(stringResource(Res.string.wrapped_day_title))
    Text(
        text = DateTimeFormatting.date(day.date),
        style = heroTextStyle,
        color = WrappedPalette.textPrimary,
    )
    Spacer(Modifier.height(24.dp))
    SlideBody(pluralStringResource(Res.plurals.wrapped_day_body, day.count, day.count))
}

@Composable
private fun TopSpeakersSlide(stats: WrappedStats) {
    SlideTitle(stringResource(Res.string.wrapped_speakers_title))
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        stats.bookmarks.topSpeakers.forEach { entry ->
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                SpeakerAvatar(
                    photoUrl = entry.speaker.photoUrl,
                    modifier = Modifier.size(64.dp),
                )
                Column {
                    Text(
                        text = entry.speaker.name,
                        style = KotlinConfTheme.typography.h3,
                        color = WrappedPalette.textPrimary,
                    )
                    Text(
                        text = pluralStringResource(Res.plurals.wrapped_speakers_count, entry.count, entry.count),
                        style = KotlinConfTheme.typography.text2,
                        color = WrappedPalette.textSecondary,
                    )
                }
            }
        }
    }
}

@Composable
private fun VotesSlide(stats: WrappedStats) {
    val votes = stats.votes
    SlideTitle(stringResource(Res.string.wrapped_votes_title))
    CountUpText(votes.total)
    Text(
        text = pluralStringResource(Res.plurals.wrapped_votes_count, votes.total),
        style = KotlinConfTheme.typography.h2,
        color = WrappedPalette.textPrimary,
    )
    Spacer(Modifier.height(24.dp))
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        VoteBar(Emotion.Positive, stringResource(Res.string.wrapped_votes_good), votes.good, votes.total)
        VoteBar(Emotion.Neutral, stringResource(Res.string.wrapped_votes_ok), votes.ok, votes.total)
        VoteBar(Emotion.Negative, stringResource(Res.string.wrapped_votes_bad), votes.bad, votes.total)
    }
    val positivity = votes.positivity
    if (positivity != null) {
        Spacer(Modifier.height(24.dp))
        SlideBody(stringResource(Res.string.wrapped_votes_positive, (positivity * 100).roundToInt()))
    }
}

@Composable
private fun VoteBar(emotion: Emotion, label: String, count: Int, total: Int) {
    val fraction = if (total == 0) 0f else count.toFloat() / total
    val animatedFraction = remember { Animatable(0f) }
    LaunchedEffect(fraction) {
        animatedFraction.animateTo(fraction, tween(900, easing = FastOutSlowInEasing))
    }
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        KodeeIconSmall(emotion = emotion, selected = true, contentDescription = null)
        Text(
            text = label,
            style = KotlinConfTheme.typography.text2,
            color = WrappedPalette.textPrimary,
            maxLines = 1,
            modifier = Modifier.widthIn(min = 72.dp),
        )
        Box(
            Modifier
                .weight(1f)
                .height(8.dp)
                .clip(RoundedCornerShape(percent = 50))
                .background(WrappedPalette.trackBackground)
        ) {
            Box(
                Modifier
                    .fillMaxHeight()
                    .fillMaxWidth(animatedFraction.value.coerceIn(0f, 1f))
                    .background(WrappedPalette.textPrimary)
            )
        }
        Text(
            text = count.toString(),
            style = KotlinConfTheme.typography.h4,
            color = WrappedPalette.textPrimary,
            modifier = Modifier.widthIn(min = 24.dp),
        )
    }
}

@Composable
private fun UsageSlide(stats: WrappedStats) {
    val usage = stats.usage
    SlideTitle(stringResource(Res.string.wrapped_usage_title))
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        if (usage.appLaunches > 0) {
            SlideBody(pluralStringResource(Res.plurals.wrapped_usage_launches, usage.appLaunches, usage.appLaunches))
        }
        if (usage.totalSessionViews > 0) {
            Column {
                Row(verticalAlignment = Alignment.Bottom, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    CountUpText(
                        target = usage.totalSessionViews,
                        style = KotlinConfTheme.typography.h1.copy(fontSize = 40.sp, lineHeight = 44.sp),
                    )
                    Text(
                        text = pluralStringResource(
                            Res.plurals.wrapped_usage_session_views,
                            usage.totalSessionViews,
                        ),
                        style = KotlinConfTheme.typography.text1,
                        color = WrappedPalette.textPrimary,
                        modifier = Modifier.padding(bottom = 4.dp),
                    )
                }
                SlideNote(
                    pluralStringResource(
                        Res.plurals.wrapped_usage_unique_sessions,
                        usage.uniqueSessionsViewed,
                        usage.uniqueSessionsViewed,
                    )
                )
            }
        }
        if (usage.totalSpeakerViews > 0) {
            SlideBody(
                pluralStringResource(
                    Res.plurals.wrapped_usage_speaker_views,
                    usage.totalSpeakerViews,
                    usage.totalSpeakerViews,
                )
            )
        }
    }
    val mostViewed = usage.mostViewedSession
    if (mostViewed != null && usage.mostViewedSessionCount > 1) {
        Spacer(Modifier.height(24.dp))
        SlideNote(stringResource(Res.string.wrapped_usage_most_viewed))
        Spacer(Modifier.height(4.dp))
        Text(
            text = mostViewed.title,
            style = KotlinConfTheme.typography.h3,
            color = WrappedPalette.textPrimary,
        )
        SlideNote(
            pluralStringResource(
                Res.plurals.wrapped_usage_most_viewed_count,
                usage.mostViewedSessionCount,
                usage.mostViewedSessionCount,
            )
        )
    }
}

@Composable
private fun EmptySlide() {
    KodeeEmotion(Emotion.Neutral)
    Spacer(Modifier.height(24.dp))
    Text(
        text = stringResource(Res.string.wrapped_empty_title),
        style = heroTextStyle,
        color = WrappedPalette.textPrimary,
    )
    Spacer(Modifier.height(24.dp))
    SlideBody(stringResource(Res.string.wrapped_empty_body))
}

@Composable
private fun PersonalitySlide(stats: WrappedStats) {
    SlideTitle(stringResource(Res.string.wrapped_personality_title))
    KodeeEmotion(
        if (stats.personality == AttendeePersonality.FreeSpirit) Emotion.Neutral else Emotion.Positive
    )
    Spacer(Modifier.height(24.dp))
    Text(
        text = personalityName(stats.personality),
        style = heroTextStyle,
        color = WrappedPalette.textPrimary,
    )
    Spacer(Modifier.height(24.dp))
    SlideBody(personalityDescription(stats.personality))
}

@Composable
private fun OutroSlide(stats: WrappedStats, onDone: () -> Unit) {
    val clipboardManager = LocalClipboardManager.current
    var copied by remember { mutableStateOf(false) }
    LaunchedEffect(copied) {
        if (copied) {
            delay(2_000)
            copied = false
        }
    }

    val shareText = stringResource(
        Res.string.wrapped_share_text,
        stats.year.toString(),
        stats.bookmarks.count,
        stats.votes.total,
        stats.usage.uniqueSessionsViewed,
        personalityName(stats.personality),
    )

    Text(
        text = stringResource(Res.string.wrapped_outro_title),
        style = heroTextStyle,
        color = WrappedPalette.textPrimary,
    )
    Spacer(Modifier.height(16.dp))
    SlideBody(stringResource(Res.string.wrapped_outro_body, stats.year.toString()))
    Spacer(Modifier.height(32.dp))
    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        StatTile(stats.bookmarks.count, stringResource(Res.string.wrapped_outro_bookmarked), Modifier.weight(1f))
        StatTile(stats.votes.total, stringResource(Res.string.wrapped_outro_rated), Modifier.weight(1f))
    }
    Spacer(Modifier.height(32.dp))
    Button(
        label = if (copied) stringResource(Res.string.wrapped_share_copied) else stringResource(Res.string.wrapped_share),
        onClick = {
            clipboardManager.setText(AnnotatedString(shareText))
            copied = true
        },
        primary = true,
        primaryBackground = WrappedPalette.magenta,
        modifier = Modifier.fillMaxWidth(),
    )
    Spacer(Modifier.height(12.dp))
    Button(
        label = stringResource(Res.string.wrapped_done),
        onClick = onDone,
        modifier = Modifier.fillMaxWidth(),
    )
    Spacer(Modifier.height(8.dp))
    SlideNote(
        text = shareText,
        modifier = Modifier.fillMaxWidth(),
    )
}
