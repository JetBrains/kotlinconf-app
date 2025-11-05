package org.jetbrains.kotlinconf.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ContentTransform
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.snap
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import androidx.navigationevent.NavigationEventInfo
import androidx.navigationevent.compose.NavigationBackHandler
import androidx.navigationevent.compose.rememberNavigationEventState
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
import androidx.navigation.NavController
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.kotlinconf.ConferenceService
import org.jetbrains.kotlinconf.LocalFlags
import org.jetbrains.kotlinconf.URLs
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
import org.jetbrains.kotlinconf.navigation.AboutAppScreen
import org.jetbrains.kotlinconf.navigation.AboutConferenceScreen
import org.jetbrains.kotlinconf.navigation.AppPrivacyNoticePrompt
import org.jetbrains.kotlinconf.navigation.AppRoute
import org.jetbrains.kotlinconf.navigation.CodeOfConductScreen
import org.jetbrains.kotlinconf.navigation.InfoScreen
import org.jetbrains.kotlinconf.navigation.MainRoute
import org.jetbrains.kotlinconf.navigation.MapScreen
import org.jetbrains.kotlinconf.navigation.PartnersScreen
import org.jetbrains.kotlinconf.navigation.ScheduleScreen
import org.jetbrains.kotlinconf.navigation.SessionScreen
import org.jetbrains.kotlinconf.navigation.SettingsScreen
import org.jetbrains.kotlinconf.navigation.SpeakerDetailScreen
import org.jetbrains.kotlinconf.navigation.SpeakersScreen
import org.jetbrains.kotlinconf.navigation.rememberNavBackStack
import org.jetbrains.kotlinconf.ui.components.Divider
import org.jetbrains.kotlinconf.ui.components.MainNavDestination
import org.jetbrains.kotlinconf.ui.components.MainNavigation
import org.jetbrains.kotlinconf.ui.theme.KotlinConfTheme
import org.koin.compose.koinInject

private val NoContentTransition = ContentTransform(EnterTransition.None, ExitTransition.None)

@Composable
fun MainScreen(
    onNavigate: (AppRoute) -> Unit,
    service: ConferenceService = koinInject(),
) {
    LaunchedEffect(Unit) {
        service.completeOnboarding()
    }

    Column(
        Modifier
            .fillMaxSize()
            .background(color = KotlinConfTheme.colors.mainBackground)
            .windowInsetsPadding(WindowInsets.safeDrawing)
    ) {
        val localBackStack = rememberNavBackStack<MainRoute>(ScheduleScreen)

        NavDisplay(
            backStack = localBackStack,
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            transitionSpec = { NoContentTransition },
            popTransitionSpec = { NoContentTransition },
            predictivePopTransitionSpec = { NoContentTransition },
            entryDecorators = listOf(
                rememberSaveableStateHolderNavEntryDecorator(),
                rememberViewModelStoreNavEntryDecorator(),
            ),
            entryProvider = entryProvider {
                entry<InfoScreen> {
                    MainBackHandler()
                    val uriHandler = LocalUriHandler.current
                    InfoScreen(
                        onAboutConf = { onNavigate(AboutConferenceScreen) },
                        onAboutApp = { onNavigate(AboutAppScreen) },
                        onOurPartners = { onNavigate(PartnersScreen) },
                        onCodeOfConduct = { onNavigate(CodeOfConductScreen) },
                        onTwitter = { uriHandler.openUri(URLs.TWITTER) },
                        onSlack = { uriHandler.openUri(URLs.SLACK) },
                        onBluesky = { uriHandler.openUri(URLs.BLUESKY) },
                        onSettings = { onNavigate(SettingsScreen) },
                    )
                }
                entry<SpeakersScreen> {
                    MainBackHandler()
                    SpeakersScreen(
                        onSpeaker = { onNavigate(SpeakerDetailScreen(it)) }
                    )
                }
                entry<ScheduleScreen> {
                    MainBackHandler()
                    ScheduleScreen(
                        onSession = { onNavigate(SessionScreen(it)) },
                        onPrivacyNoticeNeeded = { onNavigate(AppPrivacyNoticePrompt) },
                        onRequestFeedbackWithComment = { sessionId ->
                            onNavigate(SessionScreen(sessionId, openedForFeedback = true))
                        },
                    )
                }
                entry<MapScreen> {
                    MainBackHandler()
                    MapScreen()
                }
            }
        )

        AnimatedVisibility(!isKeyboardOpen(), enter = fadeIn(snap()), exit = fadeOut(snap())) {
            BottomNavigation(localBackStack)
        }
    }
}

@Composable
private fun MainBackHandler() {
    if (!LocalFlags.current.enableBackOnMainScreens) {
        // Prevent back navigation
        NavigationBackHandler(
            state = rememberNavigationEventState(NavigationEventInfo.None),
            isBackEnabled = true,
            onBackCompleted = { /* Do nothing */ },
        )
    }
}

@Composable
private fun isKeyboardOpen(): Boolean {
    val bottomInset = WindowInsets.ime.getBottom(LocalDensity.current)
    return rememberUpdatedState(bottomInset > 300).value
}

@Composable
private fun BottomNavigation(localBackStack: MutableList<MainRoute>) {
    val bottomNavDestinations: List<MainNavDestination<MainRoute>> =
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

    // TODO check if we can simplify this
    val currentDestination = localBackStack.last()
    val currentBottomNavDestination = bottomNavDestinations.find { dest ->
        currentDestination == dest.route
    }

    Divider(thickness = 1.dp, color = KotlinConfTheme.colors.strokePale)
    MainNavigation(
        currentDestination = currentBottomNavDestination,
        destinations = bottomNavDestinations,
        onSelect = {
            localBackStack.apply {
                val target = it.route
                if (last() == target) {
                    return@apply
                }

                add(target)

                if (size > 2) {
                    // Remove everything but the first and last entry
                    subList(1, lastIndex).clear()
                }
            }
        },
    )
}
