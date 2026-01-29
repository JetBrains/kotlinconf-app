package org.jetbrains.kotlinconf.navigation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.snap
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import org.jetbrains.kotlinconf.generated.resources.Res
import org.jetbrains.kotlinconf.generated.resources.clock_28
import org.jetbrains.kotlinconf.generated.resources.clock_28_fill
import org.jetbrains.kotlinconf.generated.resources.info_28
import org.jetbrains.kotlinconf.generated.resources.info_28_fill
import org.jetbrains.kotlinconf.generated.resources.location_28
import org.jetbrains.kotlinconf.generated.resources.location_28_fill
import org.jetbrains.kotlinconf.generated.resources.nav_destination_info
import org.jetbrains.kotlinconf.generated.resources.nav_destination_map
import org.jetbrains.kotlinconf.generated.resources.nav_destination_schedule
import org.jetbrains.kotlinconf.generated.resources.nav_destination_speakers
import org.jetbrains.kotlinconf.generated.resources.team_28
import org.jetbrains.kotlinconf.generated.resources.team_28_fill
import org.jetbrains.kotlinconf.ui.components.Divider
import org.jetbrains.kotlinconf.ui.components.MainNavDestination
import org.jetbrains.kotlinconf.ui.components.MainNavigation
import org.jetbrains.kotlinconf.ui.theme.KotlinConfTheme

private val bottomNavDestinations: List<MainNavDestination<AppRoute>> = listOf(
    MainNavDestination(
        label = Res.string.nav_destination_schedule,
        icon = Res.drawable.clock_28,
        iconSelected = Res.drawable.clock_28_fill,
        route = ScheduleScreen,
    ),
    MainNavDestination(
        label = Res.string.nav_destination_speakers,
        icon = Res.drawable.team_28,
        iconSelected = Res.drawable.team_28_fill,
        route = SpeakersScreen,
    ),
    MainNavDestination(
        label = Res.string.nav_destination_map,
        icon = Res.drawable.location_28,
        iconSelected = Res.drawable.location_28_fill,
        route = MapScreen,
    ),
    MainNavDestination(
        label = Res.string.nav_destination_info,
        icon = Res.drawable.info_28,
        iconSelected = Res.drawable.info_28_fill,
        route = InfoScreen,
    ),
)

private val mainRoutes = bottomNavDestinations.map { it.route }

@Composable
internal fun NavigationShell(
    backstack: MutableList<AppRoute>,
    content: @Composable () -> Unit
) {
    val currentRoute = backstack.lastOrNull()

    Column(
        Modifier
            .fillMaxSize()
            .background(color = KotlinConfTheme.colors.mainBackground)
            .windowInsetsPadding(WindowInsets.safeDrawing)
    ) {
        Box(Modifier.weight(1f)) {
            content()
        }

        AnimatedVisibility(
            visible = currentRoute in mainRoutes && !isKeyboardOpen(),
            enter = fadeIn(snap()),
            exit = fadeOut(snap())
        ) {
            BottomNavigation(
                currentRoute = currentRoute,
                onSelectRoute = { route ->
                    if (route is ScheduleScreen) {
                        // Going to Schedule: clear to just Schedule
                        while (backstack.size > 1) {
                            backstack.removeAt(backstack.size - 1)
                        }
                        backstack[0] = ScheduleScreen
                    } else {
                        // Going to other main tab: Schedule at base, selected tab on top
                        // Other main tabs don't stack on each other
                        backstack.clear()
                        backstack.add(ScheduleScreen)
                        backstack.add(route)
                    }
                }
            )
        }
    }
}

@Composable
private fun isKeyboardOpen(): Boolean {
    val bottomInset = WindowInsets.ime.getBottom(LocalDensity.current)
    return rememberUpdatedState(bottomInset > 300).value
}

@Composable
private fun BottomNavigation(
    currentRoute: AppRoute?,
    onSelectRoute: (AppRoute) -> Unit,
) {
    val currentDestination = bottomNavDestinations.find { it.route == currentRoute }

    Divider(thickness = 1.dp, color = KotlinConfTheme.colors.strokePale)
    MainNavigation(
        currentDestination = currentDestination,
        destinations = bottomNavDestinations,
        onSelect = { selectedDestination ->
            onSelectRoute(selectedDestination.route)
        },
    )
}
