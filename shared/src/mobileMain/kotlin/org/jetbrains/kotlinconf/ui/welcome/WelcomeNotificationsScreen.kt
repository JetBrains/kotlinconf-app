package org.jetbrains.kotlinconf.ui.welcome

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.unit.dp
import kotlinconfapp.shared.generated.resources.Res
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.kotlinconf.theme.grey20Grey80
import org.jetbrains.kotlinconf.theme.greyGrey20
import org.jetbrains.kotlinconf.theme.greyWhite
import org.jetbrains.kotlinconf.theme.whiteGrey
import org.jetbrains.kotlinconf.ui.painter

@OptIn(ExperimentalResourceApi::class)
@Composable
fun WelcomeNotificationsScreen(onAcceptNotifications: () -> Unit, onClose: () -> Unit) {
    FormWithButtons(onAccept = {
        onAcceptNotifications()
        onClose()
    }, onReject = {
        onClose()
    }) {
        Column(Modifier.fillMaxWidth()) {
            Image(
                painter = Res.drawable.notifications_bird.painter(),
                contentDescription = null,
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(top = 45.dp, bottom = 50.dp)
                    .width(72.dp)
                    .height(60.dp)
                    .fillMaxWidth()
            )

            Text(
                "Do you want reminders?".uppercase(),
                style = MaterialTheme.typography.h2.copy(color = MaterialTheme.colors.greyWhite),
                modifier = Modifier.padding(start = 16.dp, end = 16.dp, bottom = 24.dp)
            )

            Text(
                REMINDERS_TEXT,
                style = MaterialTheme.typography.body2.copy(color = MaterialTheme.colors.greyGrey20),
                modifier = Modifier.padding(start = 16.dp, end = 16.dp, bottom = 24.dp)
            )

            Row(Modifier.padding(16.dp)) {
                Box(
                    modifier = Modifier
                        .clip(RectangleShape)
                        .size(12.dp)
                        .border(BorderStroke(1.dp, MaterialTheme.colors.greyGrey20), RectangleShape)
                        .background(MaterialTheme.colors.whiteGrey)
                )

                Spacer(Modifier.width(8.dp))

                Box(
                    modifier = Modifier
                        .clip(RectangleShape)
                        .size(12.dp)
                        .border(
                            BorderStroke(1.dp, MaterialTheme.colors.grey20Grey80),
                            RectangleShape
                        )
                        .background(MaterialTheme.colors.greyWhite)
                )
            }
        }
    }
}

internal const val REMINDERS_TEXT =
    "Get reminders about talks that you don’t want to miss. You will receive a message 5 minutes before the session begins so you have time to get ready."
