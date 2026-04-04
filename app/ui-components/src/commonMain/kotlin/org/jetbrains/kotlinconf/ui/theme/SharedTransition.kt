package org.jetbrains.kotlinconf.ui.theme

import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.runtime.Composable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.Modifier

/**
 * Provides [SharedTransitionScope] to descendant composables, typically provided at the app/nav root
 * by wrapping content in [SharedTransitionLayout].
 */
val LocalSharedTransitionScope = compositionLocalOf<SharedTransitionScope?> { null }

/**
 * Provides [AnimatedContentScope] to descendant composables, provided locally within an [AnimatedContent] block.
 * Components use this together with [LocalSharedTransitionScope] to set up shared element transitions.
 */
val LocalAnimatedContentScope = compositionLocalOf<AnimatedContentScope?> { null }

@Composable
fun sharedElementModifier(key: String): Modifier {
    val sharedTransitionScope = LocalSharedTransitionScope.current
    val animatedContentScope = LocalAnimatedContentScope.current
    return if (sharedTransitionScope != null && animatedContentScope != null) {
        with(sharedTransitionScope) {
            Modifier.sharedElement(
                rememberSharedContentState(key),
                animatedContentScope,
            )
        }
    } else Modifier
}
