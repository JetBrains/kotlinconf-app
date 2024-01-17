package org.jetbrains.kotlinconf.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import moe.tlaster.precompose.navigation.rememberNavigator
import org.jetbrains.kotlinconf.ConferenceService
import org.jetbrains.kotlinconf.withAppController
import org.jetbrains.kotlinconf.theme.Icons
import org.jetbrains.kotlinconf.ui.components.TabItem
import org.jetbrains.kotlinconf.ui.components.TabsView

@Composable
fun MainScreen(service: ConferenceService) {
    val agenda by service.agenda.collectAsState()
    val speakers by service.speakers.collectAsState()
    val time by service.time.collectAsState()
    val controller = rememberNavigator()

    val favoriteSessions = agenda.days.flatMap { it.timeSlots.flatMap { it.sessions } }
        .filter { it.isFavorite }
        .map {
            val startsIn = ((it.startsAt.timestamp - time.timestamp) / 1000 / 60).toInt()
            when {
                startsIn in 1..15 -> it.copy(timeLine = "IN $startsIn MIN!")
                startsIn <= 0 && !it.isFinished -> it.copy(timeLine = "NOW")
                else -> it
            }
        }

    var showWelcome by remember { mutableStateOf(service.needsOnboarding()) }

    withAppController(service) {
        if (showWelcome) {
            WelcomeScreen(
                onAcceptNotifications = {
                    service.requestNotificationPermissions()
                }, onAcceptPrivacy = {
                    service.acceptPrivacyPolicy()
                }, onClose = {
                    showWelcome = false
                    service.completeOnboarding()
                }, onRejectPrivacy = {}
            )
        } else {
            TabsView(
                controller,
                TabItem("menu", Icons.MENU, Icons.MENU_ACTIVE) {
                    Menu(controller = it)
                },
                TabItem("agenda", Icons.TIME, Icons.TIME_ACTIVE) {
                    AgendaView(agenda, it)
                },
                TabItem("speakers", Icons.SPEAKERS, Icons.SPEAKERS_ACTIVE) {
                    SpeakersView(controller = it, speakers = speakers.all)
                },
                TabItem("Bookmarks", Icons.MY_TALKS, Icons.MY_TALKS_ACTIVE) {
                    Bookmarks(favoriteSessions, it)
                }
            )
        }
    }
}