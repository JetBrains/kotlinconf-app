package org.jetbrains.kotlinconf.android.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.*
import androidx.compose.ui.tooling.preview.*
import androidx.compose.ui.unit.*
import io.ktor.util.date.*
import org.jetbrains.kotlinconf.*
import org.jetbrains.kotlinconf.android.theme.*
import org.jetbrains.kotlinconf.android.ui.*


@Composable
internal fun AgendaTimeSlotHeader(title: String, isLive: Boolean, isFinished: Boolean) {
    val transition = rememberInfiniteTransition()
    val transparency by transition.animateFloat(
        initialValue = 0.0f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000),
            repeatMode = RepeatMode.Reverse
        )
    )
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
                Box(
                    modifier = Modifier
                        .clip(CircleShape)
                        .size(12.dp)
                        .background(orange.copy(alpha = transparency))
                )
                Text(
                    "NOW", modifier = Modifier
                        .padding(start = 6.dp)
                        .padding(end = 16.dp),
                    style = MaterialTheme.typography.t2.copy(
                        color = orange.copy(alpha = transparency)
                    )
                )

            }
        }
        HDivider()
    }
}

@Composable
@Preview(showBackground = true)
private fun AgendaTimeSlotHeaderPreview() {
    KotlinConfTheme {
        AgendaTimeSlotHeader(
            TimeSlot(GMTDate.START, GMTDate.START, true, true, listOf(), false, false, false).title,
            isLive = false,
            true
        )
    }
}
