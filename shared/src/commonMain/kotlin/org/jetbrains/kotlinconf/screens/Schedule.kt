package org.jetbrains.kotlinconf.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import kotlinconfapp.shared.generated.resources.Res
import kotlinconfapp.shared.generated.resources.arrow_left_24
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.kotlinconf.Agenda
import org.jetbrains.kotlinconf.ApplicationContext
import org.jetbrains.kotlinconf.ConferenceService
import org.jetbrains.kotlinconf.LocalNavController
import org.jetbrains.kotlinconf.PARTNER_DESCRIPTIONS
import org.jetbrains.kotlinconf.PartnerDetailsScreen
import org.jetbrains.kotlinconf.SpeakerDetailsScreen
import org.jetbrains.kotlinconf.Speakers
import org.jetbrains.kotlinconf.TalkDetailsScreen
import org.jetbrains.kotlinconf.ui.components.StyledText

@Composable
fun Schedule(agenda: Agenda) {
    val navController = LocalNavController.current
    Column {
        Image(painterResource(Res.drawable.arrow_left_24), "back", modifier = Modifier.clickable { navController.popBackStack() })
        StyledText("Schedule")
        for (day in agenda.days) {
            StyledText(stringResource(day.title))
            for (timeSlot in day.timeSlots) {
                StyledText(timeSlot.title)
                for (session in timeSlot.sessions) {
                    StyledText(session.title, modifier = Modifier.clickable { navController.navigate(TalkDetailsScreen(session.id)) })
                }
            }
        }
    }
}