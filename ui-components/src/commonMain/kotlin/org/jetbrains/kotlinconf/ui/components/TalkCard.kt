package org.jetbrains.kotlinconf.ui.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.EaseOut
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.relocation.BringIntoViewRequester
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.unit.dp
import kotlinconfapp.ui_components.generated.resources.Res
import kotlinconfapp.ui_components.generated.resources.action_bookmark
import kotlinconfapp.ui_components.generated.resources.arrow_right_24
import kotlinconfapp.ui_components.generated.resources.bookmark_24
import kotlinconfapp.ui_components.generated.resources.bookmark_24_fill
import kotlinconfapp.ui_components.generated.resources.lightning_16_fill
import kotlinconfapp.ui_components.generated.resources.lightning_talk
import kotlinconfapp.ui_components.generated.resources.talk_card_how_was_the_talk
import kotlinconfapp.ui_components.generated.resources.talk_card_how_was_the_workshop
import kotlinconfapp.ui_components.generated.resources.talk_card_your_feedback
import kotlinconfapp.ui_components.generated.resources.up_24
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.jetbrains.kotlinconf.ui.theme.Brand
import org.jetbrains.kotlinconf.ui.theme.KotlinConfTheme
import org.jetbrains.kotlinconf.ui.theme.PreviewHelper

@Composable
internal fun buildHighlightedString(
    text: String,
    highlights: List<IntRange>,
): AnnotatedString = buildAnnotatedString {
    append(text)
    highlights.forEach { range ->
        // Ignore invalid ranges
        if (!range.isEmpty()) {
            addStyle(
                style = SpanStyle(
                    color = KotlinConfTheme.colors.primaryTextInverted,
                    background = Brand.magenta100,
                ),
                start = range.first,
                end = range.last + 1,
            )
        }
    }
}

enum class TalkStatus {
    Past, Live, Upcoming,
}

private val CardTalkShape = RoundedCornerShape(8.dp)

@Composable
fun TalkCard(
    title: String,
    titleHighlights: List<IntRange>,
    bookmarked: Boolean,
    onBookmark: (Boolean) -> Unit,
    tags: List<String>,
    tagHighlights: List<String>,
    speakers: String,
    speakerHighlights: List<IntRange>,
    location: String,
    lightning: Boolean,
    time: String,
    timeNote: String?,
    status: TalkStatus,
    initialEmotion: Emotion? = null,
    onSubmitFeedback: (Emotion?) -> Unit,
    onRequestFeedbackWithComment: (() -> Unit)?,
    onSubmitFeedbackWithComment: (Emotion, String) -> Unit,
    onClick: () -> Unit,
    feedbackEnabled: Boolean,
    userSignedIn: Boolean,
    modifier: Modifier = Modifier,
) {
    val backgroundColor by animateColorAsState(
        if (status == TalkStatus.Past) KotlinConfTheme.colors.cardBackgroundPast
        else Color.Transparent,
        animationSpec = tween(1000),
    )
    val textColor by animateColorAsState(
        if (status == TalkStatus.Past) KotlinConfTheme.colors.secondaryText
        else KotlinConfTheme.colors.primaryText,
        animationSpec = tween(1000),
    )

    Column(
        modifier
            .border(
                width = 1.dp,
                color = KotlinConfTheme.colors.strokePale,
                shape = CardTalkShape,
            )
            .clip(CardTalkShape)
            .clickable(onClick = onClick)
            .background(backgroundColor)
    ) {
        TopBlock(
            title = title,
            titleHighlights = titleHighlights,
            textColor = textColor,
            bookmarked = bookmarked,
            onBookmark = onBookmark,
            tags = tags,
            selectedTags = tagHighlights,
            speakers = speakers,
            speakerHighlights = speakerHighlights,
        )
        Spacer(Modifier.weight(1f))
        Divider(
            thickness = 1.dp,
            color = KotlinConfTheme.colors.strokePale,
        )
        TimeBlock(
            location = location,
            textColor = textColor,
            timeNote = timeNote,
            status = status,
            lightning = lightning,
            time = time,
        )
        AnimatedVisibility(
            visible = feedbackEnabled,
            enter = fadeIn(tween(300, 70, EaseOut)) + expandVertically(tween(150, 0, EaseOut)),
            exit = fadeOut(tween(300, 70, EaseOut)) + shrinkVertically(tween(150, 0, EaseOut)),
        ) {
            Divider(
                thickness = 1.dp,
                color = KotlinConfTheme.colors.strokePale,
            )
            FeedbackBlock(
                status = status,
                userSignedIn = userSignedIn,
                initialEmotion = initialEmotion,
                onSubmitFeedback = onSubmitFeedback,
                onSubmitFeedbackWithComment = onSubmitFeedbackWithComment,
                onRequestFeedbackWithComment = onRequestFeedbackWithComment,
                isWorkshop = tags.contains("Workshop"),
            )
        }
    }
}

@Composable
@OptIn(ExperimentalLayoutApi::class)
private fun TopBlock(
    title: String,
    titleHighlights: List<IntRange>,
    textColor: Color,
    bookmarked: Boolean,
    onBookmark: (Boolean) -> Unit,
    tags: List<String>,
    selectedTags: List<String>,
    speakers: String,
    speakerHighlights: List<IntRange>,
) {
    Column(
        modifier = Modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Row {
            StyledText(
                text = buildHighlightedString(title, titleHighlights),
                style = KotlinConfTheme.typography.h3,
                color = textColor,
                maxLines = 2,
                modifier = Modifier.weight(1f),
            )

            Spacer(Modifier.width(8.dp))

            val iconColor by animateColorAsState(
                if (bookmarked) KotlinConfTheme.colors.orangeText
                else KotlinConfTheme.colors.primaryText
            )
            Icon(
                modifier = Modifier
                    .toggleable(
                        value = bookmarked,
                        onValueChange = onBookmark,
                        role = Role.Switch,
                        interactionSource = null,
                        indication = null,
                    )
                    .size(24.dp),
                painter = painterResource(
                    if (bookmarked) Res.drawable.bookmark_24_fill
                    else Res.drawable.bookmark_24
                ),
                contentDescription = stringResource(Res.string.action_bookmark),
                tint = iconColor,
            )
        }
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            tags.forEach { tag ->
                CardTag(label = tag, selected = tag in selectedTags)
            }
        }
        StyledText(
            text = buildHighlightedString(speakers, speakerHighlights),
            color = KotlinConfTheme.colors.secondaryText,
            style = KotlinConfTheme.typography.text2,
            maxLines = 1,
        )
    }
}

@Composable
private fun TimeBlock(
    location: String,
    textColor: Color,
    timeNote: String?,
    status: TalkStatus,
    lightning: Boolean,
    time: String,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.padding(16.dp)
    ) {
        StyledText(
            text = location,
            style = KotlinConfTheme.typography.text2,
            color = textColor,
        )

        Spacer(Modifier.weight(1f))

        if (timeNote != null) {
            StyledText(
                text = timeNote,
                style = KotlinConfTheme.typography.text2,
                color = KotlinConfTheme.colors.noteText,
                maxLines = 1,
            )
        }

        AnimatedVisibility(status == TalkStatus.Live, enter = fadeIn(), exit = fadeOut()) {
            NowLabel()
        }

        if (lightning) {
            Icon(
                modifier = Modifier.size(16.dp),
                painter = painterResource(Res.drawable.lightning_16_fill),
                contentDescription = stringResource(Res.string.lightning_talk),
                tint = KotlinConfTheme.colors.orangeText,
            )
        }

        StyledText(
            text = time,
            style = KotlinConfTheme.typography.text2,
            color = textColor,
            maxLines = 1,
        )
    }
}

private const val FeedbackAnimationDuration = 50

@Composable
private fun FeedbackBlock(
    status: TalkStatus,
    userSignedIn: Boolean,
    initialEmotion: Emotion? = null,
    onSubmitFeedback: (Emotion?) -> Unit,
    onRequestFeedbackWithComment: (() -> Unit)?,
    onSubmitFeedbackWithComment: (Emotion, String) -> Unit,
    isWorkshop: Boolean,
) {
    var selectedEmotion by remember { mutableStateOf(initialEmotion) }
    var feedbackExpanded by remember { mutableStateOf(false) }

    Column(
        Modifier
            .clickable(
                indication = null,
                interactionSource = remember { MutableInteractionSource() },
            ) {
                if (selectedEmotion != null) {
                    feedbackExpanded = !feedbackExpanded
                }
            },
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.padding(start = 16.dp),
        ) {
            AnimatedContent(
                targetState = selectedEmotion != null,
                transitionSpec = {
                    fadeIn(tween(FeedbackAnimationDuration)) togetherWith
                            fadeOut(tween(FeedbackAnimationDuration))
                },
                modifier = Modifier.fillMaxHeight(),
                contentAlignment = Alignment.CenterStart,
            ) { emotionSelected ->
                if (emotionSelected) {
                    if (onRequestFeedbackWithComment != null) {
                        Action(
                            label = stringResource(Res.string.talk_card_your_feedback),
                            icon = Res.drawable.arrow_right_24,
                            size = ActionSize.Medium,
                            onClick = onRequestFeedbackWithComment,
                        )
                    } else {
                        val iconRotation by animateFloatAsState(if (feedbackExpanded) 0f else 180f)
                        Action(
                            label = stringResource(Res.string.talk_card_your_feedback),
                            icon = Res.drawable.up_24,
                            size = ActionSize.Medium,
                            onClick = { feedbackExpanded = !feedbackExpanded },
                            iconRotation = iconRotation,
                        )
                    }
                } else {
                    StyledText(
                        text = stringResource(
                            if (isWorkshop) Res.string.talk_card_how_was_the_workshop
                            else Res.string.talk_card_how_was_the_talk
                        ),
                        style = KotlinConfTheme.typography.text2,
                        color = KotlinConfTheme.colors.primaryText,
                    )
                }
            }
            Spacer(Modifier.weight(1f))
            Row {
                val feedbackEmotions = remember {
                    listOf(Emotion.Negative, Emotion.Neutral, Emotion.Positive)
                }
                val hapticFeedback = LocalHapticFeedback.current
                feedbackEmotions.forEach { emotion ->
                    KodeeIconSmall(
                        emotion = emotion,
                        selected = selectedEmotion == emotion,
                        modifier = Modifier
                            .clickable(indication = null, interactionSource = null) {
                                if (userSignedIn) {
                                    hapticFeedback.performHapticFeedback(HapticFeedbackType.Confirm)
                                    selectedEmotion = if (emotion == selectedEmotion) null else emotion
                                    onSubmitFeedback(selectedEmotion)

                                    if (selectedEmotion != null) {
                                        if (onRequestFeedbackWithComment != null) {
                                            onRequestFeedbackWithComment()
                                        } else {
                                            feedbackExpanded = true
                                        }
                                    } else {
                                        feedbackExpanded = false
                                    }
                                } else {
                                    hapticFeedback.performHapticFeedback(HapticFeedbackType.Reject)
                                    onSubmitFeedback(selectedEmotion)
                                }
                            }
                            .padding(12.dp),
                    )
                }
            }
        }
        AnimatedVisibility(
            visible = feedbackExpanded,
            enter = fadeIn() + expandVertically(clip = false, expandFrom = Alignment.Top),
            exit = fadeOut() + shrinkVertically(clip = false, shrinkTowards = Alignment.Top),
        ) {
            val focusRequester = remember { FocusRequester() }
            val bringIntoViewRequester = remember { BringIntoViewRequester() }
            val hapticFeedback = LocalHapticFeedback.current
            LaunchedEffect(focusRequester) {
                focusRequester.requestFocus()
                bringIntoViewRequester.bringIntoView()
            }
            FeedbackForm(
                emotion = selectedEmotion,
                onSubmit = { emotion, comment ->
                    hapticFeedback.performHapticFeedback(HapticFeedbackType.Confirm)
                    onSubmitFeedbackWithComment(emotion, comment)
                    feedbackExpanded = false
                },
                past = status == TalkStatus.Past,
                modifier = Modifier.padding(bottom = 14.dp).focusRequester(focusRequester)
            )
        }
    }
}

@Preview
@Composable
internal fun TalkCardPreview() {
    PreviewHelper {
        Row(Modifier.fillMaxWidth()) {
            var bookmarked by remember { mutableStateOf(false) }
            TalkCard(
                title = "Asynchronous Programming With Kotlin Coroutines",
                titleHighlights = listOf(
                    30..35,
                ),
                bookmarked = bookmarked,
                onBookmark = { bookmarked = it },
                tags = listOf(
                    "Workshop", "Kotlin", "Coroutines", "Multiplatform",
                    "Label", "Label", "Label", "Label", "Label",
                ),
                tagHighlights = listOf(
                    "Kotlin", "Multiplatform",
                ),
                speakers = "Sebastian Aigner, Vsevolod Tolstopyatov",
                speakerHighlights = listOf(10..15),
                location = "Auditorium 14",
                lightning = true,
                time = "9:00 – 10:00",
                timeNote = null,
                status = TalkStatus.Live,
                initialEmotion = Emotion.Positive,
                onRequestFeedbackWithComment = null,
                onSubmitFeedbackWithComment = { e, s -> println("Feedback, emotion + comment: $e, $s") },
                onSubmitFeedback = { e -> println("Feedback, emotion only: $e") },
                onClick = { println("Clicked session") },
                modifier = Modifier.weight(1f),
                feedbackEnabled = true,
                userSignedIn = true,
            )
            Spacer(Modifier.width(16.dp))
            TalkCard(
                title = "Asynchronous Programming With Kotlin Coroutines",
                titleHighlights = emptyList(),
                bookmarked = false,
                onBookmark = { },
                tags = listOf(
                    "Workshop", "Kotlin", "Coroutines", "Multiplatform",
                    "Label", "Label", "Label", "Label", "Label",
                ),
                tagHighlights = listOf(),
                speakers = "Sebastian Aigner, Vsevolod Tolstopyatov",
                speakerHighlights = emptyList(),
                location = "Auditorium 14",
                lightning = true,
                time = "9:00 – 10:00",
                timeNote = "In 10 min",
                status = TalkStatus.Upcoming,
                initialEmotion = null,
                onRequestFeedbackWithComment = null,
                onSubmitFeedbackWithComment = { e, s -> println("Feedback, emotion + comment: $e, $s") },
                onSubmitFeedback = { e -> println("Feedback, emotion only: $e") },
                onClick = { println("Clicked session") },
                modifier = Modifier.weight(1f),
                feedbackEnabled = false,
                userSignedIn = true,
            )
        }
    }
}
