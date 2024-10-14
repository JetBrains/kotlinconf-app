package org.jetbrains.kotlinconf.ui.components

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import org.jetbrains.kotlinconf.ui.HDivider
import org.jetbrains.kotlinconf.ui.theme.agendaHeaderColor
import org.jetbrains.kotlinconf.ui.theme.grey20Grey80
import org.jetbrains.kotlinconf.ui.theme.greyGrey5
import org.jetbrains.kotlinconf.ui.theme.orange


@Composable
internal fun AgendaTimeSlotHeader(title: String, isLive: Boolean, isFinished: Boolean) {

    Column(
        modifier = Modifier
            .background(MaterialTheme.colors.agendaHeaderColor)
            .fillMaxWidth()

    ) {
        Row(
            Modifier
                .fillMaxWidth()
                .padding(end = 16.dp), verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = title,
                maxLines = 1,
                modifier = Modifier
                    .padding(16.dp),
                style = MaterialTheme.typography.h2.copy(
                    color = if (isFinished) MaterialTheme.colors.grey20Grey80 else MaterialTheme.colors.greyGrey5
                )
            )
            Spacer(modifier = Modifier.weight(1f))
            if (isLive) {
                LiveIndicator()
            }
        }
        HDivider()
    }
}

@Composable
fun LiveIndicator() {
    Row(verticalAlignment = Alignment.CenterVertically) {
        val transition = rememberInfiniteTransition()
        val transparency by transition.animateFloat(
            initialValue = 0.0f,
            targetValue = 1.0f,
            animationSpec = infiniteRepeatable(
                animation = tween(1000),
                repeatMode = RepeatMode.Reverse
            )
        )
        Box(
            modifier = Modifier
                .clip(CircleShape)
                .size(12.dp)
                .background(orange.copy(alpha = transparency))
        )
        Text(
            "Now", modifier = Modifier
                .padding(start = 6.dp)
                .padding(end = 16.dp),
            style = MaterialTheme.typography.body2.copy(
                color = orange.copy(alpha = transparency)
            )
        )
    }
}