package org.jetbrains.kotlinconf.screens

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.unit.dp
import kotlinconfapp.shared.generated.resources.Res
import kotlinconfapp.shared.generated.resources.arrow_left_24
import kotlinconfapp.shared.generated.resources.arrow_up_right_24
import kotlinconfapp.shared.generated.resources.down_24
import kotlinconfapp.shared.generated.resources.navigate_back
import kotlinconfapp.shared.generated.resources.play_video
import kotlinconfapp.shared.generated.resources.session_room_state_description_collapsed
import kotlinconfapp.shared.generated.resources.session_room_state_description_expanded
import kotlinconfapp.shared.generated.resources.session_screen_error
import kotlinconfapp.shared.generated.resources.session_title
import kotlinconfapp.shared.generated.resources.session_watch_video
import kotlinconfapp.shared.generated.resources.session_your_feedback
import kotlinconfapp.shared.generated.resources.up_24
import kotlinconfapp.ui_components.generated.resources.talk_card_how_was_the_talk
import kotlinconfapp.ui_components.generated.resources.talk_card_how_was_the_workshop
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.kotlinconf.HideKeyboardOnDragHandler
import org.jetbrains.kotlinconf.ScrollToTopHandler
import org.jetbrains.kotlinconf.SessionId
import org.jetbrains.kotlinconf.SessionState
import org.jetbrains.kotlinconf.SpeakerId
import org.jetbrains.kotlinconf.toEmotion
import org.jetbrains.kotlinconf.ui.components.Action
import org.jetbrains.kotlinconf.ui.components.ActionSize
import org.jetbrains.kotlinconf.ui.components.Divider
import org.jetbrains.kotlinconf.ui.components.Emotion
import org.jetbrains.kotlinconf.ui.components.FeedbackForm
import org.jetbrains.kotlinconf.ui.components.KodeeIconLarge
import org.jetbrains.kotlinconf.ui.components.MainHeaderTitleBar
import org.jetbrains.kotlinconf.ui.components.MajorError
import org.jetbrains.kotlinconf.ui.components.PageMenuItem
import org.jetbrains.kotlinconf.ui.components.PageTitle
import org.jetbrains.kotlinconf.ui.components.SpeakerCard
import org.jetbrains.kotlinconf.ui.components.Text
import org.jetbrains.kotlinconf.ui.components.TopMenuButton
import org.jetbrains.kotlinconf.ui.theme.KotlinConfTheme
import org.jetbrains.kotlinconf.utils.FadingAnimationSpec
import org.jetbrains.kotlinconf.utils.bottomInsetPadding
import org.jetbrains.kotlinconf.utils.topInsetPadding
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf
import kotlinconfapp.ui_components.generated.resources.Res as UiRes

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun SessionScreen(
    sessionId: SessionId,
    openedForFeedback: Boolean,
    onBack: () -> Unit,
    onSpeaker: (SpeakerId) -> Unit,
    onPrivacyNoticeNeeded: () -> Unit,
    onNavigateToMap: (String) -> Unit,
    onWatchVideo: (String) -> Unit,
    viewModel: SessionViewModel = koinViewModel { parametersOf(sessionId) }
) {
    val session = viewModel.session.collectAsState().value
    val speakers = viewModel.speakers.collectAsState().value
    val userSignedIn by viewModel.userSignedIn.collectAsState()
    val shouldNavigateToPrivacyNotice by viewModel.navigateToPrivacyNotice.collectAsState()

    LaunchedEffect(shouldNavigateToPrivacyNotice) {
        if (shouldNavigateToPrivacyNotice) {
            onPrivacyNoticeNeeded()
            viewModel.onNavigatedToPrivacyNotice()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(color = KotlinConfTheme.colors.mainBackground)
            .padding(topInsetPadding())
    ) {
        MainHeaderTitleBar(
            title = stringResource(Res.string.session_title),
            startContent = {
                TopMenuButton(
                    icon = Res.drawable.arrow_left_24,
                    contentDescription = stringResource(Res.string.navigate_back),
                    onClick = onBack,
                )
            },
        )

        Divider(
            thickness = 1.dp,
            color = KotlinConfTheme.colors.strokePale
        )

        AnimatedContent(
            session != null,
            modifier = Modifier.fillMaxSize(),
            contentKey = { it::class },
            transitionSpec = { FadingAnimationSpec }
        ) { hasState ->
            if (hasState && session != null) {
                val listState = rememberLazyListState()

                LaunchedEffect(openedForFeedback) {
                    if (openedForFeedback) {
                        listState.animateScrollToItem(1)
                    }
                }

                ScrollToTopHandler(listState)
                HideKeyboardOnDragHandler(listState)

                LazyColumn(
                    state = listState,
                    modifier = Modifier.padding(horizontal = 12.dp),
                    contentPadding = bottomInsetPadding()
                ) {
                    item {
                        Column {
                            PageTitle(
                                time = session.fullTimeline,
                                title = session.title,
                                lightning = session.isLightning,
                                tags = session.tags,
                                bookmarked = session.isFavorite,
                                onBookmark = { viewModel.toggleFavorite(it) },
                                modifier = Modifier.padding(vertical = 24.dp),
                            )
                            if (session.videoUrl != null) {
                                PageMenuItem(
                                    label = stringResource(Res.string.session_watch_video),
                                    onClick = { onWatchVideo(session.videoUrl) },
                                    drawableStart = Res.drawable.play_video,
                                    drawableEnd = Res.drawable.arrow_up_right_24,
                                    modifier = Modifier.padding(bottom = 12.dp),
                                )
                            }
                        }
                    }

                    item {
                        if (session.state != SessionState.Upcoming) {
                            val scope = rememberCoroutineScope()
                            FeedbackPanel(
                                onFeedback = { emotion ->
                                    viewModel.submitFeedback(emotion)
                                },
                                onFeedbackWithComment = { emotion, comment ->
                                    viewModel.submitFeedbackWithComment(emotion, comment)
                                },
                                onFeedbackExpanded = {
                                    scope.launch {
                                        delay(100)
                                        listState.animateScrollToItem(1)
                                    }
                                },
                                userSignedIn = userSignedIn,
                                startExpanded = openedForFeedback,
                                initialEmotion = session.vote?.toEmotion(),
                                feedbackQuestion = stringResource(
                                    if (session.tags.contains("Workshop")) UiRes.string.talk_card_how_was_the_workshop
                                    else UiRes.string.talk_card_how_was_the_talk
                                ),
                                modifier = Modifier.padding(bottom = 20.dp),
                            )
                        }
                    }

                    item {
                        Column(Modifier.fillMaxWidth()) {
                            speakers.forEach { speaker ->
                                SpeakerCard(
                                    name = speaker.name,
                                    title = speaker.position,
                                    photoUrl = speaker.photoUrl,
                                    modifier = Modifier.padding(vertical = 12.dp).fillMaxWidth(),
                                    onClick = { onSpeaker(speaker.id) }
                                )
                            }

                            RoomSection(
                                roomName = session.locationLine,
                                onNavigateToMap = onNavigateToMap
                            )

                            Text(
                                text = session.description,
                                style = KotlinConfTheme.typography.text1,
                                selectable = true,
                            )

                            Spacer(Modifier.height(24.dp))
                        }
                    }
                }
            } else {
                MajorError(
                    message = stringResource(Res.string.session_screen_error),
                    modifier = Modifier.fillMaxSize(),
                )
            }
        }
    }
}

@Composable
private fun FeedbackPanel(
    onFeedback: (Emotion?) -> Unit,
    onFeedbackWithComment: (Emotion, String) -> Unit,
    onFeedbackExpanded: () -> Unit,
    userSignedIn: Boolean,
    modifier: Modifier = Modifier,
    startExpanded: Boolean,
    initialEmotion: Emotion? = null,
    feedbackQuestion: String,
) {
    var selectedEmotion by rememberSaveable { mutableStateOf(initialEmotion) }
    var feedbackExpanded by rememberSaveable { mutableStateOf(initialEmotion != null && startExpanded) }
    var feedbackText by rememberSaveable { mutableStateOf("") }

    LaunchedEffect(feedbackExpanded) {
        if (feedbackExpanded) {
            onFeedbackExpanded()
        }
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .border(
                width = 1.dp,
                color = KotlinConfTheme.colors.strokePale,
                shape = RoundedCornerShape(8.dp),
            )
            .clip(RoundedCornerShape(8.dp))
            .background(KotlinConfTheme.colors.cardBackgroundPast),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 24.dp, vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = feedbackQuestion,
                style = KotlinConfTheme.typography.text2,
                color = KotlinConfTheme.colors.primaryText,
            )
            Spacer(Modifier.height(16.dp))
            Row(
                modifier = Modifier.fillMaxWidth().selectableGroup(),
                horizontalArrangement = Arrangement.SpaceEvenly,
            ) {
                val feedbackEmotions = remember {
                    listOf(Emotion.Negative, Emotion.Neutral, Emotion.Positive)
                }
                val hapticFeedback = LocalHapticFeedback.current
                feedbackEmotions.forEach { emotion ->
                    val selected = selectedEmotion == emotion
                    KodeeIconLarge(
                        emotion = emotion,
                        selected = selected,
                        modifier = Modifier.selectable(
                            selected = selected,
                            indication = null,
                            interactionSource = null
                        ) {
                            val newEmotion = if (emotion == selectedEmotion) null else emotion
                            if (userSignedIn) {
                                hapticFeedback.performHapticFeedback(HapticFeedbackType.Confirm)
                                selectedEmotion = newEmotion
                                feedbackExpanded = newEmotion != null
                                onFeedback(newEmotion)
                            } else {
                                hapticFeedback.performHapticFeedback(HapticFeedbackType.Reject)
                                onFeedback(newEmotion)
                            }
                        },
                    )
                }
            }
        }

        AnimatedVisibility(
            selectedEmotion != null,
            enter = fadeIn() + expandVertically(clip = false, expandFrom = Alignment.Top),
            exit = fadeOut(animationSpec = tween(100)) + shrinkVertically(clip = false, shrinkTowards = Alignment.Top),
        ) {
            Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
                Divider(thickness = 1.dp, color = KotlinConfTheme.colors.strokePale)

                val iconRotation by animateFloatAsState(if (feedbackExpanded) 0f else 180f)
                Action(
                    label = stringResource(Res.string.session_your_feedback),
                    icon = Res.drawable.up_24,
                    size = ActionSize.Medium,
                    enabled = true,
                    onClick = { feedbackExpanded = !feedbackExpanded },
                    iconRotation = iconRotation,
                    modifier = Modifier.padding(16.dp)
                )
            }
        }

        AnimatedVisibility(visible = feedbackExpanded) {
            var focusRequested by rememberSaveable { mutableStateOf(false) }
            val focusRequester = remember { FocusRequester() }
            if (!focusRequested) {
                LaunchedEffect(Unit) {
                    focusRequester.requestFocus()
                    focusRequested = true
                }
            }
            val hapticFeedback = LocalHapticFeedback.current
            FeedbackForm(
                feedbackText = feedbackText,
                onFeedbackTextChange = { feedbackText = it },
                emotion = selectedEmotion,
                onSubmit = { emotion, comment ->
                    hapticFeedback.performHapticFeedback(HapticFeedbackType.Confirm)
                    onFeedbackWithComment(emotion, comment)
                    feedbackExpanded = false
                },
                past = true,
                modifier = Modifier
                    .focusRequester(focusRequester)
                    .padding(bottom = 16.dp)
            )
        }
    }
}

@Composable
private fun RoomSection(
    roomName: String,
    onNavigateToMap: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    var isExpanded by rememberSaveable { mutableStateOf(false) }
    val iconRotation by animateFloatAsState(if (isExpanded) 180f else 0f)

    Column(modifier = modifier.padding(vertical = 16.dp)) {
        if (rooms[roomName] == null) {
            Text(
                text = roomName,
                style = KotlinConfTheme.typography.h3,
            )
        } else {
            val stateDesc = stringResource(
                if (isExpanded) Res.string.session_room_state_description_expanded
                else Res.string.session_room_state_description_collapsed
            )
            Action(
                label = roomName,
                icon = Res.drawable.down_24,
                size = ActionSize.Large,
                enabled = true,
                onClick = { isExpanded = !isExpanded },
                iconRotation = iconRotation,
                modifier = Modifier
                    .fillMaxWidth()
                    .semantics {
                        stateDescription = stateDesc
                    }
            )
            AnimatedVisibility(
                visible = isExpanded,
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically(),
                modifier = Modifier.fillMaxWidth(),
            ) {
                StaticMap(
                    roomName = roomName,
                    modifier = Modifier
                        .padding(top = 8.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .clickable {
                            onNavigateToMap(roomName)
                        }
                        .fillMaxWidth()
                        .heightIn(max = 400.dp),
                )
            }
        }
    }
}
