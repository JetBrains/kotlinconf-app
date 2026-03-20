package org.jetbrains.kotlinconf.screens

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.relocation.BringIntoViewRequester
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.LifecycleResumeEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dev.zacsweers.metrox.viewmodel.assistedMetroViewModel
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.kotlinconf.SessionId
import org.jetbrains.kotlinconf.generated.resources.Res
import org.jetbrains.kotlinconf.generated.resources.feedback_how_was_the_talk
import org.jetbrains.kotlinconf.generated.resources.feedback_how_was_the_workshop
import org.jetbrains.kotlinconf.generated.resources.feedback_thanks_for_rating
import org.jetbrains.kotlinconf.generated.resources.session_feedback_sent
import org.jetbrains.kotlinconf.ui.components.Emotion
import org.jetbrains.kotlinconf.ui.components.FeedbackForm
import org.jetbrains.kotlinconf.ui.components.KodeeIconLarge
import org.jetbrains.kotlinconf.ui.components.KodeeIconSmall
import org.jetbrains.kotlinconf.ui.components.TalkStatus
import org.jetbrains.kotlinconf.ui.components.Text
import org.jetbrains.kotlinconf.ui.theme.KotlinConfTheme
import org.jetbrains.kotlinconf.utils.LocalNotificationBar

@Composable
fun FeedbackBlock(
    sessionId: SessionId,
    tags: Set<String>,
    status: TalkStatus,
    onPrivacyNoticeNeeded: () -> Unit,
) {
    val viewModel = rememberFeedbackViewModel(sessionId, onPrivacyNoticeNeeded)
    val selectedEmotion by viewModel.selectedEmotion.collectAsStateWithLifecycle()

    Column {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.padding(start = 16.dp),
        ) {
            FeedbackQuestion(
                emotionSelected = selectedEmotion != null,
                tags = tags,
                modifier = Modifier.weight(1f),
            )
            FeedbackEmotionSelector(viewModel) { emotion, selected, modifier ->
                KodeeIconSmall(
                    emotion = emotion,
                    selected = selected,
                    modifier = modifier.padding(12.dp),
                )
            }
        }
        FeedbackFormSection(
            viewModel = viewModel,
            past = status == TalkStatus.Past,
        )
    }
}

@Composable
fun FeedbackPanel(
    sessionId: SessionId,
    tags: Set<String>,
    onPrivacyNoticeNeeded: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val viewModel = rememberFeedbackViewModel(sessionId, onPrivacyNoticeNeeded)
    val selectedEmotion by viewModel.selectedEmotion.collectAsStateWithLifecycle()

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .border(
                width = 1.dp,
                color = KotlinConfTheme.colors.strokePale,
                shape = KotlinConfTheme.shapes.roundedCornerMd,
            )
            .clip(KotlinConfTheme.shapes.roundedCornerMd)
            .background(KotlinConfTheme.colors.cardBackgroundPast),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 24.dp, vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            FeedbackQuestion(
                emotionSelected = selectedEmotion != null,
                tags = tags,
                contentAlignment = Alignment.Center,
            )
            Spacer(Modifier.height(16.dp))
            FeedbackEmotionSelector(
                viewModel = viewModel,
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
            ) { emotion, selected, selectorModifier ->
                KodeeIconLarge(
                    emotion = emotion,
                    selected = selected,
                    modifier = selectorModifier,
                )
            }
        }

        FeedbackFormSection(
            viewModel = viewModel,
            past = true,
            modifier = Modifier.padding(bottom = 16.dp),
        )
    }
}

@Composable
private fun rememberFeedbackViewModel(
    sessionId: SessionId,
    onPrivacyNoticeNeeded: () -> Unit,
): FeedbackViewModel {
    val viewModel =
        assistedMetroViewModel<FeedbackViewModel, FeedbackViewModel.Factory>(key = sessionId.id) {
            create(sessionId)
        }

    val shouldNavigate by viewModel.navigateToPrivacyNotice.collectAsStateWithLifecycle()
    LaunchedEffect(shouldNavigate) {
        if (shouldNavigate) {
            onPrivacyNoticeNeeded()
            viewModel.onNavigatedToPrivacyNotice()
        }
    }

    LifecycleResumeEffect(viewModel) {
        viewModel.onReturnedFromPrivacyNotice()
        onPauseOrDispose { }
    }

    return viewModel
}

@Composable
private fun FeedbackQuestion(
    emotionSelected: Boolean,
    tags: Set<String>,
    modifier: Modifier = Modifier,
    contentAlignment: Alignment = Alignment.CenterStart,
) {
    AnimatedContent(
        targetState = emotionSelected,
        modifier = modifier,
        contentAlignment = contentAlignment,
    ) { selected ->
        if (selected) {
            Text(
                stringResource(Res.string.feedback_thanks_for_rating),
                style = KotlinConfTheme.typography.h4,
                color = KotlinConfTheme.colors.primaryText,
            )
        } else {
            Text(
                text = stringResource(
                    if ("Workshop" in tags) Res.string.feedback_how_was_the_workshop
                    else Res.string.feedback_how_was_the_talk
                ),
                style = KotlinConfTheme.typography.text2,
                color = KotlinConfTheme.colors.primaryText,
                maxLines = 1,
            )
        }
    }
}

private val feedbackEmotions = listOf(Emotion.Negative, Emotion.Neutral, Emotion.Positive)

@Composable
private fun FeedbackEmotionSelector(
    viewModel: FeedbackViewModel,
    modifier: Modifier = Modifier,
    horizontalArrangement: Arrangement.Horizontal = Arrangement.Start,
    content: @Composable (emotion: Emotion, selected: Boolean, modifier: Modifier) -> Unit,
) {
    val selectedEmotion by viewModel.selectedEmotion.collectAsStateWithLifecycle()
    val hapticFeedback = LocalHapticFeedback.current
    Row(
        modifier = modifier.selectableGroup(),
        horizontalArrangement = horizontalArrangement,
    ) {
        feedbackEmotions.forEach { emotion ->
            val selected = selectedEmotion == emotion
            content(
                emotion,
                selected,
                Modifier.selectable(
                    selected = selected,
                    indication = null,
                    interactionSource = null,
                ) {
                    hapticFeedback.performHapticFeedback(HapticFeedbackType.Confirm)
                    viewModel.selectEmotion(emotion)
                },
            )
        }
    }
}

@Composable
private fun FeedbackFormSection(
    viewModel: FeedbackViewModel,
    past: Boolean,
    modifier: Modifier = Modifier,
) {
    val feedbackExpanded = viewModel.feedbackExpanded.collectAsStateWithLifecycle().value
    val selectedEmotion = viewModel.selectedEmotion.collectAsStateWithLifecycle().value

    if (feedbackExpanded && selectedEmotion != null) {
        var focusRequested by rememberSaveable { mutableStateOf(false) }
        val focusRequester = remember { FocusRequester() }
        val bringIntoViewRequester = remember { BringIntoViewRequester() }


        val windowSize = LocalWindowInfo.current.containerSize
        if (!focusRequested) {
            LaunchedEffect(Unit) {
                bringIntoViewRequester.bringIntoView(Rect(0f, 0f, windowSize.width.toFloat(), windowSize.height / 2f))
                focusRequester.requestFocus()
                focusRequested = true
            }
        }

        var feedbackText by rememberSaveable { mutableStateOf("") }
        val hapticFeedback = LocalHapticFeedback.current

        val notificationBar = LocalNotificationBar.current
        val feedbackSentMessage = stringResource(Res.string.session_feedback_sent)
        val feedbackSent by viewModel.feedbackSent.collectAsStateWithLifecycle()

        LaunchedEffect(feedbackSent) {
            if (feedbackSent) {
                notificationBar.show(feedbackSentMessage)
                viewModel.onFeedbackSentHandled()
                feedbackText = ""
            }
        }

        FeedbackForm(
            feedbackText = feedbackText,
            onFeedbackTextChange = { feedbackText = it },
            emotion = selectedEmotion,
            onSubmit = { _, _ ->
                hapticFeedback.performHapticFeedback(HapticFeedbackType.Confirm)
                viewModel.submitFeedbackWithComment(feedbackText)
            },
            onSkip = {
                viewModel.skipComment()
                feedbackText = ""
            },
            past = past,
            bringIntoViewRequester = bringIntoViewRequester,
            modifier = modifier.focusRequester(focusRequester),
        )
    }
}
