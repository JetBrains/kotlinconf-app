package org.jetbrains.kotlinconf.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import kotlinconfapp.ui_components.generated.resources.Res
import kotlinconfapp.ui_components.generated.resources.arrow_right_24
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.jetbrains.kotlinconf.ui.theme.KotlinConfTheme
import org.jetbrains.kotlinconf.ui.theme.PreviewHelper

enum class ActionSize {
    Medium, Large,
}

@Composable
fun Action(
    label: String,
    icon: DrawableResource,
    size: ActionSize,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    iconRotation: Float = 0f,
) {
    val color by animateColorAsState(
        if (enabled) KotlinConfTheme.colors.primaryText
        else KotlinConfTheme.colors.noteText,
        ColorSpringSpec,
    )
    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .clickable(
                onClick = onClick,
                enabled = enabled,
                interactionSource = null,
                indication = null,
                role = Role.Button,
            )
    ) {
        Text(
            text = label,
            color = color,
            style = when (size) {
                ActionSize.Medium -> KotlinConfTheme.typography.h4
                ActionSize.Large -> KotlinConfTheme.typography.h3
            }
        )
        Icon(
            modifier = Modifier
                .size(
                    when (size) {
                        ActionSize.Medium -> 20.dp
                        ActionSize.Large -> 24.dp
                    }
                )
                .rotate(iconRotation),
            painter = painterResource(icon),
            contentDescription = null,
            tint = color,
        )
    }
}

@Preview
@Composable
internal fun ActionPreview() {
    PreviewHelper {
        Action(
            label = "Action",
            icon = Res.drawable.arrow_right_24,
            size = ActionSize.Medium,
            onClick = {},
            enabled = true,
        )
        Action(
            label = "Action",
            icon = Res.drawable.arrow_right_24,
            size = ActionSize.Medium,
            onClick = {},
            enabled = false,
        )
        Action(
            label = "Action",
            icon = Res.drawable.arrow_right_24,
            size = ActionSize.Large,
            onClick = {},
            enabled = true,
        )
        Action(
            label = "Action",
            icon = Res.drawable.arrow_right_24,
            size = ActionSize.Large,
            onClick = {},
            enabled = false,
        )
    }
}
