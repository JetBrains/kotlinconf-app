package org.jetbrains.kotlinconf.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.BasicTooltipBox
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberBasicTooltipState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.kotlinconf.ui.generated.resources.UiRes
import org.jetbrains.kotlinconf.ui.generated.resources.view_grid_24
import org.jetbrains.kotlinconf.ui.generated.resources.view_list_24
import org.jetbrains.kotlinconf.ui.theme.KotlinConfTheme
import org.jetbrains.kotlinconf.ui.theme.PreviewHelper
import androidx.compose.ui.tooling.preview.PreviewLightDark


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
                    positionProvider = rememberTooltipPositionProvider(),
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

private val headerTogglePreviewOptions = listOf(
    HeaderToggleOption(UiRes.drawable.view_list_24, "List view"),
    HeaderToggleOption(UiRes.drawable.view_grid_24, "Grid view"),
)

private class HeaderToggleSelectedIndexProvider : PreviewParameterProvider<Int> {
    override val values = headerTogglePreviewOptions.indices.asSequence()
    override fun getDisplayName(index: Int) = "selected=$index"
}

@PreviewLightDark
@Composable
private fun HeaderToggleButtonPreview(
    @PreviewParameter(HeaderToggleSelectedIndexProvider::class) selectedIndex: Int,
) = PreviewHelper {
    HeaderToggleButton(
        options = headerTogglePreviewOptions,
        selectedIndex = selectedIndex,
        onSelect = {},
    )
}
