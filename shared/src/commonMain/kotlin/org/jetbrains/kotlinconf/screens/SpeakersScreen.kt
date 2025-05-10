@file:Suppress("unused", "DuplicatedCode")

package org.jetbrains.kotlinconf.screens

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.EaseInOutQuad
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.StartOffset
import androidx.compose.animation.core.StartOffsetType
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.snapping.SnapPosition
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.backhandler.BackHandler
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.lerp
import kotlinconfapp.shared.generated.resources.Res
import kotlinconfapp.shared.generated.resources.speakers_error_no_data
import kotlinconfapp.shared.generated.resources.speakers_title
import kotlinconfapp.ui_components.generated.resources.main_header_search_hint
import kotlinconfapp.ui_components.generated.resources.search_24
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.kotlinconf.SpeakerId
import org.jetbrains.kotlinconf.ui.components.Divider
import org.jetbrains.kotlinconf.ui.components.Emotion
import org.jetbrains.kotlinconf.ui.components.KodeeIconLarge
import org.jetbrains.kotlinconf.ui.components.MainHeaderContainer
import org.jetbrains.kotlinconf.ui.components.MainHeaderContainerState
import org.jetbrains.kotlinconf.ui.components.MainHeaderSearchBar
import org.jetbrains.kotlinconf.ui.components.MainHeaderTitleBar
import org.jetbrains.kotlinconf.ui.components.NormalErrorWithLoading
import org.jetbrains.kotlinconf.ui.components.SpeakerAvatar
import org.jetbrains.kotlinconf.ui.components.SpeakerCard
import org.jetbrains.kotlinconf.ui.components.Text
import org.jetbrains.kotlinconf.ui.components.TopMenuButton
import org.jetbrains.kotlinconf.ui.theme.KotlinConfTheme
import org.jetbrains.kotlinconf.utils.FadingAnimationSpec
import org.koin.compose.viewmodel.koinViewModel
import kotlin.math.absoluteValue
import kotlin.math.sin
import kotlin.random.Random
import kotlinconfapp.ui_components.generated.resources.Res as UiRes

@Composable
fun SpeakersScreen(
    onSpeaker: (SpeakerId) -> Unit,
    viewModel: SpeakersViewModel = koinViewModel(),
) {
    var searchState by rememberSaveable { mutableStateOf(MainHeaderContainerState.Title) }
    var searchText by rememberSaveable { mutableStateOf("") }

    val uiState = viewModel.speakers.collectAsState().value

    LaunchedEffect(searchText) {
        viewModel.setSearchText(searchText)
    }

    Column(Modifier.fillMaxSize().background(color = KotlinConfTheme.colors.mainBackground)) {
        MainHeaderContainer(
            state = searchState,
            titleContent = {
                MainHeaderTitleBar(
                    title = stringResource(Res.string.speakers_title),
                    endContent = {
                        TopMenuButton(
                            icon = UiRes.drawable.search_24,
                            onClick = { searchState = MainHeaderContainerState.Search },
                            contentDescription = stringResource(UiRes.string.main_header_search_hint)
                        )
                    }
                )
            },
            searchContent = {
                @OptIn(ExperimentalComposeUiApi::class)
                BackHandler(true) {
                    searchState = MainHeaderContainerState.Title
                    searchText = ""
                }
                MainHeaderSearchBar(
                    searchValue = searchText,
                    onSearchValueChange = { searchText = it },
                    onClose = {
                        searchState = MainHeaderContainerState.Title
                        searchText = ""
                    },
                    onClear = { searchText = "" },
                )
            }
        )

        Divider(1.dp, KotlinConfTheme.colors.strokePale)

        AnimatedContent(
            uiState,
            modifier = Modifier.fillMaxSize().clipToBounds(),
            contentKey = {
                when (uiState) {
                    is SpeakersUiState.Content -> 1
                    SpeakersUiState.Error, SpeakersUiState.Loading -> 2
                }
            },
            transitionSpec = { FadingAnimationSpec }
        ) { targetState ->
            when (targetState) {
                is SpeakersUiState.Content -> {
                    Box(modifier = Modifier.fillMaxSize()) {
                        SpeakerGrid(targetState, onSpeaker)
                    }
                }

                SpeakersUiState.Error, SpeakersUiState.Loading -> {
                    NormalErrorWithLoading(
                        message = stringResource(Res.string.speakers_error_no_data),
                        isLoading = uiState is SpeakersUiState.Loading,
                        modifier = Modifier.fillMaxSize(),
                        onRetry = { viewModel.refresh() },
                    )
                }
            }
        }
    }
}

@Composable
private fun SpeakerGrid(
    targetState: SpeakersUiState.Content,
    onSpeaker: (SpeakerId) -> Unit
) {
    val listState = rememberLazyGridState()
    LazyVerticalGrid(
        state = listState,
        columns = GridCells.Fixed(1),
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(12.dp)
    ) {
        items(targetState.speakers, key = { it.speaker.id.id }) { speakerWithHighlights ->
            val speaker = speakerWithHighlights.speaker
            SpeakerCard(
                name = speaker.name,
                nameHighlights = speakerWithHighlights.nameHighlights,
                title = speaker.position,
                titleHighlights = speakerWithHighlights.titleHighlights,
                photoUrl = speaker.photoUrl,
                modifier = Modifier
                    .animateItem()
                    .fillMaxWidth(),
                onClick = { onSpeaker(speaker.id) },
            )
        }
    }
}

@Composable
private fun SpeakerCarousel(
    targetState: SpeakersUiState.Content,
    onSpeaker: (SpeakerId) -> Unit
) {
    val pagerState = rememberPagerState(27, pageCount = { targetState.speakers.size })
    HorizontalPager(
        state = pagerState,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 180.dp),
        contentPadding = PaddingValues(horizontal = 150.dp),
        pageSpacing = 0.dp,
        beyondViewportPageCount = 8,
        snapPosition = SnapPosition.Center,
        verticalAlignment = Alignment.Top,
    ) { page ->
        val speakerWithHighlights = targetState.speakers[page]
        val speaker = speakerWithHighlights.speaker

        // Calculate the offset for the cover flow effect
        val pageOffset = (
                (pagerState.currentPage - page) + pagerState
                    .currentPageOffsetFraction
                )

        Column(
            modifier = Modifier
                .graphicsLayer {
                    // Cover flow effect: scale and rotate based on offset
                    alpha = lerp(
                        start = 0.5f,
                        stop = 1f,
                        fraction = 1f - pageOffset.absoluteValue.coerceIn(0f, 1f)
                    )
                    scaleX = lerp(
                        start = 0.7f,
                        stop = 1f,
                        fraction = 1f - pageOffset.absoluteValue.coerceIn(0f, 1f)
                    )
                    scaleY = lerp(
                        start = 0.7f,
                        stop = 1f,
                        fraction = 1f - pageOffset.absoluteValue.coerceIn(0f, 1f)
                    )
                    rotationY = lerp(
                        start = 0f,
                        stop = if (pageOffset > 0) 20f else -20f,
                        fraction = pageOffset.absoluteValue.coerceIn(0f, 1f)
                    )
                }
                .padding(6.dp)
                .width(300.dp)
                //                                    .fillMaxWidth(0.7f)
                .clip(RoundedCornerShape(8.dp)),
//                .clickable(onClick = { onSpeaker(speaker.id) })
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            SpeakerAvatar(
                photoUrl = speaker.photoUrl,
                modifier = Modifier.size(150.dp),
            )
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = buildHighlightedString(
                        speaker.name,
                        speakerWithHighlights.nameHighlights
                    ),
                    style = KotlinConfTheme.typography.h2,
                    color = KotlinConfTheme.colors.primaryText,
                )
                Spacer(modifier = Modifier.size(8.dp))
                Text(
                    text = buildHighlightedString(
                        speaker.position,
                        speakerWithHighlights.titleHighlights
                    ),
                    style = KotlinConfTheme.typography.text1.copy(
                        textAlign = TextAlign.Center,
                    ),
                    color = KotlinConfTheme.colors.secondaryText,
                )
            }
        }
    }
}

@Composable
internal fun buildHighlightedString(
    text: String,
    highlights: List<IntRange>,
): AnnotatedString = if (highlights.isEmpty()) {
    AnnotatedString(text)
} else {
    buildAnnotatedString {
        append(text)
        highlights.forEach { range ->
            // Ignore invalid ranges
            if (!range.isEmpty()) {
                addStyle(
                    style = SpanStyle(
                        color = KotlinConfTheme.colors.primaryTextInverted,
                        background = Color(0xFFC202D7),
                    ),
                    start = range.first,
                    end = range.last + 1,
                )
            }
        }
    }
}


// Data class to represent a falling Kodee icon
private data class KodeeDroplet(
    val id: Int,
    val initialX: Float,
    val speed: Float,
    val size: Float,
    val emotion: Emotion,
    val selected: Boolean,
    val rotationDirection: Float, // Direction and intensity of rotation
    val rotationSpeed: Float, // Speed of rotation
    val phaseOffset: Float // Phase offset for continuous falling (0.0f to 1.0f)
)

@Composable
private fun KodeeRain() {
    // Create a list of falling Kodee icons
    val kodeeDroplets = remember {
        mutableStateListOf<KodeeDroplet>().apply {
            // Create 12 Kodee icons with random properties for better distribution
            repeat(25) { index ->
                add(
                    KodeeDroplet(
                        id = index,
                        // Better distribution across the screen width
                        initialX = (Random.nextFloat() * 1000f - 500f), // Random X position between -200 and 200
                        // More varied speeds for natural falling effect
                        speed = Random.nextFloat() * 3f + 0.7f, // Random speed between 0.7 and 3.7
                        // More varied sizes for visual interest
                        size = Random.nextFloat() * 50f + 50f, // Random size between 50 and 100
                        // Mostly positive emotions with some variety
                        emotion = when (Random.nextInt(5)) {
                            0, 1 -> Emotion.Neutral
                            1 -> Emotion.Negative
                            else -> Emotion.Positive
                        },
                        // Mostly selected (colored) Kodees
                        selected = Random.nextFloat() > 0.3f, // 70% chance of being selected
                        // Random rotation direction (-20 to 20 degrees)
                        rotationDirection = Random.nextFloat() * 40f - 20f,
                        // Random rotation speed (0.5 to 1.5)
                        rotationSpeed = Random.nextFloat() + 0.5f,
                        // Random phase offset (0.0f to 1.0f) for continuous falling
                        phaseOffset = Random.nextFloat()
                    )
                )
            }
        }
    }

    // Create an infinite animation for vertical movement (falling)
    val infiniteTransition = rememberInfiniteTransition()

    // Vertical falling animation - slower for more gentle falling effect
    val verticalOffset = infiniteTransition.animateFloat(
        initialValue = -300f, // Start higher above the screen
        targetValue = 1700f, // Move further beyond the screen height
        animationSpec = infiniteRepeatable(
            animation = tween(45000, easing = EaseInOutQuad), // 15 seconds to fall for a gentler effect
            repeatMode = RepeatMode.Restart,
            initialStartOffset = StartOffset(2000, offsetType = StartOffsetType.FastForward)
        )
    )

    // Overlay multiple falling Kodee icons on top of the list
    kodeeDroplets.forEach { kodee ->
        KodeeIconLarge(
            emotion = kodee.emotion,
            selected = kodee.selected,
            modifier = Modifier
                .fillMaxWidth()
                .graphicsLayer {
                    // Calculate the adjusted vertical position based on phase offset
                    // This creates a continuous stream of falling Kodees
                    val animationRange = 1500f // Total animation range (from -300 to 1200)
                    val adjustedVerticalPosition =
                        (verticalOffset.value + animationRange * kodee.phaseOffset) % animationRange - 300f

                    translationX = kodee.initialX
                    translationY = adjustedVerticalPosition * kodee.speed
                    scaleX = kodee.size / 100f
                    scaleY = kodee.size / 100f
                    // Apply rotation based on vertical position for a natural swinging effect
                    rotationZ = kodee.rotationDirection * sin(adjustedVerticalPosition * 0.01f * kodee.rotationSpeed)
                }
        )
    }
}
