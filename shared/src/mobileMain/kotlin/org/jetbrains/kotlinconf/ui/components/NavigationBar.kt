package org.jetbrains.kotlinconf.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinconfapp.shared.generated.resources.Res
import kotlinconfapp.shared.generated.resources.back
import kotlinconfapp.shared.generated.resources.menu
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.kotlinconf.ui.painter
import org.jetbrains.kotlinconf.ui.theme.divider
import org.jetbrains.kotlinconf.ui.theme.grey50
import org.jetbrains.kotlinconf.ui.theme.greyGrey5
import org.jetbrains.kotlinconf.ui.theme.whiteGrey

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
                text = title,
                style = MaterialTheme.typography.h4,
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
