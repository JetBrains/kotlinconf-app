package org.jetbrains.kotlinconf.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.kotlinconf.ui.theme.blackGrey5
import org.jetbrains.kotlinconf.ui.theme.grey50
import org.jetbrains.kotlinconf.ui.theme.greyWhite
import org.jetbrains.kotlinconf.ui.theme.whiteGrey
import org.jetbrains.kotlinconf.ui.painter


@OptIn(ExperimentalResourceApi::class)
@Composable
fun BigMenuItem(
    title: String,
    subtitle: String,
    icon: DrawableResource,
    onClick: () -> Unit = {}
) {
    Column(
        Modifier
            .background(MaterialTheme.colors.whiteGrey)
            .clickable { onClick() }
            .height(160.dp)
    ) {
        Text(
            title, style = MaterialTheme.typography.body2.copy(
                color = MaterialTheme.colors.greyWhite
            ),
            modifier = Modifier.padding(top = 16.dp, start = 16.dp)
        )
        Text(
            subtitle, style = MaterialTheme.typography.body2.copy(color = grey50),
            modifier = Modifier.padding(start = 16.dp)
        )
        Spacer(modifier = Modifier.weight(1f))
        Row(Modifier.fillMaxWidth().padding(end = 16.dp, bottom = 16.dp)) {
            Spacer(modifier = Modifier.weight(1f))
            Icon(
                painter = icon.painter(),
                contentDescription = title,
                tint = MaterialTheme.colors.blackGrey5,
                modifier = Modifier
                    .size(48.dp)
            )
        }
    }
}
