package org.jetbrains.kotlinconf.android.ui

import androidx.activity.*
import androidx.compose.runtime.*
import androidx.navigation.compose.*
import com.jetbrains.kotlinconf.R
import org.jetbrains.kotlinconf.*
import org.jetbrains.kotlinconf.android.*
import org.jetbrains.kotlinconf.android.ui.components.*

@Composable
fun MainScreen(service: ConferenceService, onBackPressed: (OnBackPressedCallback) -> Unit) {
    val agenda by service.agenda.collectAsState()
    val speakers by service.speakers.collectAsState()
    val time by service.time.collectAsState()
    val controller = rememberNavController()

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
        onBackPressed(object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                it.back()
            }
        })

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
                TabItem("menu", R.drawable.menu, R.drawable.menu_active) {
                    Menu(controller = it)
                },
                TabItem("agenda", R.drawable.time, R.drawable.time_active) {
                    AgendaView(agenda, it)
                },
                TabItem("speakers", R.drawable.speakers, R.drawable.speakers_active) {
                    Speakers(controller = it, speakers = speakers.all)
                },
                TabItem("Bookmarks", R.drawable.mytalks, R.drawable.mytalks_active) {
                    Bookmarks(favoriteSessions, it)
                },
                TabItem("Map", R.drawable.location, R.drawable.location_active) {
                    Map()
                }
            )
        }
    }
}