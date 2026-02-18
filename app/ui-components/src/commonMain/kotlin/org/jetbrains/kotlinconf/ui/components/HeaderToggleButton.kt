package org.jetbrains.kotlinconf.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.BasicTooltipBox
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberBasicTooltipState
import androidx.compose.foundation.selection.selectable
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
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntRect
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.PopupPositionProvider
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.kotlinconf.ui.generated.resources.UiRes
import org.jetbrains.kotlinconf.ui.generated.resources.view_grid_24
import org.jetbrains.kotlinconf.ui.generated.resources.view_list_24
import org.jetbrains.kotlinconf.ui.theme.KotlinConfTheme
import org.jetbrains.kotlinconf.ui.theme.PreviewHelper


@Composable
private fun rememberPositionProvider(): PopupPositionProvider {
    val tooltipAnchorSpacing = with(LocalDensity.current) { 4.dp.roundToPx() }
    return remember(tooltipAnchorSpacing) {
        object : PopupPositionProvider {
            override fun calculatePosition(
                anchorBounds: IntRect,
                windowSize: IntSize,
                layoutDirection: LayoutDirection,
                popupContentSize: IntSize,
            ): IntOffset {
                val x = anchorBounds.left + (anchorBounds.width - popupContentSize.width) / 2
                var y = anchorBounds.bottom - tooltipAnchorSpacing
                if (y < 0) y = anchorBounds.bottom + tooltipAnchorSpacing
                return IntOffset(x, y)
            }
        }
    }
}

private val HeaderToggleItemWidth = 48.dp
private val HeaderToggleItemHeight = 40.dp

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun HeaderToggleButton(
    options: List<HeaderToggleOption>,
    selectedIndex: Int,
    onSelect: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier
            .border(1.dp, KotlinConfTheme.colors.strokePale, CircleShape)
            .size(width = HeaderToggleItemWidth * options.size, height = HeaderToggleItemHeight)
    ) {
        val offsetX by animateDpAsState(
            targetValue = HeaderToggleItemWidth * selectedIndex,
            animationSpec = spring(stiffness = Spring.StiffnessMediumLow)
        )
        Box(
            Modifier
                .offset(x = offsetX)
                .clip(CircleShape)
                .background(KotlinConfTheme.colors.primaryBackground)
                .size(width = HeaderToggleItemWidth, height = HeaderToggleItemHeight)
                .align(Alignment.CenterStart)
        )
        Row {
            options.forEachIndexed { index, option ->
                val selected = index == selectedIndex
                val iconColor by animateColorAsState(
                    if (selected) KotlinConfTheme.colors.primaryTextWhiteFixed
                    else KotlinConfTheme.colors.primaryText
                )

                BasicTooltipBox(
                    positionProvider = rememberPositionProvider(),
                    tooltip = { Tooltip(option.contentDescription) },
                    state = rememberBasicTooltipState(),
                ) {
                    Icon(
                        painter = painterResource(option.icon),
                        contentDescription = option.contentDescription,
                        tint = iconColor,
                        modifier = Modifier
                            .clip(CircleShape)
                            .selectable(
                                selected = selected,
                                enabled = true,
                                onClick = { onSelect(index) },
                                role = Role.Tab,
                            )
                            .padding(horizontal = 12.dp, vertical = 8.dp)
                            .size(24.dp)
                    )
                }
            }
        }
    }
}

data class HeaderToggleOption(
    val icon: DrawableResource,
    val contentDescription: String,
)

@Preview
@Composable
internal fun HeaderToggleButtonPreview() {
    val options = listOf(
        HeaderToggleOption(UiRes.drawable.view_list_24, "List view"),
        HeaderToggleOption(UiRes.drawable.view_grid_24, "Grid view"),
    )
    PreviewHelper {
        var selectedIndex by remember { mutableStateOf(0) }
        HeaderToggleButton(
            options = options,
            selectedIndex = selectedIndex,
            onSelect = { selectedIndex = it },
        )

        HeaderToggleButton(
            options = options,
            selectedIndex = 1,
            onSelect = {},
        )
    }
}
