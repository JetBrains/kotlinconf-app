package org.jetbrains.kotlinconf.screens

import androidx.compose.animation.AnimatedContent
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.saveable.SaveableStateHolder
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.saveable.rememberSaveableStateHolder
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.unit.dp
import androidx.navigationevent.NavigationEventInfo
import androidx.navigationevent.compose.NavigationBackHandler
import androidx.navigationevent.compose.rememberNavigationEventState
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
        var currentIndex by rememberSaveable { mutableIntStateOf(0) }

        val saveableStateHolder: SaveableStateHolder = rememberSaveableStateHolder()

        if (currentIndex > 0 && LocalFlags.current.enableBackOnMainScreens) {
            NavigationBackHandler(
                state = rememberNavigationEventState(NavigationEventInfo.None),
                isBackEnabled = true,
                onBackCompleted = { currentIndex = 0 },
            )
        }

        AnimatedContent(
            targetState = currentIndex,
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            transitionSpec = { NoContentTransition },
        ) { index ->
            saveableStateHolder.SaveableStateProvider(index) {
                MainScreenContent(bottomNavDestinations[index].route, onNavigate)
            }
        }

        AnimatedVisibility(!isKeyboardOpen(), enter = fadeIn(snap()), exit = fadeOut(snap())) {
            BottomNavigation(
                currentIndex = currentIndex,
                onSelect = { selected -> currentIndex = selected }
            )
        }
    }
}

@Composable
private fun MainScreenContent(route: MainRoute, onNavigate: (AppRoute) -> Unit) {
    when (route) {
        ScheduleScreen -> {
            ScheduleScreen(
                onSession = { onNavigate(SessionScreen(it)) },
                onPrivacyNoticeNeeded = { onNavigate(AppPrivacyNoticePrompt) },
                onRequestFeedbackWithComment = { sessionId ->
                    onNavigate(SessionScreen(sessionId, openedForFeedback = true))
                },
            )
        }

        SpeakersScreen -> {
            SpeakersScreen(
                onSpeaker = { onNavigate(SpeakerDetailScreen(it)) }
            )
        }

        MapScreen -> {
            MapScreen()
        }

        InfoScreen -> {
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
    }
}

@Composable
private fun isKeyboardOpen(): Boolean {
    val bottomInset = WindowInsets.ime.getBottom(LocalDensity.current)
    return rememberUpdatedState(bottomInset > 300).value
}

private val bottomNavDestinations: List<MainNavDestination<MainRoute>> = listOf(
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

@Composable
private fun BottomNavigation(
    currentIndex: Int,
    onSelect: (Int) -> Unit,
) {
    Divider(thickness = 1.dp, color = KotlinConfTheme.colors.strokePale)
    MainNavigation(
        currentDestination = bottomNavDestinations[currentIndex],
        destinations = bottomNavDestinations,
        onSelect = { selectedDestination ->
            onSelect(bottomNavDestinations.indexOf(selectedDestination))
        },
    )
}
