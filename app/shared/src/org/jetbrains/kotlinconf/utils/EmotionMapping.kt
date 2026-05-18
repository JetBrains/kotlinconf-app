package org.jetbrains.kotlinconf.utils

import org.jetbrains.kotlinconf.Score
import org.jetbrains.kotlinconf.ui.components.Emotion

fun Emotion.toScore(): Score = when (this) {
    Emotion.Positive -> Score.GOOD
    Emotion.Neutral -> Score.OK
    Emotion.Negative -> Score.BAD
}
