package org.jetbrains.kotlinconf.ui

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.unit.*
import kotlinconfapp.shared.generated.resources.Res
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.kotlinconf.theme.*

@OptIn(ExperimentalResourceApi::class)
@Composable
fun NavigationBar(
    title: String,
    isLeftVisible: Boolean = true,
    isRightVisible: Boolean = true,
    rightIcon: DrawableResource = Res.drawable.menu,
    onLeftClick: () -> Unit = {},
    onRightClick: () -> Unit = {},
) {
    Column(
        Modifier.fillMaxWidth()
    ) {
        Row(
            Modifier
                .height(48.dp)
                .background(MaterialTheme.colors.whiteGrey)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            if (isLeftVisible) {
                IconButton(onClick = onLeftClick) {
                    Icon(
                        painter = Res.drawable.back.painter(),
                        "Back",
                        tint = MaterialTheme.colors.greyGrey5
                    )
                }
            } else {
                Spacer(Modifier.width(36.dp))
            }

            Spacer(Modifier.weight(1f))

            Text(
                text = title.uppercase(),
                style = MaterialTheme.typography.t2,
                color = grey50
            )

            Spacer(Modifier.weight(1f))

            if (isRightVisible) {
                RightButton(rightIcon, onRightClick = onRightClick)
            } else {
                Spacer(Modifier.width(36.dp))
            }
        }
        Divider(color = MaterialTheme.colors.divider)
    }
}

@OptIn(ExperimentalResourceApi::class)
@Composable
fun RightButton(rightIcon: DrawableResource = Res.drawable.menu, onRightClick: () -> Unit) {
    IconButton(onClick = onRightClick) {
        Icon(
            painter = rightIcon.painter(),
            "Right",
            tint = MaterialTheme.colors.greyGrey5
        )
    }
}
