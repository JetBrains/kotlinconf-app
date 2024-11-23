package org.jetbrains.kotlinconf.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.jetbrains.kotlinconf.ui.components.NowLabel
import org.jetbrains.kotlinconf.ui.components.StyledText
import org.jetbrains.kotlinconf.ui.theme.KotlinConfTheme
import org.jetbrains.kotlinconf.ui.theme.PreviewHelper

data class ServiceEventData(
    val name: String,
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
        modifier = modifier.padding(16.dp)
    ) {
        StyledText(
            text = event.name,
            style = KotlinConfTheme.typography.h3,
            color = KotlinConfTheme.colors.primaryText,
        )

        Spacer(Modifier.weight(1f))

        if (event.now) {
            NowLabel()
        }

        if (event.note != null) {
            StyledText(
                text = event.note,
                style = KotlinConfTheme.typography.text2,
                color = KotlinConfTheme.colors.noteText,
                maxLines = 1,
            )
        }

        if (event.time != null) {
            StyledText(
                text = event.time,
                style = KotlinConfTheme.typography.text2,
                color = KotlinConfTheme.colors.primaryText,
                maxLines = 1,
            )
        }
    }
}

@Composable
private fun Divider(
    thickness: Dp,
    color: Color,
) {
    Canvas(Modifier.fillMaxWidth().height(thickness)) {
        drawLine(
            color = color,
            strokeWidth = thickness.toPx(),
            start = Offset(0f, thickness.toPx() / 2),
            end = Offset(size.width, thickness.toPx() / 2),
        )
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
                name = "Breakfast",
                now = false,
                time = "9:00 – 10:00",
            )
        )
        ServiceEvents(
            listOf(
                ServiceEventData(
                    name = "Lunch",
                    now = false,
                    time = "12:00 – 13:00",
                    note = "In 30 min",
                ),
                ServiceEventData(
                    name = "Dinner",
                    now = true,
                    time = "17:00 – 18:00",
                ),
                ServiceEventData(
                    name = "Party",
                    now = false,
                )
            )
        )
    }
}
