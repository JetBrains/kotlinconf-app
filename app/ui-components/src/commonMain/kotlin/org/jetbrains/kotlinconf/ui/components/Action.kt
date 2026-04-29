package org.jetbrains.kotlinconf.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.kotlinconf.ui.generated.resources.UiRes
import org.jetbrains.kotlinconf.ui.generated.resources.arrow_right_24
import org.jetbrains.kotlinconf.ui.theme.KotlinConfTheme
import org.jetbrains.kotlinconf.ui.theme.PreviewHelper
import androidx.compose.ui.tooling.preview.PreviewLightDark

enum class ActionSize {
    Medium, Large,
}

@Composable
fun Action(
    label: String,
    icon: DrawableResource,
    size: ActionSize,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
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
            .then(
                if (onClick != null) {
                    Modifier.clickable(
                        onClick = onClick,
                        enabled = enabled,
                        interactionSource = null,
                        indication = null,
                        role = Role.Button,
                    )
                } else {
                    Modifier
                }
            )
    ) {
        Text(
            text = label,
            color = color,
            style = when (size) {
                ActionSize.Medium -> KotlinConfTheme.typography.h4
                ActionSize.Large -> KotlinConfTheme.typography.h3
            },
            maxLines = 1,
            modifier = Modifier.weight(1f, fill = false),
        )
        Icon(
            modifier = Modifier
                .size(
                    when (size) {
                        ActionSize.Medium -> 20.dp
                        ActionSize.Large -> 24.dp
                    }
                )
                .graphicsLayer { rotationZ = iconRotation },
            painter = painterResource(icon),
            contentDescription = null,
            tint = color,
        )
    }
}

private data class ActionPreviewParams(val size: ActionSize, val enabled: Boolean)

private class ActionPreviewParamsProvider : PreviewParameterProvider<ActionPreviewParams> {
    override val values = ActionSize.entries.flatMap { size ->
        listOf(true, false).map { enabled ->
            ActionPreviewParams(size, enabled)
        }
    }.asSequence()

    override fun getDisplayName(index: Int): String {
        val params = values.elementAt(index)
        val enabled = if (params.enabled) "enabled" else "disabled"
        return "${params.size}, $enabled"
    }
}

@PreviewLightDark
@Composable
private fun ActionPreview(
    @PreviewParameter(ActionPreviewParamsProvider::class) params: ActionPreviewParams,
) = PreviewHelper {
    Action(
        label = "Action",
        icon = UiRes.drawable.arrow_right_24,
        size = params.size,
        onClick = {},
        enabled = params.enabled,
    )
}
