package org.jetbrains.kotlinconf.ui.components

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinconfapp.shared.generated.resources.Res
import kotlinconfapp.shared.generated.resources.cup
import kotlinconfapp.shared.generated.resources.cup_active
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.kotlinconf.ui.theme.grey50
import org.jetbrains.kotlinconf.ui.theme.greyWhite
import org.jetbrains.kotlinconf.ui.theme.orange
import org.jetbrains.kotlinconf.ui.theme.whiteGrey
import org.jetbrains.kotlinconf.ui.HDivider
import org.jetbrains.kotlinconf.ui.VDivider
import org.jetbrains.kotlinconf.ui.painter

@OptIn(ExperimentalResourceApi::class)
@Composable
fun Break(
    duration: String,
    title: String,
    isLive: Boolean,
    icon: DrawableResource = Res.drawable.cup,
    liveIcon: DrawableResource = Res.drawable.cup_active
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
                " / $title", style = MaterialTheme.typography.body2,
                color = MaterialTheme.colors.greyWhite
            )
            Spacer(modifier = Modifier.weight(1f))
            val iconResource = if (isLive) liveIcon else icon
            Icon(
                painter = iconResource.painter(),
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
