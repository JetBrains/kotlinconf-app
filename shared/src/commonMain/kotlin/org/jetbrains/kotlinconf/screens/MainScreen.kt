package org.jetbrains.kotlinconf.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavDestination.Companion.hasRoute
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
import org.jetbrains.kotlinconf.navigation.PartnersScreen
import org.jetbrains.kotlinconf.navigation.ScheduleScreen
import org.jetbrains.kotlinconf.navigation.SpeakerDetailsScreen
import org.jetbrains.kotlinconf.navigation.SpeakersScreen
import org.jetbrains.kotlinconf.navigation.TalkDetailsScreen
import org.jetbrains.kotlinconf.ui.components.Divider
import org.jetbrains.kotlinconf.ui.components.MainNavDestination
import org.jetbrains.kotlinconf.ui.components.MainNavigation
import org.jetbrains.kotlinconf.ui.theme.KotlinConfTheme

@Composable
fun MainScreen(
    service: ConferenceService,
    rootNavController: NavController,
) {
    Column(Modifier.fillMaxSize()) {
        val nestedNavController = rememberNavController()
        NavHost(
            nestedNavController,
            startDestination = InfoScreen,
            modifier = Modifier.fillMaxWidth().weight(1f),
        ) {
            composable<InfoScreen> {
                // TODO add action handlers
                val uriHandler = LocalUriHandler.current
                InfoScreen(
                    onAboutConf = { rootNavController.navigate(AboutConferenceScreen) },
                    onAboutApp = { rootNavController.navigate(AboutAppScreen) },
                    onOurPartners = { rootNavController.navigate(PartnersScreen) },
                    onCodeOfConduct = { rootNavController.navigate(CodeOfConductScreen) },
                    onTwitter = { uriHandler.openUri(URLs.TWITTER) },
                    onSlack = { uriHandler.openUri(URLs.SLACK) },
                    onBluesky = { uriHandler.openUri(URLs.BLUESKY) },
                )
            }
            composable<SpeakersScreen> {
                Speakers(
                    service.speakers.collectAsState().value,
                    onSpeaker = { rootNavController.navigate(SpeakerDetailsScreen(it)) },
                    onBack = rootNavController::popBackStack
                )
            }
            composable<ScheduleScreen> {
                Schedule(
                    service.agenda.collectAsState().value,
                    onBack = { rootNavController.popBackStack() },
                    onSession = { rootNavController.navigate(TalkDetailsScreen(it)) },
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
                label = stringResource(Res.string.nav_destination_info),
                icon = Res.drawable.info_28,
                iconSelected = Res.drawable.info_28_fill,
                route = InfoScreen,
                routeClass = InfoScreen::class
            ),
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
                launchSingleTop = true
            }
        },
    )
}
