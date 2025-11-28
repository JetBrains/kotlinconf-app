package org.jetbrains.kotlinconf.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.requiredSizeIn
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.kotlinconf.ui.generated.resources.UiRes
import org.jetbrains.kotlinconf.ui.generated.resources.bookmark_24
import org.jetbrains.kotlinconf.ui.theme.KotlinConfTheme
import org.jetbrains.kotlinconf.ui.theme.PreviewHelper

@Composable
fun IconButton(
    icon: DrawableResource,
    enabled: Boolean,
    onClick: () -> Unit,
    contentDescription: String,
    modifier: Modifier = Modifier,
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
            .then(if (enabled) Modifier.clickable(onClick = onClick) else Modifier)
            .semantics { this.contentDescription = contentDescription }
            .size(48.dp)
            .aspectRatio(1f)
            .border(1.dp, strokeColor, CircleShape)
            .clip(CircleShape)
            .background(KotlinConfTheme.colors.mainBackground),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            painter = painterResource(icon),
            contentDescription = null,
            tint = iconColor,
            modifier = Modifier.size(24.dp)
        )
    }
}

@Preview
@Composable
fun IconButtonPreviewEnabled() {
    PreviewHelper {
        IconButton(
            icon = UiRes.drawable.bookmark_24,
            enabled = true,
            onClick = {},
            contentDescription = "",
        )
    }
}

@Preview
@Composable
fun IconButtonPreviewDisabled() {
    PreviewHelper {
        IconButton(
            icon = UiRes.drawable.bookmark_24,
            enabled = false,
            onClick = {},
            contentDescription = "",
        )
    }
}