package org.jetbrains.kotlinconf.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import kotlinconfapp.shared.generated.resources.Res
import kotlinconfapp.shared.generated.resources.arrow_left_24
import kotlinconfapp.shared.generated.resources.down_24
import kotlinconfapp.shared.generated.resources.navigate_back
import kotlinconfapp.shared.generated.resources.session_how_was_the_talk
import kotlinconfapp.shared.generated.resources.session_title
import kotlinconfapp.shared.generated.resources.session_your_feedback
import kotlinconfapp.shared.generated.resources.up_24
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.kotlinconf.SessionId
import org.jetbrains.kotlinconf.SessionState
import org.jetbrains.kotlinconf.SpeakerId
import org.jetbrains.kotlinconf.ui.components.Action
import org.jetbrains.kotlinconf.ui.components.ActionSize
import org.jetbrains.kotlinconf.ui.components.Divider
import org.jetbrains.kotlinconf.ui.components.Emotion
import org.jetbrains.kotlinconf.ui.components.FeedbackForm
import org.jetbrains.kotlinconf.ui.components.KodeeIconLarge
import org.jetbrains.kotlinconf.ui.components.MainHeaderTitleBar
import org.jetbrains.kotlinconf.ui.components.PageTitle
import org.jetbrains.kotlinconf.ui.components.SpeakerCard
import org.jetbrains.kotlinconf.ui.components.StyledText
import org.jetbrains.kotlinconf.ui.components.TopMenuButton
import org.jetbrains.kotlinconf.ui.theme.KotlinConfTheme
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
    viewModel: SessionViewModel = koinViewModel { parametersOf(sessionId) }
) {
    val session = viewModel.session.collectAsState().value ?: return
    val speakers = viewModel.speakers.collectAsState().value
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
                    modifier = Modifier.padding(bottom = 20.dp)
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

            RoomSection(roomName = session.locationLine)

            StyledText(
                text = session.description,
                style = KotlinConfTheme.typography.text1
            )

            Spacer(Modifier.height(24.dp))
        }
    }
}

@Composable
private fun FeedbackPanel(
    onFeedback: (Emotion?) -> Unit,
    onFeedbackWithComment: (Emotion, String) -> Unit,
    modifier: Modifier = Modifier,
) {
    var selectedEmotion by remember { mutableStateOf<Emotion?>(null) }
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
                feedbackEmotions.forEach { emotion ->
                    KodeeIconLarge(
                        emotion = emotion,
                        selected = selectedEmotion == emotion,
                        onClick = {
                            val newEmotion = if (emotion == selectedEmotion) null else emotion
                            selectedEmotion = newEmotion
                            feedbackExpanded = newEmotion != null
                            onFeedback(newEmotion)
                        },
                    )
                }
            }
        }

        AnimatedVisibility(selectedEmotion != null) {
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
            FeedbackForm(
                emotion = selectedEmotion,
                onSubmit = { emotion, comment ->
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
    modifier: Modifier = Modifier,
) {
    var isExpanded by remember { mutableStateOf(false) }
    val iconRotation by animateFloatAsState(if (isExpanded) 180f else 0f)

    Column(modifier = modifier.padding(vertical = 16.dp)) {
        Action(
            label = roomName,
            icon = Res.drawable.down_24,
            size = ActionSize.Large,
            enabled = true,
            onClick = { isExpanded = !isExpanded },
            iconRotation = iconRotation
        )
        AnimatedVisibility(
            visible = isExpanded,
            enter = fadeIn() + expandVertically(),
            exit = fadeOut() + shrinkVertically(),
        ) {
            // TODO add real map
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .height(200.dp)
                    .background(Color.Blue.copy(alpha = 0.2f))
            )
        }
    }
}
