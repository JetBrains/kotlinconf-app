package org.jetbrains.kotlinconf.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import kotlinconfapp.shared.generated.resources.Res
import kotlinconfapp.shared.generated.resources.arrow_left_24
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.kotlinconf.Agenda
import org.jetbrains.kotlinconf.SessionId
import org.jetbrains.kotlinconf.ui.components.StyledText

@Composable
fun Schedule(
    agenda: Agenda,
    onBack: () -> Unit,
    onSession: (sessionId: SessionId) -> Unit,
) {
    Column {
        Image(
            painterResource(Res.drawable.arrow_left_24),
            "back",
            modifier = Modifier.clickable(onClick = onBack)
        )
        StyledText("Schedule")
        for (day in agenda.days) {
            StyledText(day.title)
            for (timeSlot in day.timeSlots) {
                StyledText(timeSlot.title)
                for (session in timeSlot.sessions) {
                    StyledText(
                        session.title,
                        modifier = Modifier.clickable { onSession(session.id) })
                }
            }
        }
    }
}
