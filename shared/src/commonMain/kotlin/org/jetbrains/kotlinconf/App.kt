package org.jetbrains.kotlinconf

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavBackStackEntry
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import org.jetbrains.kotlinconf.ui.components.StyledText
import org.jetbrains.kotlinconf.ui.theme.KotlinConfTheme

const val apiEndpoint = "https://kotlinconf-app-prod.labs.jb.gg"

@Composable
fun App(context: ApplicationContext) {
    val navController = rememberNavController()
    KotlinConfTheme {
        val service = remember {
            ConferenceService(context, apiEndpoint)
        }
        CompositionLocalProvider(LocalNavController provides navController) {
            Box(
                Modifier.fillMaxSize()
                    .background(KotlinConfTheme.colors.mainBackground),
                contentAlignment = Alignment.Center
            ) {
                NavHost(
                    navController = navController,
                    startDestination = StartScreen,
                    modifier = Modifier.fillMaxSize(),
                    enterTransition = enterTransition { it },
                    exitTransition = exitTransition { -it },
                    popEnterTransition = enterTransition { -it },
                    popExitTransition = exitTransition { it },
                ) {
                    composable<StartScreen> {
                        StyledText("Start screen")
                    }
                }
            }
        }
    }
}

private fun enterTransition(
    initialOffsetX: (fullWidth: Int) -> Int,
): AnimatedContentTransitionScope<NavBackStackEntry>.() -> EnterTransition = {
    slideInHorizontally(animationSpec = tween(300), initialOffsetX = initialOffsetX)
}

private fun exitTransition(
    targetOffsetX: (fullWidth: Int) -> Int,
): AnimatedContentTransitionScope<NavBackStackEntry>.() -> ExitTransition = {
    slideOutHorizontally(animationSpec = tween(300), targetOffsetX = targetOffsetX)
}