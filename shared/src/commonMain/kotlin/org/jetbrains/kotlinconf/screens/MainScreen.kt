package org.jetbrains.kotlinconf.screens

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.backhandler.BackHandler
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.unit.dp
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.ui.NavDisplay
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
import org.jetbrains.kotlinconf.LocalFlags
import org.jetbrains.kotlinconf.URLs
import org.jetbrains.kotlinconf.navigation.AboutAppScreen
import org.jetbrains.kotlinconf.navigation.AboutConferenceScreen
import org.jetbrains.kotlinconf.navigation.AppPrivacyNoticePrompt
import org.jetbrains.kotlinconf.navigation.CodeOfConductScreen
import org.jetbrains.kotlinconf.navigation.InfoScreen
import org.jetbrains.kotlinconf.navigation.MapScreen
import org.jetbrains.kotlinconf.navigation.NewsListScreen
import org.jetbrains.kotlinconf.navigation.PartnersScreen
import org.jetbrains.kotlinconf.navigation.ScheduleScreen
import org.jetbrains.kotlinconf.navigation.SessionScreen
import org.jetbrains.kotlinconf.navigation.SettingsScreen
import org.jetbrains.kotlinconf.navigation.SpeakerDetailScreen
import org.jetbrains.kotlinconf.navigation.SpeakersScreen
import org.jetbrains.kotlinconf.ui.components.Divider
import org.jetbrains.kotlinconf.ui.components.MainNavDestination
import org.jetbrains.kotlinconf.ui.components.MainNavigation
import org.jetbrains.kotlinconf.ui.theme.KotlinConfTheme
import org.koin.compose.koinInject

@Composable
fun MainScreen(
    onNavigate: (Any) -> Unit,
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
        val localBackStack = remember { mutableStateListOf<Any>(ScheduleScreen) }
        NavDisplay(
            backStack = localBackStack,
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            entryProvider = entryProvider {
                entry<InfoScreen> {
                    MainBackHandler()
                    val uriHandler = LocalUriHandler.current
                    InfoScreen(
                        onAboutConf = { onNavigate(AboutConferenceScreen) },
                        onAboutApp = { onNavigate(AboutAppScreen) },
                        onNewsFeed = { onNavigate(NewsListScreen) },
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
        // Prevent back navigation with an empty handler
        @OptIn(ExperimentalComposeUiApi::class)
        BackHandler(true) { }
    }
}

@Composable
private fun isKeyboardOpen(): Boolean {
    val bottomInset = WindowInsets.ime.getBottom(LocalDensity.current)
    return rememberUpdatedState(bottomInset > 300).value
}

@Composable
private fun BottomNavigation(localBackStack: SnapshotStateList<Any>) {
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

    val currentDestination = localBackStack.last()
    val currentBottomNavDestination = bottomNavDestinations.find { dest ->
        currentDestination == dest.route
    }

    Divider(thickness = 1.dp, color = KotlinConfTheme.colors.strokePale)
    MainNavigation(
        currentDestination = currentBottomNavDestination,
        destinations = bottomNavDestinations,
        onSelect = {
            localBackStack.add(it.route)
            //{
//                // Avoid stacking multiple copies of the main screens
//                popUpTo(localBackStack.graph.findStartDestination().route!!) {
//                    saveState = true
//                }
//                launchSingleTop = true
//                restoreState = true
//            }
        },
    )
}
