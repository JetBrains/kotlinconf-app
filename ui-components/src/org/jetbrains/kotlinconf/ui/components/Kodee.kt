package org.jetbrains.kotlinconf.ui.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinconfapp.ui_components.generated.resources.Res
import kotlinconfapp.ui_components.generated.resources.action_rate_negative
import kotlinconfapp.ui_components.generated.resources.action_rate_neutral
import kotlinconfapp.ui_components.generated.resources.action_rate_positive
import kotlinconfapp.ui_components.generated.resources.kodee_emotion_negative
import kotlinconfapp.ui_components.generated.resources.kodee_emotion_neutral
import kotlinconfapp.ui_components.generated.resources.kodee_emotion_positive
import kotlinconfapp.ui_components.generated.resources.kodee_large_negative_dark
import kotlinconfapp.ui_components.generated.resources.kodee_large_negative_light
import kotlinconfapp.ui_components.generated.resources.kodee_large_negative_selected
import kotlinconfapp.ui_components.generated.resources.kodee_large_neutral_dark
import kotlinconfapp.ui_components.generated.resources.kodee_large_neutral_light
import kotlinconfapp.ui_components.generated.resources.kodee_large_neutral_selected
import kotlinconfapp.ui_components.generated.resources.kodee_large_positive_dark
import kotlinconfapp.ui_components.generated.resources.kodee_large_positive_light
import kotlinconfapp.ui_components.generated.resources.kodee_large_positive_selected
import kotlinconfapp.ui_components.generated.resources.kodee_small_negative_filled
import kotlinconfapp.ui_components.generated.resources.kodee_small_negative_outline
import kotlinconfapp.ui_components.generated.resources.kodee_small_neutral_filled
import kotlinconfapp.ui_components.generated.resources.kodee_small_neutral_outline
import kotlinconfapp.ui_components.generated.resources.kodee_small_positive_filled
import kotlinconfapp.ui_components.generated.resources.kodee_small_positive_outline
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.jetbrains.kotlinconf.ui.theme.KotlinConfTheme
import org.jetbrains.kotlinconf.ui.theme.PreviewHelper

enum class Emotion {
    Negative, Neutral, Positive;
}

@Composable
private fun ratingDescription(emotion: Emotion): String {
    return when (emotion) {
        Emotion.Negative -> stringResource(Res.string.action_rate_negative)
        Emotion.Neutral -> stringResource(Res.string.action_rate_neutral)
        Emotion.Positive -> stringResource(Res.string.action_rate_positive)
    }
}

@Composable
fun KodeeIconSmall(
    emotion: Emotion,
    selected: Boolean,
    modifier: Modifier = Modifier,
    contentDescription: String? = ratingDescription(emotion),
) {
    val resource = if (selected) {
        when (emotion) {
            Emotion.Negative -> Res.drawable.kodee_small_negative_filled
            Emotion.Neutral -> Res.drawable.kodee_small_neutral_filled
            Emotion.Positive -> Res.drawable.kodee_small_positive_filled
        }
    } else {
        when (emotion) {
            Emotion.Negative -> Res.drawable.kodee_small_negative_outline
            Emotion.Neutral -> Res.drawable.kodee_small_neutral_outline
            Emotion.Positive -> Res.drawable.kodee_small_positive_outline
        }
    }

    Image(
        imageVector = vectorResource(resource),
        contentDescription = contentDescription,
        modifier = modifier.size(24.dp),
    )
}

@Composable
fun KodeeIconLarge(
    emotion: Emotion,
    selected: Boolean,
    modifier: Modifier = Modifier,
    contentDescription: String? = ratingDescription(emotion),
) {
    val resource = if (selected) {
        when (emotion) {
            Emotion.Negative -> Res.drawable.kodee_large_negative_selected
            Emotion.Neutral -> Res.drawable.kodee_large_neutral_selected
            Emotion.Positive -> Res.drawable.kodee_large_positive_selected
        }
    } else {
        if (KotlinConfTheme.colors.isDark) {
            when (emotion) {
                Emotion.Negative -> Res.drawable.kodee_large_negative_dark
                Emotion.Neutral -> Res.drawable.kodee_large_neutral_dark
                Emotion.Positive -> Res.drawable.kodee_large_positive_dark
            }
        } else {
            when (emotion) {
                Emotion.Negative -> Res.drawable.kodee_large_negative_light
                Emotion.Neutral -> Res.drawable.kodee_large_neutral_light
                Emotion.Positive -> Res.drawable.kodee_large_positive_light
            }
        }
    }

    Image(
        imageVector = vectorResource(resource),
        contentDescription = contentDescription,
        modifier = modifier.size(width = 64.dp, height = 55.dp)
    )
}

@Composable
fun KodeeEmotion(
    emotion: Emotion,
    modifier: Modifier = Modifier,
) {
    AnimatedContent(emotion) { targetEmotion ->
        val resource = when (targetEmotion) {
            Emotion.Negative -> Res.drawable.kodee_emotion_negative
            Emotion.Neutral -> Res.drawable.kodee_emotion_neutral
            Emotion.Positive -> Res.drawable.kodee_emotion_positive
        }
        Image(
            imageVector = vectorResource(resource),
            contentDescription = null,
            modifier = modifier.size(80.dp),
        )
    }
}

@Preview
@Composable
internal fun KodeeIconsPreview() {
    PreviewHelper {
        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
            KodeeIconSmall(Emotion.Negative, false)
            KodeeIconSmall(Emotion.Neutral, false)
            KodeeIconSmall(Emotion.Positive, false)

            KodeeIconSmall(Emotion.Negative, true)
            KodeeIconSmall(Emotion.Neutral, true)
            KodeeIconSmall(Emotion.Positive, true)
        }
        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
            KodeeIconLarge(Emotion.Negative, false)
            KodeeIconLarge(Emotion.Neutral, false)
            KodeeIconLarge(Emotion.Positive, false)

            KodeeIconLarge(Emotion.Negative, true)
            KodeeIconLarge(Emotion.Neutral, true)
            KodeeIconLarge(Emotion.Positive, true)
        }
        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
            KodeeEmotion(Emotion.Negative)
            KodeeEmotion(Emotion.Neutral)
            KodeeEmotion(Emotion.Positive)
        }
    }
}
