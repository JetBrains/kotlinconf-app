package org.jetbrains.kotlinconf.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.focus.focusProperties
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.jetbrains.kotlinconf.ui.theme.KotlinConfTheme
import org.jetbrains.kotlinconf.ui.theme.PreviewHelper

private val ToggleBackgroundShape = RoundedCornerShape(size = 100.dp)
private val ToggleThumbShape = CircleShape

private val ToggleWidth = 28.dp
private val ToggleHeight = 16.dp
private val ThumbSize = 18.dp

@Composable
fun Toggle(
    enabled: Boolean,
    onToggle: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
) {
    val toggleColor by animateColorAsState(
        if (enabled) KotlinConfTheme.colors.toggleOn
        else KotlinConfTheme.colors.toggleOff
    )

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .toggleable(
                value = enabled,
                enabled = true,
                role = Role.Switch,
                onValueChange = { onToggle(!enabled) },
                interactionSource = interactionSource,
                indication = null,
            )
            .clearAndSetSemantics {},
    ) {
        Box(
            Modifier.size(ToggleWidth, ToggleHeight)
                .clip(ToggleBackgroundShape)
                .focusProperties {
                    canFocus = false
                }
                .clickable(
                    onClick = { onToggle(!enabled) },
                    interactionSource = interactionSource,
                    indication = LocalIndication.current,
                )
                .background(toggleColor)
        )
        val xOffset by animateDpAsState(if (enabled) ToggleWidth / 4 else -ToggleWidth / 4)
        val thumbCenterColor = KotlinConfTheme.colors.mainBackground
        Box(
            Modifier
                .offset(x = xOffset)
                .size(ToggleHeight)
                .wrapContentSize(unbounded = true)
                .size(ThumbSize)
                .clip(ToggleThumbShape)
                .background(toggleColor)
                .focusProperties {
                    canFocus = false
                }
                .clickable(
                    onClick = { onToggle(!enabled) },
                    interactionSource = interactionSource,
                    indication = LocalIndication.current,
                )
                .drawBehind {
                    drawCircle(color = thumbCenterColor, radius = (size.minDimension / 2) - 2.dp.toPx())
                }
        )
    }
}

@Preview
@Composable
internal fun TogglePreview() {
    PreviewHelper {
        var state1 by remember { mutableStateOf(false) }
        Toggle(state1, { state1 = it })

        var state2 by remember { mutableStateOf(true) }
        Toggle(state2, { state2 = it })
    }
}
