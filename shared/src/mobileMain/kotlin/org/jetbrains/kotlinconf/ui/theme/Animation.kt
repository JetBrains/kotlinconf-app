package org.jetbrains.kotlinconf.ui.theme

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import moe.tlaster.precompose.navigation.transition.NavTransition

internal val DEFAULT_TRANSITION = NavTransition(
    createTransition = EnterTransition.None,
    destroyTransition = ExitTransition.None,
    pauseTransition = ExitTransition.None,
    resumeTransition = EnterTransition.None
)

