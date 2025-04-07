package org.jetbrains.kotlinconf.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Spring.StiffnessHigh
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.unit.dp
import kotlinconfapp.ui_components.generated.resources.Res
import kotlinconfapp.ui_components.generated.resources.arrow_right_24
import kotlinconfapp.ui_components.generated.resources.feedback_form_send
import kotlinconfapp.ui_components.generated.resources.feedback_form_type_something
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.jetbrains.kotlinconf.ui.theme.KotlinConfTheme
import org.jetbrains.kotlinconf.ui.theme.PreviewHelper

@Composable
fun FeedbackForm(
    feedbackText: String,
    onFeedbackTextChange: (String) -> Unit,
    emotion: Emotion?,
    onSubmit: (emotion: Emotion, comment: String) -> Unit,
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

    Box(modifier.fillMaxWidth().onKeyEvent { true }) {
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
                        .padding(bottom = 40.dp)
                        .drawBehind {
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
                        .padding(bottom = 32.dp)
                        .padding(16.dp)
                        .animateContentSize(spring())
                ) {
                    innerTextField()
                    AnimatedVisibility(
                        feedbackText.isEmpty(),
                        enter = fadeIn(tween(10)),
                        exit = fadeOut(tween(10)),
                    ) {
                        Text(
                            text = stringResource(Res.string.feedback_form_type_something),
                            style = KotlinConfTheme.typography.text1,
                            color = KotlinConfTheme.colors.placeholderText
                        )
                    }
                }
            }
        )
        Row(
            modifier = Modifier
                .height(80.dp)
                .padding(horizontal = 16.dp)
                .align(Alignment.BottomCenter),
            verticalAlignment = Alignment.Bottom
        ) {
            if (emotion != null) {
                KodeeEmotion(emotion)
            }
            Spacer(Modifier.weight(1f))
            Action(
                label = stringResource(Res.string.feedback_form_send),
                icon = Res.drawable.arrow_right_24,
                size = ActionSize.Large,
                enabled = feedbackText.isNotEmpty(),
                onClick = {
                    if (emotion != null) {
                        onSubmit(emotion, feedbackText)
                    }
                },
            )
        }
    }
}

@Preview
@Composable
internal fun FeedbackFormPreview() {
    PreviewHelper {
        var text by remember { mutableStateOf("") }
        FeedbackForm(text, { text = it }, Emotion.Positive, { emotion, text -> println("Feedback: $text") }, true)

        var text2 by remember { mutableStateOf("") }
        FeedbackForm(text2, { text2 = it },Emotion.Negative, { emotion, text -> println("Feedback: $text") }, false)
    }
}
