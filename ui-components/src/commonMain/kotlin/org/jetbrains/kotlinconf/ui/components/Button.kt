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
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.jetbrains.kotlinconf.ui.theme.KotlinConfTheme
import org.jetbrains.kotlinconf.ui.theme.PreviewHelper

private val ButtonShape = RoundedCornerShape(size = 100.dp)

@Composable
fun Button(
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    primary: Boolean = false,
    enabled: Boolean = true,
) {
    val backgroundColor by animateColorAsState(
        if (primary) KotlinConfTheme.colors.primaryBackground
        else Color.Transparent
    )
    val borderColor by animateColorAsState(
        if (primary) Color.Transparent
        else KotlinConfTheme.colors.strokeHalf
    )
    val textColor by animateColorAsState(
        if (primary) KotlinConfTheme.colors.primaryTextInverted
        else KotlinConfTheme.colors.primaryText
    )
    val alpha by animateFloatAsState(if (enabled) 1f else 0.5f)

    Box(
        modifier = modifier
            .alpha(alpha)
            .heightIn(min = 56.dp)
            .border(width = 1.dp, color = borderColor, shape = ButtonShape)
            .clip(ButtonShape)
            .then(if (enabled) Modifier.clickable(onClick = onClick) else Modifier)
            .semantics { role = Role.Button }
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

@Preview
@Composable
internal fun ButtonPreview() {
    PreviewHelper {
        Button("Primary", { }, primary = true)
        Button("Secondary", { }, primary = false)
    }
}
