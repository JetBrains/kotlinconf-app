package org.jetbrains.kotlinconf.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import kotlinconfapp.shared.generated.resources.Res
import kotlinconfapp.shared.generated.resources.schedule_day_1_banner
import kotlinconfapp.shared.generated.resources.schedule_day_2_banner
import kotlinconfapp.shared.generated.resources.schedule_day_3_banner
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.kotlinconf.EventDay
import org.jetbrains.kotlinconf.ui.theme.agendaHeaderColor
import org.jetbrains.kotlinconf.ui.painter


@OptIn(ExperimentalResourceApi::class)
@Composable
internal fun AgendaDayHeader(day: EventDay) {
    val image = when (day) {
        EventDay.May22 -> Res.drawable.schedule_day_1_banner
        EventDay.May23 -> Res.drawable.schedule_day_2_banner
        else -> Res.drawable.schedule_day_3_banner
    }

    Column(
        modifier = Modifier
            .background(MaterialTheme.colors.agendaHeaderColor)
            .fillMaxWidth()
            .heightIn(min = 56.dp)
            .padding(top = 4.dp)
    ) {
        Image(
            image.painter(),
            contentDescription = stringResource(day.title),
            modifier = Modifier
                .height(56.dp)
                .fillMaxWidth(),
            contentScale = ContentScale.None
        )
    }
}
