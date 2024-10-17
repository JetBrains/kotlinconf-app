package org.jetbrains.kotlinconf.ui.welcome

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.unit.dp
import org.jetbrains.kotlinconf.ui.HDivider
import org.jetbrains.kotlinconf.ui.theme.greyGrey5
import org.jetbrains.kotlinconf.ui.theme.violet
import org.jetbrains.kotlinconf.ui.theme.white
import org.jetbrains.kotlinconf.ui.theme.whiteGrey

@Composable
fun FormWithButtons(
    onAccept: () -> Unit,
    onReject: () -> Unit,
    body: @Composable () -> Unit
) {
    Box(
        Modifier.fillMaxSize(),
    ) {
        Column(Modifier.fillMaxHeight()) {
            body()
        }
        Column {
            Spacer(Modifier.weight(1f))
            Buttons(onAccept, onReject)
        }
    }
}

@Composable
private fun Buttons(onAccept: () -> Unit, onReject: () -> Unit) {
    Column(Modifier.height(68.dp)) {
        HDivider()
        Row {
            Button(
                onClick = { onReject() },
                modifier = Modifier
                    .weight(1f)
                    .height(68.dp)
                    .shadow(0.dp),
                shape = RectangleShape,
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = MaterialTheme.colors.whiteGrey,
                )
            ) {
                Text(
                    "Reject",
                    style = MaterialTheme.typography.body2.copy(color = MaterialTheme.colors.greyGrey5)
                )
            }
            Button(
                onClick = { onAccept() },
                modifier = Modifier
                    .weight(1f)
                    .height(68.dp)
                    .shadow(0.dp),
                shape = RectangleShape,
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = violet,
                )
            ) {
                Text(
                    "Accept",
                    style = MaterialTheme.typography.body2.copy(color = white)
                )
            }
        }
    }
}
