package org.jetbrains.kotlinconf.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.kotlinconf.ui.generated.resources.UiRes
import org.jetbrains.kotlinconf.ui.generated.resources.bookmark_24
import org.jetbrains.kotlinconf.ui.theme.KotlinConfTheme
import org.jetbrains.kotlinconf.ui.theme.PreviewHelper
import androidx.compose.ui.tooling.preview.PreviewLightDark

@Composable
fun OverlayIconButton(
    icon: DrawableResource,
    enabled: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    contentDescription: String? = null,
) {
    val strokeColor by animateColorAsState(
        if (enabled) KotlinConfTheme.colors.strokeHalf
        else KotlinConfTheme.colors.strokePale
    )
    val iconColor by animateColorAsState(
        if (enabled) KotlinConfTheme.colors.primaryText
        else KotlinConfTheme.colors.placeholderText
    )

    Box(
        modifier
            .size(48.dp)
            .aspectRatio(1f)
            .clip(CircleShape)
            .border(1.dp, strokeColor, CircleShape)
            .clickable(enabled = enabled, onClick = onClick, role = Role.Button)
            .background(KotlinConfTheme.colors.mainBackground),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            painter = painterResource(icon),
            contentDescription = contentDescription,
            tint = iconColor,
            modifier = Modifier.size(24.dp)
        )
    }
}

@PreviewLightDark
@Composable
private fun IconButtonPreviewEnabled() = PreviewHelper {
    OverlayIconButton(
        icon = UiRes.drawable.bookmark_24,
        enabled = true,
        onClick = {},
        contentDescription = "Bookmark",
    )
}

@PreviewLightDark
@Composable
private fun IconButtonPreviewDisabled() = PreviewHelper {
    OverlayIconButton(
        icon = UiRes.drawable.bookmark_24,
        enabled = false,
        onClick = {},
        contentDescription = "Bookmark",
    )
}
