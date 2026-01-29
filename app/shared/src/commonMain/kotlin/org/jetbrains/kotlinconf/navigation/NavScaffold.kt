package org.jetbrains.kotlinconf.navigation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.AnimationConstants
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkHorizontally
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntSize
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
import org.jetbrains.kotlinconf.ui.components.HorizontalDivider
import org.jetbrains.kotlinconf.ui.components.MainNavDestination
import org.jetbrains.kotlinconf.ui.components.MainNavigationBar
import org.jetbrains.kotlinconf.ui.components.MainNavigationRail
import org.jetbrains.kotlinconf.ui.components.VerticalDivider
import org.jetbrains.kotlinconf.ui.theme.KotlinConfTheme
import org.jetbrains.kotlinconf.utils.LocalWindowSize
import org.jetbrains.kotlinconf.utils.WindowSize

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
internal fun NavScaffold(
    backstack: MutableList<AppRoute>,
    content: @Composable () -> Unit
) {
    val onSelectRoute: (AppRoute) -> Unit = { route ->
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

    Box(
        Modifier
            .fillMaxSize()
            .background(color = KotlinConfTheme.colors.mainBackground)
            .windowInsetsPadding(WindowInsets.safeDrawing)
    ) {
        val windowSize = LocalWindowSize.current
        val currentRoute = backstack.lastOrNull()
        val showNavigation = currentRoute in mainRoutes

        Row(
            Modifier
                .fillMaxSize()
        ) {
            val enterAnimSpec = tween<IntSize>(delayMillis = AnimationConstants.DefaultDurationMillis)

            AnimatedVisibility(
                visible = showNavigation && windowSize != WindowSize.Compact,
                enter = expandHorizontally(enterAnimSpec),
                exit = shrinkHorizontally(),
            ) {
                SideNavigation(
                    currentRoute = currentRoute,
                    onSelectRoute = onSelectRoute,
                    expanded = windowSize == WindowSize.Large,
                )
            }

            Column(Modifier.weight(1f)) {
                Box(Modifier.weight(1f)) {
                    content()
                }

                AnimatedVisibility(
                    visible = showNavigation && windowSize == WindowSize.Compact && !isKeyboardOpen(),
                    enter = expandVertically(enterAnimSpec),
                    exit = shrinkVertically(),
                ) {
                    BottomNavigation(
                        currentRoute = currentRoute,
                        onSelectRoute = onSelectRoute,
                    )
                }
            }
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

    Column {
        HorizontalDivider(thickness = 1.dp, color = KotlinConfTheme.colors.strokePale)
        MainNavigationBar(
            currentDestination = currentDestination,
            destinations = bottomNavDestinations,
            onSelect = { selectedDestination ->
                onSelectRoute(selectedDestination.route)
            },
        )
    }
}

@Composable
private fun SideNavigation(
    currentRoute: AppRoute?,
    onSelectRoute: (AppRoute) -> Unit,
    expanded: Boolean,
) {
    val currentDestination = bottomNavDestinations.find { it.route == currentRoute }

    Row {
        MainNavigationRail(
            currentDestination = currentDestination,
            destinations = bottomNavDestinations,
            onSelect = { selectedDestination ->
                onSelectRoute(selectedDestination.route)
            },
            expanded = expanded,
        )
        VerticalDivider(thickness = 1.dp, color = KotlinConfTheme.colors.strokePale)
    }
}
