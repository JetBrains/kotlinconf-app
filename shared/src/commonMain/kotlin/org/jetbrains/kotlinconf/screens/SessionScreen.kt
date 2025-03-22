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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.unit.dp
import kotlinconfapp.shared.generated.resources.Res
import kotlinconfapp.shared.generated.resources.arrow_left_24
import kotlinconfapp.shared.generated.resources.down_24
import kotlinconfapp.shared.generated.resources.navigate_back
import kotlinconfapp.shared.generated.resources.session_how_was_the_talk
import kotlinconfapp.shared.generated.resources.session_screen_error
import kotlinconfapp.shared.generated.resources.session_title
import kotlinconfapp.shared.generated.resources.session_your_feedback
import kotlinconfapp.shared.generated.resources.up_24
import kotlinx.coroutines.delay
import org.jetbrains.compose.resources.stringResource
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
import org.jetbrains.kotlinconf.ui.components.PageTitle
import org.jetbrains.kotlinconf.ui.components.SpeakerCard
import org.jetbrains.kotlinconf.ui.components.StyledText
import org.jetbrains.kotlinconf.ui.components.TopMenuButton
import org.jetbrains.kotlinconf.ui.theme.KotlinConfTheme
import org.jetbrains.kotlinconf.utils.FadingAnimationSpec
import org.jetbrains.kotlinconf.utils.topInsetPadding
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun SessionScreen(
    sessionId: SessionId,
    onBack: () -> Unit,
    onSpeaker: (SpeakerId) -> Unit,
    onPrivacyPolicyNeeded: () -> Unit,
    onNavigateToMap: (String) -> Unit,
    viewModel: SessionViewModel = koinViewModel { parametersOf(sessionId) }
) {
    val session = viewModel.session.collectAsState().value
    val speakers = viewModel.speakers.collectAsState().value
    val userSignedIn by viewModel.userSignedIn.collectAsState()
    val shouldNavigateToPrivacyPolicy by viewModel.navigateToPrivacyPolicy.collectAsState()

    LaunchedEffect(shouldNavigateToPrivacyPolicy) {
        if (shouldNavigateToPrivacyPolicy) {
            onPrivacyPolicyNeeded()
            viewModel.onNavigatedToPrivacyPolicy()
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
                Column(
                    modifier = Modifier
                        .padding(horizontal = 12.dp)
                        .verticalScroll(rememberScrollState())
                ) {
                    PageTitle(
                        time = session.timeLine,
                        title = session.title,
                        lightning = session.isLightning,
                        tags = session.tags,
                        bookmarked = session.isFavorite,
                        onBookmark = { viewModel.toggleFavorite(it) },
                        modifier = Modifier.padding(vertical = 24.dp),
                    )

                    if (session.state != SessionState.Upcoming) {
                        FeedbackPanel(
                            onFeedback = { emotion ->
                                viewModel.submitFeedback(emotion)
                            },
                            onFeedbackWithComment = { emotion, comment ->
                                viewModel.submitFeedbackWithComment(emotion, comment)
                            },
                            userSignedIn= userSignedIn,
                            initialEmotion = session.vote?.toEmotion(),
                            modifier = Modifier.padding(bottom = 20.dp),
                        )
                    }

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

                    StyledText(
                        text = session.description,
                        style = KotlinConfTheme.typography.text1
                    )

                    Spacer(Modifier.height(24.dp))
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
    userSignedIn: Boolean,
    modifier: Modifier = Modifier,
    initialEmotion: Emotion? = null,
) {
    var selectedEmotion by remember { mutableStateOf<Emotion?>(initialEmotion) }
    var feedbackExpanded by remember { mutableStateOf(false) }

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
            StyledText(
                text = stringResource(Res.string.session_how_was_the_talk),
                style = KotlinConfTheme.typography.text2,
                color = KotlinConfTheme.colors.primaryText,
            )
            Spacer(Modifier.height(16.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
            ) {
                val feedbackEmotions = remember {
                    listOf(Emotion.Negative, Emotion.Neutral, Emotion.Positive)
                }
                val hapticFeedback = LocalHapticFeedback.current
                feedbackEmotions.forEach { emotion ->
                    KodeeIconLarge(
                        emotion = emotion,
                        selected = selectedEmotion == emotion,
                        modifier = Modifier.clickable(indication = null, interactionSource = null) {
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
            val focusRequester = remember { FocusRequester() }
            LaunchedEffect(Unit) {
                focusRequester.requestFocus()
            }
            val hapticFeedback = LocalHapticFeedback.current
            FeedbackForm(
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
    var isExpanded by remember { mutableStateOf(false) }
    val iconRotation by animateFloatAsState(if (isExpanded) 180f else 0f)

    Column(modifier = modifier.padding(vertical = 16.dp)) {
        if (rooms[roomName] == null) {
            StyledText(
                text = roomName,
                style = KotlinConfTheme.typography.h3,
            )
        } else {
            Action(
                label = roomName,
                icon = Res.drawable.down_24,
                size = ActionSize.Large,
                enabled = true,
                onClick = { isExpanded = !isExpanded },
                iconRotation = iconRotation,
                modifier = Modifier.fillMaxWidth()
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
