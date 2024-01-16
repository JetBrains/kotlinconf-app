package org.jetbrains.kotlinconf.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.unit.*
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.kotlinconf.theme.*
import org.jetbrains.kotlinconf.ui.*

@Composable
fun Break(
    duration: String,
    title: String,
    isLive: Boolean,
    icon: Painter = Drawables.CUP_ICON,
    liveIcon: Painter = Drawables.CUP_ACTIVE_ICON
) {
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
        Modifier.background(MaterialTheme.colors.whiteGrey)
    ) {
        Row(
            Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            VDivider(modifier = Modifier.height(24.dp))
        }
        HDivider()

        Row(
            Modifier.padding(16.dp),
        ) {
            Text(
                duration,
                style = MaterialTheme.typography.h4,
                color = MaterialTheme.colors.greyWhite
            )
            Text(
                " / $title", style = MaterialTheme.typography.t2,
                color = MaterialTheme.colors.greyWhite
            )
            Spacer(modifier = Modifier.weight(1f))
            Icon(
                painter = if (isLive) liveIcon else icon,
                contentDescription = "icon",
                tint = if (isLive) orange.copy(alpha = transparency) else grey50
            )
        }

        HDivider()
        Row(
            Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            VDivider(modifier = Modifier.height(24.dp))
        }
        HDivider()
    }
}
