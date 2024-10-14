package org.jetbrains.kotlinconf.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import kotlinconfapp.shared.generated.resources.Res
import kotlinconfapp.shared.generated.resources.schedule_party_section_bird
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.kotlinconf.ui.painter
import org.jetbrains.kotlinconf.ui.theme.bannerText
import org.jetbrains.kotlinconf.ui.theme.grey20Grey80
import org.jetbrains.kotlinconf.ui.theme.greyGrey5

@OptIn(ExperimentalResourceApi::class)
@Composable
fun Party(time: String, isFinished: Boolean) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .alpha(if (isFinished) 0.5f else 1.0f)
    ) {
        Text(
            text = time,
            maxLines = 1,
            modifier = Modifier
                .padding(16.dp),
            style = MaterialTheme.typography.h2.copy(
                color = if (isFinished) MaterialTheme.colors.grey20Grey80 else MaterialTheme.colors.greyGrey5
            )
        )
        Row(
            modifier = Modifier
                .padding(start = 16.dp, top = 12.dp, bottom = 24.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = Res.drawable.schedule_party_section_bird.painter(),
                contentDescription = "party",
                modifier = Modifier
                    .size(26.dp, 26.dp),
                contentScale = ContentScale.Crop,
            )
            Text(
                "Party!",
                style = MaterialTheme.typography.bannerText.copy(
                    color = if (isFinished) MaterialTheme.colors.grey20Grey80 else MaterialTheme.colors.greyGrey5,
                ),
                modifier = Modifier.padding(start = 8.dp)
            )
        }
    }
}
