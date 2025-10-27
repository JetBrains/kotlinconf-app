package org.jetbrains.kotlinconf

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.ContentTransform
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation3.scene.Scene
import org.jetbrains.kotlinconf.navigation.KotlinConfNavHost
import org.jetbrains.kotlinconf.ui.theme.KotlinConfTheme
import org.koin.compose.koinInject

@Composable
fun App(
    onThemeChange: ((isDarkTheme: Boolean) -> Unit)? = null,
    popTransactionSpec: (AnimatedContentTransitionScope<Scene<Any>>.() -> ContentTransform)? = null,
) {
    val service = koinInject<ConferenceService>()
    val currentTheme by service.getTheme().collectAsStateWithLifecycle(initialValue = Theme.SYSTEM)
    val isDarkTheme = when (currentTheme) {
        Theme.SYSTEM -> isSystemInDarkTheme()
        Theme.LIGHT -> false
        Theme.DARK -> true
    }

    if (onThemeChange != null) {
        LaunchedEffect(isDarkTheme) { onThemeChange(isDarkTheme) }
    }

    val isOnboardingComplete = service.isOnboardingComplete()
        .collectAsStateWithLifecycle(initialValue = null)
        .value

    val flags by koinInject<FlagsManager>().flags.collectAsStateWithLifecycle()
    CompositionLocalProvider(LocalFlags provides flags) {
        KotlinConfTheme(
            darkTheme = isDarkTheme,
            rippleEnabled = LocalFlags.current.rippleEnabled,
        ) {
            Box(
                Modifier
                    .fillMaxSize()
                    .background(KotlinConfTheme.colors.mainBackground)
            ) {
                if (isOnboardingComplete != null) {
                    KotlinConfNavHost(isOnboardingComplete, popTransactionSpec)
                }
            }
        }
    }
}
