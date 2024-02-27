package org.jetbrains.kotlinconf.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import org.jetbrains.kotlinconf.ui.theme.grey5Black
import org.jetbrains.kotlinconf.ui.theme.greyGrey20
import org.jetbrains.kotlinconf.ui.theme.greyWhite

@Composable
fun AboutConfSubtitle(tile: String, title: String) {
    Column(Modifier.background(MaterialTheme.colors.grey5Black)) {
        Text(
            tile,
            style = MaterialTheme.typography.body2.copy(color = MaterialTheme.colors.greyGrey20),
            modifier = Modifier.padding(16.dp, top = 24.dp)
        )

        Text(
            title,
            style = MaterialTheme.typography.h2.copy(
                color = MaterialTheme.colors.greyWhite,
                fontWeight = FontWeight.Bold
            ),
            modifier = Modifier.padding(start = 16.dp, bottom = 24.dp)
        )
    }
}

