package org.jetbrains.kotlinconf.ui

import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.navigation.compose.rememberNavController
import kotlinconfapp.shared.generated.resources.Res
import kotlinconfapp.shared.generated.resources.location
import kotlinconfapp.shared.generated.resources.location_active
import kotlinconfapp.shared.generated.resources.menu
import kotlinconfapp.shared.generated.resources.menu_active
import kotlinconfapp.shared.generated.resources.mytalks
import kotlinconfapp.shared.generated.resources.mytalks_active
import kotlinconfapp.shared.generated.resources.speakers
import kotlinconfapp.shared.generated.resources.speakers_active
import kotlinconfapp.shared.generated.resources.time
import kotlinconfapp.shared.generated.resources.time_active
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.kotlinconf.AppController
import org.jetbrains.kotlinconf.ConferenceService
import org.jetbrains.kotlinconf.ui.components.TabItem
import org.jetbrains.kotlinconf.ui.components.TabsView
import org.jetbrains.kotlinconf.ui.welcome.WelcomeScreen

@OptIn(ExperimentalResourceApi::class)
@Composable
fun MainScreen(service: ConferenceService) {
    val agenda by service.agenda.collectAsState()
    val speakers by service.speakers.collectAsState()
    val time by service.time.collectAsState()
    val navigator = rememberNavController()
    val controller = remember { AppController(service) }
    var showWelcome by remember { mutableStateOf(service.needsOnboarding()) }

    if (showWelcome) {
        WelcomeScreen(onAcceptNotifications = {
            service.requestNotificationPermissions()
        }, onAcceptPrivacy = {
            service.acceptPrivacyPolicy()
        }, onClose = {
            showWelcome = false
            service.completeOnboarding()
        }, onRejectPrivacy = {})
        return
    }

    val agendaScrollState = rememberLazyListState()
    val speakersScrollState = rememberLazyListState()

    TabsView(
        controller,
        navigator,
        TabItem("menu", Res.drawable.menu, Res.drawable.menu_active) {
            MenuScreen(controller)
        },
        TabItem("agenda", Res.drawable.time, Res.drawable.time_active) {
            AgendaScreen(agenda, agendaScrollState, controller)
        },
        TabItem(
            "speakers", Res.drawable.speakers, Res.drawable.speakers_active
        ) {
            SpeakersScreen(speakers.all, speakersScrollState, controller)
        },
        TabItem(
            "bookmarks", Res.drawable.mytalks, Res.drawable.mytalks_active
        ) {
            val favoriteSessions = agenda.days.flatMap { it.timeSlots.flatMap { it.sessions } }
                .filter { it.isFavorite }.map {
                    val startsIn = ((it.startsAt.timestamp - time.timestamp) / 1000 / 60).toInt()
                    when {
                        startsIn in 1..15 -> it.copy(timeLine = "In $startsIn min!")
                        startsIn <= 0 && !it.isFinished -> it.copy(timeLine = "NOW")
                        else -> it
                    }
                }

            BookmarksScreen(favoriteSessions, controller)
        },
        TabItem(
            "location", Res.drawable.location, Res.drawable.location_active
        ) {
            LocationScreen()
        },
    )
}