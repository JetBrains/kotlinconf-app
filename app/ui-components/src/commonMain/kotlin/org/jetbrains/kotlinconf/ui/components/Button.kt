package org.jetbrains.kotlinconf.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import org.jetbrains.kotlinconf.ui.theme.KotlinConfTheme
import org.jetbrains.kotlinconf.ui.theme.PreviewHelper
import org.jetbrains.kotlinconf.ui.utils.PreviewLightDark

private val ButtonShape = RoundedCornerShape(percent = 100)

@Composable
fun Button(
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    primary: Boolean = false,
    enabled: Boolean = true,
    primaryBackground: Color = KotlinConfTheme.colors.primaryBackground,
) {
    val backgroundColor by animateColorAsState(
        if (primary) primaryBackground
        else Color.Transparent
    )
    val borderColor by animateColorAsState(
        if (primary) Color.Transparent
        else KotlinConfTheme.colors.strokeHalf
    )
    val textColor by animateColorAsState(
        if (primary) KotlinConfTheme.colors.primaryTextWhiteFixed
        else KotlinConfTheme.colors.primaryText
    )
    val alpha by animateFloatAsState(if (enabled) 1f else 0.5f)

    Box(
        modifier = modifier
            .graphicsLayer { this.alpha = alpha }
            .heightIn(min = 56.dp)
            .border(width = 1.dp, color = borderColor, shape = ButtonShape)
            .clip(ButtonShape)
            .clickable(enabled = enabled, onClick = onClick, role = Role.Button)
            .background(backgroundColor)
            .padding(horizontal = 32.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            label,
            style = KotlinConfTheme.typography.text1,
            color = textColor,
        )
    }
}

@PreviewLightDark
@Composable
private fun ButtonPrimaryPreview() = PreviewHelper {
    Button("Primary", { }, primary = true)
}

@PreviewLightDark
@Composable
private fun ButtonSecondaryPreview() = PreviewHelper {
    Button("Secondary", { }, primary = false)
}
