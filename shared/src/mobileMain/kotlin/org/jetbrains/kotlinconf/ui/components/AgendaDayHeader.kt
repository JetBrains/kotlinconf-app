package org.jetbrains.kotlinconf.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import org.jetbrains.kotlinconf.theme.Vector
import org.jetbrains.kotlinconf.theme.agendaHeaderColor


@Composable
internal fun AgendaDayHeader(title: String) {
    val image = when (title) {
        "APRIL 12" -> Vector.SCHEDULE_BANNERS[0]
        "APRIL 13" -> Vector.SCHEDULE_BANNERS[1]
        else -> Vector.SCHEDULE_BANNERS[2]
    }

    Column(
        modifier = Modifier
            .background(MaterialTheme.colors.agendaHeaderColor)
            .fillMaxWidth()
            .padding(top = 4.dp)
    ) {
        Image(
            image,
            contentDescription = null,
            modifier = Modifier
                .height(56.dp)
//                .offset(-24.dp)
                .fillMaxWidth(),
            contentScale = ContentScale.None
        )
    }
}
