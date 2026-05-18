package org.jetbrains.kotlinconf.utils

import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith

/**
 * Used for AnimatedContent transitions between loading/content/error states.
 */
val FadingAnimationSpec =
    fadeIn(animationSpec = tween(220, delayMillis = 90)) togetherWith
            fadeOut(animationSpec = tween(90))
