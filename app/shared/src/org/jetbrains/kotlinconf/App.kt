package org.jetbrains.kotlinconf

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.compose.runtime.getValue
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dev.zacsweers.metrox.viewmodel.LocalMetroViewModelFactory
import org.jetbrains.kotlinconf.di.AppGraph
import org.jetbrains.kotlinconf.flags.LocalFlags
import org.jetbrains.kotlinconf.navigation.NavHost
import org.jetbrains.kotlinconf.utils.LocalNotificationBar
import org.jetbrains.kotlinconf.utils.LocalWindowSize
import org.jetbrains.kotlinconf.utils.rememberNotificationBarState
import org.jetbrains.kotlinconf.utils.windowSize

@Composable
fun App(
    appGraph: AppGraph,
    onThemeChange: ((isDarkTheme: Boolean) -> Unit)? = null,
) {
    val service = appGraph.conferenceService
    val currentTheme by service.getTheme().collectAsStateWithLifecycle(initialValue = Theme.SYSTEM)
    val isDarkTheme = when (currentTheme) {
        Theme.SYSTEM -> isSystemInDarkTheme()
        Theme.LIGHT -> false
        Theme.DARK -> true
    }

    val isOnboardingComplete = service.isOnboardingComplete()
        .collectAsStateWithLifecycle(initialValue = null)
        .value

    val flags by appGraph.flagsManager.flags.collectAsStateWithLifecycle()
    CompositionLocalProvider(
        LocalFlags provides flags,
        LocalAppGraph provides appGraph,
        LocalMetroViewModelFactory provides appGraph.metroViewModelFactory,
        LocalWindowSize provides windowSize(),
        LocalMapHandler provides rememberMapHandler(),
        LocalNotificationBar provides rememberNotificationBarState(),
    ) {
        if (isOnboardingComplete != null) {
            NavHost(isOnboardingComplete, isDarkTheme, onThemeChange)
        }
    }
}

public val LocalAppGraph: ProvidableCompositionLocal<AppGraph> =
    staticCompositionLocalOf {
        error("No AppGraph registered")
    }
