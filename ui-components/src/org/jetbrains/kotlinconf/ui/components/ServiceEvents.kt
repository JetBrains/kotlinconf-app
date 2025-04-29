package org.jetbrains.kotlinconf.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.jetbrains.kotlinconf.ui.theme.KotlinConfTheme
import org.jetbrains.kotlinconf.ui.theme.PreviewHelper

data class ServiceEventData(
    val title: String,
    val now: Boolean,
    val note: String? = null,
    val time: String? = null,
)

@Composable
private fun ServiceEventItem(
    event: ServiceEventData,
    modifier: Modifier = Modifier,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        modifier = modifier.padding(16.dp).semantics(mergeDescendants = true) {}
    ) {
        Text(
            text = event.title,
            style = KotlinConfTheme.typography.h3,
            color = KotlinConfTheme.colors.primaryText,
        )

        Spacer(Modifier.weight(1f))

        AnimatedVisibility(event.now, enter = fadeIn(), exit = fadeOut()) {
            NowLabel()
        }

        if (event.note != null) {
            Text(
                text = event.note,
                style = KotlinConfTheme.typography.text2,
                color = KotlinConfTheme.colors.noteText,
                maxLines = 1,
            )
        }

        if (event.time != null) {
            Text(
                text = event.time,
                style = KotlinConfTheme.typography.text2,
                color = KotlinConfTheme.colors.primaryText,
                maxLines = 1,
            )
        }
    }
}

@Composable
fun ServiceEvent(
    event: ServiceEventData,
    modifier: Modifier = Modifier,
) {
    ServiceEvents(
        events = listOf(event),
        modifier = modifier,
    )
}

@Composable
fun ServiceEvents(
    events: List<ServiceEventData>,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier
            .clip(RoundedCornerShape(8.dp))
            .background(KotlinConfTheme.colors.tileBackground)
    ) {
        events.forEachIndexed { index, event ->
            if (index > 0) {
                Divider(1.dp, KotlinConfTheme.colors.strokePale)
            }
            ServiceEventItem(event)
        }
    }
}

@Preview
@Composable
internal fun ServiceEventsPreview() {
    PreviewHelper {
        ServiceEvent(
            ServiceEventData(
                title = "Breakfast",
                now = false,
                time = "9:00 – 10:00",
            )
        )
        ServiceEvents(
            listOf(
                ServiceEventData(
                    title = "Lunch",
                    now = false,
                    time = "12:00 – 13:00",
                    note = "In 30 min",
                ),
                ServiceEventData(
                    title = "Dinner",
                    now = true,
                    time = "17:00 – 18:00",
                ),
                ServiceEventData(
                    title = "Party",
                    now = false,
                )
            )
        )
    }
}
