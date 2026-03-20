package org.jetbrains.kotlinconf

import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dev.zacsweers.metrox.viewmodel.LocalMetroViewModelFactory
import org.jetbrains.kotlinconf.di.AppGraph
import org.jetbrains.kotlinconf.flags.LocalFlags
import org.jetbrains.kotlinconf.navigation.AppRoute
import org.jetbrains.kotlinconf.navigation.ExternalNavigator
import org.jetbrains.kotlinconf.navigation.LocalUseNativeNavigation
import org.jetbrains.kotlinconf.navigation.ScreenContent
import org.jetbrains.kotlinconf.navigation.ScheduleScreen
import org.jetbrains.kotlinconf.navigation.TopLevelRoute
import org.jetbrains.kotlinconf.navigation.rememberNavState
import org.jetbrains.kotlinconf.ui.theme.KotlinConfDarkColors
import org.jetbrains.kotlinconf.ui.theme.KotlinConfLightColors
import org.jetbrains.kotlinconf.ui.theme.KotlinConfTheme
import org.jetbrains.kotlinconf.utils.LocalNotificationBar
import org.jetbrains.kotlinconf.utils.LocalWindowSize
import org.jetbrains.kotlinconf.utils.rememberNotificationBarState
import org.jetbrains.kotlinconf.utils.windowSize

@Composable
internal fun SingleScreenApp(
    appGraph: AppGraph,
    route: AppRoute,
    onNavigate: (AppRoute) -> Unit,
    onGoBack: () -> Unit,
    onSet: (AppRoute) -> Unit,
    onActivate: (TopLevelRoute) -> Unit,
) {
    val service = appGraph.conferenceService
    val currentTheme by service.getTheme().collectAsStateWithLifecycle(initialValue = Theme.SYSTEM)
    val isDarkTheme = when (currentTheme) {
        Theme.SYSTEM -> isSystemInDarkTheme()
        Theme.LIGHT -> false
        Theme.DARK -> true
    }

    val colors = if (isDarkTheme) KotlinConfDarkColors else KotlinConfLightColors

    val flags by appGraph.flagsManager.flags.collectAsStateWithLifecycle()

    val navState = rememberNavState(
        startRoute = ScheduleScreen,
        topLevelRoutes = setOf(ScheduleScreen),
        primaryTopLevelRoute = ScheduleScreen,
    )

    val navigator = remember(navState) {
        ExternalNavigator(
            state = navState,
            topLevelBackEnabled = false,
            onAdd = onNavigate,
            onGoBack = onGoBack,
            onSet = onSet,
            onActivate = onActivate,
        )
    }

    CompositionLocalProvider(
        LocalFlags provides flags,
        LocalAppGraph provides appGraph,
        LocalMetroViewModelFactory provides appGraph.metroViewModelFactory,
        LocalWindowSize provides windowSize(),
        LocalMapHandler provides rememberMapHandler(),
        LocalNotificationBar provides rememberNotificationBarState(),
        LocalUseNativeNavigation provides true,
    ) {
        KotlinConfTheme(
            rippleEnabled = LocalFlags.current.rippleEnabled,
            colors = colors,
        ) {
            Box(
                Modifier
                    .fillMaxSize()
                    .background(KotlinConfTheme.colors.mainBackground)
            ) {
                ScreenContent(
                    route = route,
                    navigator = navigator,
                    onBack = onGoBack,
                )
            }
        }
    }
}
