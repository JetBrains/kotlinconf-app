package org.jetbrains.kotlinconf.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import org.jetbrains.kotlinconf.ui.theme.greyGrey5
import org.jetbrains.kotlinconf.ui.theme.text3
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
        Box(
            Modifier.height(48.dp).background(MaterialTheme.colors.whiteGrey).fillMaxWidth(),
        ) {
            if (isLeftVisible) {
                Box(contentAlignment = Alignment.CenterStart, modifier = Modifier.fillMaxWidth()) {
                    IconButton(onClick = onLeftClick) {
                        Icon(
                            painter = Res.drawable.back.painter(),
                            "Back",
                            tint = MaterialTheme.colors.greyGrey5
                        )
                    }
                }
            }

            Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxWidth().height(48.dp)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.h4,
                    color = MaterialTheme.colors.text3
                )
            }

            if (isRightVisible) {
                Box(
                    contentAlignment = Alignment.CenterEnd, modifier = Modifier.fillMaxWidth()
                ) {
                    RightButton(rightIcon, onRightClick = onRightClick)
                }
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
            painter = rightIcon.painter(), "Right", tint = MaterialTheme.colors.greyGrey5
        )
    }
}
