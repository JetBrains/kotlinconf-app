package org.jetbrains.kotlinconf.navigation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.AnimationConstants
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.navigation3.scene.Scene
import androidx.navigation3.scene.SceneDecoratorStrategy
import androidx.navigation3.scene.SceneDecoratorStrategyScope
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
import org.jetbrains.kotlinconf.utils.WindowSize
import org.jetbrains.kotlinconf.utils.bottomInsetPadding
import org.jetbrains.kotlinconf.utils.topInsetPadding

private val topLevelNavDestinations: List<MainNavDestination<TopLevelRoute>> = listOf(
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

internal class TopLevelNavStrategy(
    private val navState: NavState,
    private val windowSize: WindowSize,
    private val showGoldenKodee: Boolean,
    private val onSelectRoute: (TopLevelRoute) -> Unit,
) : SceneDecoratorStrategy<AppRoute> {
    override fun SceneDecoratorStrategyScope<AppRoute>.decorateScene(scene: Scene<AppRoute>): Scene<AppRoute> {
        if (navState.topLevelRoute == null) {
            return scene
        }

        val destinations = if (showGoldenKodee) {
            topLevelNavDestinations
        } else {
            topLevelNavDestinations.filter { it.route !is GoldenKodeeScreen }
        }

        return when (windowSize) {
            WindowSize.Compact -> {
                if (navState.currentBackstack.size == 1) {
                    BottomNavScene(scene, navState, onSelectRoute, destinations)
                } else {
                    scene
                }
            }

            WindowSize.Medium, WindowSize.Large -> {
                NavRailScene(scene, navState, onSelectRoute, destinations, windowSize)
            }
        }
    }
}

private class BottomNavScene(
    private val scene: Scene<AppRoute>,
    private val navState: NavState,
    private val onSelectRoute: (TopLevelRoute) -> Unit,
    private val destinations: List<MainNavDestination<TopLevelRoute>>,
) : Scene<AppRoute> by scene {
    override val content: @Composable () -> Unit = {
        Column(Modifier.padding(bottomInsetPadding())) {
            Box(Modifier.weight(1f)) {
                scene.content()
            }

            val enterAnimSpec =
                tween<IntSize>(delayMillis = AnimationConstants.DefaultDurationMillis)

            val bottomInset = WindowInsets.ime.getBottom(LocalDensity.current)
            val isKeyboardOpen by rememberUpdatedState(bottomInset > 300)

            AnimatedVisibility(
                visible = !isKeyboardOpen,
                enter = expandVertically(enterAnimSpec),
                exit = shrinkVertically() + fadeOut(),
            ) {
                HorizontalDivider(thickness = 1.dp, color = KotlinConfTheme.colors.strokePale)

                MainNavigationBar(
                    currentDestination = destinations.find { it.route == navState.topLevelRoute },
                    destinations = destinations,
                    onSelect = { selectedDestination ->
                        onSelectRoute(selectedDestination.route)
                    },
                )
            }
        }
    }
}

private class NavRailScene(
    private val scene: Scene<AppRoute>,
    private val navState: NavState,
    private val onSelectRoute: (TopLevelRoute) -> Unit,
    private val destinations: List<MainNavDestination<TopLevelRoute>>,
    private val windowSize: WindowSize,
) : Scene<AppRoute> by scene {
    override val content: @Composable () -> Unit = {
        Row {
            MainNavigationRail(
                currentDestination = destinations.find { it.route == navState.topLevelRoute },
                destinations = destinations,
                onSelect = { selectedDestination ->
                    onSelectRoute(selectedDestination.route)
                },
                expanded = windowSize == WindowSize.Large,
                modifier = Modifier.padding(topInsetPadding()),
            )

            VerticalDivider(thickness = 1.dp, color = KotlinConfTheme.colors.strokePale)

            scene.content()
        }
    }
}
