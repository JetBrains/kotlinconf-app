package org.jetbrains.kotlinconf.ui.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Spring.StiffnessHigh
import androidx.compose.animation.core.VisibilityThreshold
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.kotlinconf.ui.generated.resources.UiRes
import org.jetbrains.kotlinconf.ui.generated.resources.arrow_right_24
import org.jetbrains.kotlinconf.ui.generated.resources.close_24
import org.jetbrains.kotlinconf.ui.generated.resources.feedback_form_negative
import org.jetbrains.kotlinconf.ui.generated.resources.feedback_form_neutral
import org.jetbrains.kotlinconf.ui.generated.resources.feedback_form_positive
import org.jetbrains.kotlinconf.ui.generated.resources.feedback_form_send
import org.jetbrains.kotlinconf.ui.generated.resources.feedback_form_skip_comment
import org.jetbrains.kotlinconf.ui.generated.resources.feedback_form_type_something
import org.jetbrains.kotlinconf.ui.theme.Brand
import org.jetbrains.kotlinconf.ui.theme.KotlinConfTheme
import org.jetbrains.kotlinconf.ui.theme.PreviewHelper
import org.jetbrains.kotlinconf.ui.utils.PreviewLightDark

@Composable
fun FeedbackForm(
    feedbackText: String,
    onFeedbackTextChange: (String) -> Unit,
    emotion: Emotion?,
    onSubmit: (emotion: Emotion, comment: String) -> Unit,
    onSkip: () -> Unit,
    past: Boolean,
    modifier: Modifier = Modifier,
) {
    val interactionSource = remember { MutableInteractionSource() }
    val focused by interactionSource.collectIsFocusedAsState()

    val verticalBorderColor by animateColorAsState(
        if (focused) KotlinConfTheme.colors.strokeInputFocus
        else Color.Transparent,
        animationSpec = spring(stiffness = StiffnessHigh),
    )
    val horizontalBorderColor by animateColorAsState(
        if (focused) KotlinConfTheme.colors.strokeInputFocus
        else KotlinConfTheme.colors.strokePale,
        animationSpec = spring(stiffness = StiffnessHigh),
    )
    val fieldBackgroundColor by animateColorAsState(
        if (past) KotlinConfTheme.colors.mainBackground
        else KotlinConfTheme.colors.tileBackground,
    )

    Column(modifier.fillMaxWidth().onKeyEvent { true }) {
        BasicTextField(
            value = feedbackText,
            onValueChange = onFeedbackTextChange,
            interactionSource = interactionSource,
            textStyle = KotlinConfTheme.typography.text1
                .copy(color = KotlinConfTheme.colors.primaryText),
            cursorBrush = SolidColor(KotlinConfTheme.colors.primaryText),
            decorationBox = { innerTextField ->
                Box(
                    Modifier
                        .drawWithContent {
                            drawContent()

                            val lineWidth = 1.dp.toPx()
                            val halfLineWidth = lineWidth / 2

                            // Top
                            drawLine(
                                horizontalBorderColor,
                                Offset(0f + halfLineWidth, 0f + halfLineWidth),
                                Offset(size.width - halfLineWidth, 0f + halfLineWidth),
                                lineWidth
                            )
                            // Bottom
                            drawLine(
                                horizontalBorderColor,
                                Offset(0f + halfLineWidth, size.height - halfLineWidth),
                                Offset(size.width - halfLineWidth, size.height - halfLineWidth),
                                lineWidth
                            )
                            // Start
                            drawLine(
                                verticalBorderColor,
                                Offset(0f + halfLineWidth, 0f + halfLineWidth),
                                Offset(0f + halfLineWidth, size.height - halfLineWidth),
                                lineWidth
                            )
                            // End
                            drawLine(
                                verticalBorderColor,
                                Offset(size.width - halfLineWidth, 0f + halfLineWidth),
                                Offset(size.width - halfLineWidth, size.height - halfLineWidth),
                                lineWidth
                            )
                        }
                        .background(fieldBackgroundColor)
                        .fillMaxWidth()
                        .heightIn(min = 132.dp)
                        .padding(16.dp)
                        .animateContentSize(spring(), alignment = Alignment.Center)
                ) {
                    innerTextField()
                    androidx.compose.animation.AnimatedVisibility(
                        feedbackText.isEmpty(),
                        enter = fadeIn(tween(10)),
                        exit = fadeOut(tween(10)),
                    ) {
                        Text(
                            text = stringResource(UiRes.string.feedback_form_type_something),
                            style = KotlinConfTheme.typography.text1,
                            color = KotlinConfTheme.colors.placeholderText
                        )
                    }
                }
            }
        )
        Row(
            modifier = Modifier
                .padding(horizontal = 16.dp, vertical = 12.dp),
        ) {
            Action(
                label = stringResource(UiRes.string.feedback_form_skip_comment),
                icon = UiRes.drawable.close_24,
                size = ActionSize.Large,
                onClick = {
                    onSkip()
                },
            )

            Spacer(Modifier.weight(1f))
            Action(
                label = stringResource(UiRes.string.feedback_form_send),
                icon = UiRes.drawable.arrow_right_24,
                size = ActionSize.Large,
                enabled = feedbackText.isNotEmpty(),
                onClick = {
                    if (emotion != null) {
                        onSubmit(emotion, feedbackText)
                    }
                },
            )
        }

        if (emotion != null) {
            KodeeAdvice(
                emotion = emotion,
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .padding(bottom = 12.dp)
                    .widthIn(max = 400.dp)
                    .align(Alignment.CenterHorizontally),
            )
        }
    }
}

@Composable
private fun KodeeAdvice(
    emotion: Emotion,
    modifier: Modifier = Modifier,
) {
    val bubbleText = when (emotion) {
        Emotion.Positive -> stringResource(UiRes.string.feedback_form_positive)
        Emotion.Neutral -> stringResource(UiRes.string.feedback_form_neutral)
        Emotion.Negative -> stringResource(UiRes.string.feedback_form_negative)
    }

    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Row(
            Modifier
                .weight(1f)
                .animateContentSize(
                    alignment = Alignment.Center,
                    animationSpec = spring(
                        stiffness = StiffnessHigh,
                        visibilityThreshold = IntSize.VisibilityThreshold,
                    )
                )
        ) {
            val arrowWidth = 11.dp
            AnimatedContent(
                targetState = bubbleText,
                transitionSpec = {
                    fadeIn(animationSpec = tween(220, delayMillis = 90)) togetherWith
                            fadeOut(animationSpec = tween(90))
                },
                modifier = Modifier
                    .weight(1f)
                    .drawBehind {
                        val aw = arrowWidth.toPx()
                        val ah = aw * 2

                        val path = Path().apply {
                            addRoundRect(
                                RoundRect(
                                    left = 0f,
                                    top = 0f,
                                    right = size.width,
                                    bottom = size.height,
                                    cornerRadius = CornerRadius(8.dp.toPx()),
                                )
                            )
                            val arrowTop = (size.height - ah) / 2
                            moveTo(size.width, arrowTop)
                            lineTo(size.width + aw, arrowTop + ah / 2)
                            lineTo(size.width, arrowTop + ah)
                            close()
                        }

                        drawPath(path, Brand.purple20)
                    }
                    .padding(horizontal = 12.dp, vertical = 8.dp),
            ) { targetText ->
                Text(
                    text = targetText,
                    style = KotlinConfTheme.typography.text2,
                )
            }
            Spacer(Modifier.width(8.dp + arrowWidth))
        }
        KodeeEmotion(
            emotion = emotion,
            modifier = Modifier
                .align(Alignment.CenterVertically)
                .size(72.dp)
        )
    }
}

private data class FeedbackFormPreviewParams(
    val emotion: Emotion,
    val text: String,
)

private class FeedbackFormPreviewParameterProvider :
    PreviewParameterProvider<FeedbackFormPreviewParams> {
    override val values = sequence {
        for (emotion in Emotion.entries) {
            for (text in listOf("", "Wow such talk")) {
                yield(FeedbackFormPreviewParams(emotion, text))
            }
        }
    }

    override fun getDisplayName(index: Int): String {
        val param = values.elementAt(index)
        val textLabel = if (param.text.isEmpty()) "Empty" else "Filled"
        return "${param.emotion.name} - $textLabel"
    }
}

@PreviewLightDark
@Composable
private fun FeedbackFormPreview(
    @PreviewParameter(FeedbackFormPreviewParameterProvider::class) params: FeedbackFormPreviewParams,
) = PreviewHelper {
    var text by remember(params.text) { mutableStateOf(params.text) }
    FeedbackForm(text, { text = it }, params.emotion, { _, _ -> }, {}, past = false)
}
