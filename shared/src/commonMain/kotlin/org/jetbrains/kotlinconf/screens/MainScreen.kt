package org.jetbrains.kotlinconf.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import kotlinconfapp.shared.generated.resources.Res
import kotlinconfapp.shared.generated.resources.clock_28
import kotlinconfapp.shared.generated.resources.clock_28_fill
import kotlinconfapp.shared.generated.resources.info_28
import kotlinconfapp.shared.generated.resources.info_28_fill
import kotlinconfapp.shared.generated.resources.location_28
import kotlinconfapp.shared.generated.resources.location_28_fill
import kotlinconfapp.shared.generated.resources.nav_destination_info
import kotlinconfapp.shared.generated.resources.nav_destination_map
import kotlinconfapp.shared.generated.resources.nav_destination_schedule
import kotlinconfapp.shared.generated.resources.nav_destination_speakers
import kotlinconfapp.shared.generated.resources.team_28
import kotlinconfapp.shared.generated.resources.team_28_fill
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.kotlinconf.ConferenceService
import org.jetbrains.kotlinconf.URLs
import org.jetbrains.kotlinconf.navigation.AboutAppScreen
import org.jetbrains.kotlinconf.navigation.AboutConferenceScreen
import org.jetbrains.kotlinconf.navigation.CodeOfConductScreen
import org.jetbrains.kotlinconf.navigation.InfoScreen
import org.jetbrains.kotlinconf.navigation.MapScreen
import org.jetbrains.kotlinconf.navigation.NewsListScreen
import org.jetbrains.kotlinconf.navigation.PartnersScreen
import org.jetbrains.kotlinconf.navigation.PrivacyPolicyScreen
import org.jetbrains.kotlinconf.navigation.ScheduleScreen
import org.jetbrains.kotlinconf.navigation.SessionScreen
import org.jetbrains.kotlinconf.navigation.SpeakerDetailScreen
import org.jetbrains.kotlinconf.navigation.SpeakersScreen
import org.jetbrains.kotlinconf.ui.components.Divider
import org.jetbrains.kotlinconf.ui.components.MainNavDestination
import org.jetbrains.kotlinconf.ui.components.MainNavigation
import org.jetbrains.kotlinconf.ui.theme.KotlinConfTheme
import org.koin.compose.koinInject

@Composable
fun MainScreen(
    rootNavController: NavController,
    service: ConferenceService = koinInject(),
) {
    LaunchedEffect(Unit) {
        service.completeOnboarding()
    }

    Column(
        Modifier
            .fillMaxSize()
            .windowInsetsPadding(WindowInsets.safeDrawing)
    ) {
        val nestedNavController = rememberNavController()
        NavHost(
            nestedNavController,
            startDestination = ScheduleScreen,
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
        ) {
            composable<InfoScreen> {
                val uriHandler = LocalUriHandler.current
                InfoScreen(
                    onAboutConf = { rootNavController.navigate(AboutConferenceScreen) },
                    onAboutApp = { rootNavController.navigate(AboutAppScreen) },
                    onNewsFeed = { rootNavController.navigate(NewsListScreen) },
                    onOurPartners = { rootNavController.navigate(PartnersScreen) },
                    onCodeOfConduct = { rootNavController.navigate(CodeOfConductScreen) },
                    onTwitter = { uriHandler.openUri(URLs.TWITTER) },
                    onSlack = { uriHandler.openUri(URLs.SLACK) },
                    onBluesky = { uriHandler.openUri(URLs.BLUESKY) },
                )
            }
            composable<SpeakersScreen> {
                SpeakersScreen(
                    onSpeaker = { rootNavController.navigate(SpeakerDetailScreen(it)) }
                )
            }
            composable<ScheduleScreen> {
                ScheduleScreen(
                    onSession = { rootNavController.navigate(SessionScreen(it)) },
                    onPrivacyPolicyNeeded = { rootNavController.navigate(PrivacyPolicyScreen) },
                )
            }
            composable<MapScreen> {
                MapScreen()
            }
        }

        BottomNavigation(nestedNavController)
    }
}

@Composable
private fun BottomNavigation(nestedNavController: NavHostController) {
    val bottomNavDestinations: List<MainNavDestination> =
        listOf(
            MainNavDestination(
                label = stringResource(Res.string.nav_destination_schedule),
                icon = Res.drawable.clock_28,
                iconSelected = Res.drawable.clock_28_fill,
                route = ScheduleScreen,
                routeClass = ScheduleScreen::class
            ),
            MainNavDestination(
                label = stringResource(Res.string.nav_destination_speakers),
                icon = Res.drawable.team_28,
                iconSelected = Res.drawable.team_28_fill,
                route = SpeakersScreen,
                routeClass = SpeakersScreen::class
            ),
            MainNavDestination(
                label = stringResource(Res.string.nav_destination_map),
                icon = Res.drawable.location_28,
                iconSelected = Res.drawable.location_28_fill,
                route = MapScreen,
                routeClass = MapScreen::class
            ),
            MainNavDestination(
                label = stringResource(Res.string.nav_destination_info),
                icon = Res.drawable.info_28,
                iconSelected = Res.drawable.info_28_fill,
                route = InfoScreen,
                routeClass = InfoScreen::class
            ),
        )

    val currentDestination = nestedNavController.currentBackStackEntryAsState().value?.destination
    val currentBottomNavDestination = currentDestination?.let {
        bottomNavDestinations.find { dest ->
            val routeClass = dest.routeClass
            routeClass != null && currentDestination.hasRoute(routeClass)
        }
    }

    Divider(thickness = 1.dp, color = KotlinConfTheme.colors.strokePale)
    MainNavigation(
        currentDestination = currentBottomNavDestination,
        destinations = bottomNavDestinations,
        onSelect = {
            nestedNavController.navigate(it.route) {
                // Avoid stacking multiple copies of the main screens
                popUpTo(nestedNavController.graph.findStartDestination().route!!) {
                    saveState = true
                }
                launchSingleTop = true
                restoreState = true
            }
        },
    )
}
