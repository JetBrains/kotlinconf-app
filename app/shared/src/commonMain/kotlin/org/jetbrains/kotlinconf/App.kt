package org.jetbrains.kotlinconf

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.jetbrains.kotlinconf.flags.FlagsManager
import org.jetbrains.kotlinconf.flags.LocalFlags
import org.jetbrains.kotlinconf.navigation.NavHost
import org.jetbrains.kotlinconf.utils.LocalNotificationBar
import org.jetbrains.kotlinconf.utils.LocalWindowSize
import org.jetbrains.kotlinconf.utils.rememberNotificationBarState
import org.jetbrains.kotlinconf.utils.windowSize
import org.koin.compose.koinInject

@Composable
fun App(
    onThemeChange: ((isDarkTheme: Boolean) -> Unit)? = null,
) {
    val service = koinInject<ConferenceService>()
    val currentTheme by service.getTheme().collectAsStateWithLifecycle(initialValue = Theme.SYSTEM)
    val isDarkTheme = when (currentTheme) {
        Theme.SYSTEM -> isSystemInDarkTheme()
        Theme.LIGHT -> false
        Theme.DARK -> true
    }

    val isOnboardingComplete = service.isOnboardingComplete()
        .collectAsStateWithLifecycle(initialValue = null)
        .value

    val flags by koinInject<FlagsManager>().flags.collectAsStateWithLifecycle()
    CompositionLocalProvider(
        LocalFlags provides flags,
        LocalWindowSize provides windowSize(),
        LocalMapHandler provides rememberMapHandler(),
        LocalNotificationBar provides rememberNotificationBarState(),
    ) {
        if (isOnboardingComplete != null) {
            NavHost(isOnboardingComplete, isDarkTheme, onThemeChange)
        }
    }
}
