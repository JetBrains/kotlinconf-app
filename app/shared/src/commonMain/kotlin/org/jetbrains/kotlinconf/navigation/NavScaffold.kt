package org.jetbrains.kotlinconf.navigation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.AnimationConstants
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkHorizontally
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import org.jetbrains.kotlinconf.generated.resources.Res
import org.jetbrains.kotlinconf.generated.resources.award_28
import org.jetbrains.kotlinconf.generated.resources.award_28_fill
import org.jetbrains.kotlinconf.generated.resources.clock_28
import org.jetbrains.kotlinconf.generated.resources.clock_28_fill
import org.jetbrains.kotlinconf.generated.resources.info_28
import org.jetbrains.kotlinconf.generated.resources.info_28_fill
import org.jetbrains.kotlinconf.generated.resources.location_28
import org.jetbrains.kotlinconf.generated.resources.location_28_fill
import org.jetbrains.kotlinconf.generated.resources.nav_destination_golden_kodee
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
import org.jetbrains.kotlinconf.ui.components.Text
import org.jetbrains.kotlinconf.ui.components.VerticalDivider
import org.jetbrains.kotlinconf.ui.theme.KotlinConfTheme
import org.jetbrains.kotlinconf.utils.LocalNotificationBar
import org.jetbrains.kotlinconf.utils.LocalWindowSize
import org.jetbrains.kotlinconf.utils.WindowSize
import org.jetbrains.kotlinconf.utils.bottomInsetPadding
import org.jetbrains.kotlinconf.utils.topInsetPadding

private val bottomNavDestinations: List<MainNavDestination<TopLevelRoute>> = listOf(
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
        label = Res.string.nav_destination_golden_kodee,
        icon = Res.drawable.award_28,
        iconSelected = Res.drawable.award_28_fill,
        route = GoldenKodeeScreen,
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

@Composable
internal fun NavScaffold(
    navState: NavState,
    navigator: Navigator,
    showGoldenKodee: Boolean,
    content: @Composable (() -> Unit)
) {
    val onSelectRoute: (TopLevelRoute) -> Unit = { route ->
        navigator.activate(route)
    }

    val destinations = remember(showGoldenKodee) {
        if (showGoldenKodee) {
            bottomNavDestinations
        } else {
            bottomNavDestinations.filter { it.route !is GoldenKodeeScreen }
        }
    }

    val windowSize = LocalWindowSize.current

    val showLargeNavigation = windowSize != WindowSize.Compact &&
            navState.topLevelRoute != null
    val showCompactNavigation = windowSize == WindowSize.Compact &&
            navState.topLevelRoute != null &&
            navState.currentBackstack.size == 1 &&
            !isKeyboardOpen()

    Row(
        Modifier.fillMaxSize()
            .background(color = KotlinConfTheme.colors.mainBackground)
    ) {
        val enterAnimSpec = tween<IntSize>(delayMillis = AnimationConstants.DefaultDurationMillis)

        AnimatedVisibility(
            visible = showLargeNavigation,
            enter = expandHorizontally(enterAnimSpec),
            exit = shrinkHorizontally() + fadeOut(),
        ) {
            SideNavigation(
                currentRoute = navState.topLevelRoute,
                destinations = destinations,
                onSelectRoute = onSelectRoute,
                expanded = windowSize == WindowSize.Large,
            )
        }

        Column(Modifier.weight(1f)) {
            Box(Modifier.weight(1f).clipToBounds()) {
                content()
            }

            NotificationBar()

            AnimatedVisibility(
                visible = showCompactNavigation,
                enter = expandVertically(enterAnimSpec),
                exit = shrinkVertically() + fadeOut(),
            ) {
                BottomNavigation(
                    currentRoute = navState.topLevelRoute,
                    destinations = destinations,
                    onSelectRoute = onSelectRoute,
                )
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
    currentRoute: TopLevelRoute?,
    destinations: List<MainNavDestination<TopLevelRoute>>,
    onSelectRoute: (TopLevelRoute) -> Unit,
) {
    val currentDestination = destinations.find { it.route == currentRoute }

    Column(
        Modifier.padding(bottomInsetPadding()),
    ) {
        HorizontalDivider(thickness = 1.dp, color = KotlinConfTheme.colors.strokePale)
        MainNavigationBar(
            currentDestination = currentDestination,
            destinations = destinations,
            onSelect = { selectedDestination ->
                onSelectRoute(selectedDestination.route)
            },
        )
    }
}

@Composable
private fun SideNavigation(
    currentRoute: TopLevelRoute?,
    destinations: List<MainNavDestination<TopLevelRoute>>,
    onSelectRoute: (TopLevelRoute) -> Unit,
    expanded: Boolean,
) {
    val currentDestination = destinations.find { it.route == currentRoute }

    Row {
        MainNavigationRail(
            currentDestination = currentDestination,
            destinations = destinations,
            onSelect = { selectedDestination ->
                onSelectRoute(selectedDestination.route)
            },
            expanded = expanded,
            modifier = Modifier.padding(topInsetPadding()),
        )
        VerticalDivider(thickness = 1.dp, color = KotlinConfTheme.colors.strokePale)
    }
}

@Composable
private fun NotificationBar() {
    val message = LocalNotificationBar.current.message
    AnimatedVisibility(
        visible = message != null,
        modifier = Modifier.fillMaxWidth(),
    ) {
        Text(
            text = message ?: "",
            color = KotlinConfTheme.colors.primaryTextInverted,
            style = KotlinConfTheme.typography.text2.copy(textAlign = TextAlign.Center),
            modifier = Modifier
                .fillMaxWidth()
                .background(color = KotlinConfTheme.colors.mainBackgroundInverted)
                .padding(vertical = 16.dp, horizontal = 10.dp)
        )
    }
}
