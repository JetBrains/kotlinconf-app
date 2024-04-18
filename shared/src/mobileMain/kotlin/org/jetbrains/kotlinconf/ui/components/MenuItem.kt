package org.jetbrains.kotlinconf.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.kotlinconf.ui.theme.blackGrey5
import org.jetbrains.kotlinconf.ui.theme.greyWhite
import org.jetbrains.kotlinconf.ui.theme.whiteGrey
import org.jetbrains.kotlinconf.ui.painter
import org.jetbrains.kotlinconf.ui.theme.grey5Black

@OptIn(ExperimentalResourceApi::class)
@Composable
fun MenuItem(text: String, icon: DrawableResource, dimmed: Boolean = false, onClick: () -> Unit) {
    Row(
        Modifier
            .height(56.dp)
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .background(if (!dimmed) MaterialTheme.colors.whiteGrey else MaterialTheme.colors.grey5Black)
    ) {
        Text(
            text, modifier = Modifier
                .padding(16.dp),
            style = MaterialTheme.typography.body1.copy(
                color = MaterialTheme.colors.greyWhite
            )
        )

        Spacer(modifier = Modifier.weight(1f))
        Icon(
            painter = icon.painter(),
            contentDescription = "icon",
            tint = MaterialTheme.colors.blackGrey5,
            modifier = Modifier.padding(16.dp)
        )
    }
}
