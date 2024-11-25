package org.jetbrains.kotlinconf.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkHorizontally
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import kotlinconfapp.ui_components.generated.resources.Res
import kotlinconfapp.ui_components.generated.resources.up_24
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.jetbrains.kotlinconf.ui.theme.KotlinConfTheme
import org.jetbrains.kotlinconf.ui.theme.PreviewHelper

data class FilterItem(
    val id: Int,
    val value: String,
    val isSelected: Boolean,
)

@Composable
fun Filters(
    categories: List<FilterItem>,
    levels: List<FilterItem>,
    sessionFormats: List<FilterItem>,
    toggleItem: (FilterItem, Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    var isExpanded by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(KotlinConfTheme.colors.tileBackground),
    ) {
        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .toggleable(
                    enabled = true,
                    value = isExpanded,
                    onValueChange = { isExpanded = it },
                    indication = null,
                    interactionSource = remember { MutableInteractionSource() }
                )
                .heightIn(min = 46.dp)
                .padding(vertical = 11.dp)
                .fillMaxWidth()
        ) {
            val iconRotation by animateFloatAsState(if (isExpanded) 0f else 180f)
            Action(
                label = "Filter by tags",
                icon = Res.drawable.up_24,
                size = ActionSize.Medium,
                enabled = true,
                onClick = { isExpanded = !isExpanded },
                iconRotation = iconRotation,
            )

            AnimatedVisibility(
                visible = !isExpanded,
                enter = fadeIn() + expandHorizontally(clip = false, expandFrom = Alignment.Start),
                exit = fadeOut() + shrinkHorizontally(clip = false, shrinkTowards = Alignment.Start),
            ) {
                Row {
                    Spacer(Modifier.width(8.dp))
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier.size(24.dp)
                            .clip(CircleShape)
                            .background(KotlinConfTheme.colors.primaryBackground)
                    ) {
                        val count = remember(categories, levels, sessionFormats) {
                            categories.count { it.isSelected } +
                                    levels.count { it.isSelected } +
                                    sessionFormats.count { it.isSelected }
                        }
                        StyledText(
                            text = count.toString(),
                            color = KotlinConfTheme.colors.primaryTextInverted,
                            style = KotlinConfTheme.typography.text2,
                        )
                    }
                }
            }
        }

        AnimatedVisibility(
            visible = isExpanded,
            enter = fadeIn() + expandVertically(clip = false, expandFrom = Alignment.Top),
            exit = fadeOut() + shrinkVertically(clip = false, shrinkTowards = Alignment.Top),
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.padding(12.dp),
            ) {
                FilterItemGroup("Category", categories, toggleItem)
                FilterItemGroup("Level", levels, toggleItem)
                FilterItemGroup("Session format", sessionFormats, toggleItem)
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun FilterItemGroup(
    title: String,
    items: List<FilterItem>,
    toggle: (FilterItem, Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = modifier,
    ) {
        StyledText(
            text = title,
            style = KotlinConfTheme.typography.text2,
            color = KotlinConfTheme.colors.noteText,
        )
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            items.forEach { item ->
                FilterTag(
                    label = item.value,
                    selected = item.isSelected,
                    onSelect = { value -> toggle(item, value) },
                )
            }
        }
    }
}

@Preview
@Composable
internal fun FiltersPreview() {
    PreviewHelper {
        val categories = remember {
            mutableStateListOf(
                FilterItem(1, "Server-side", false),
                FilterItem(2, "Multiplatform", false),
                FilterItem(3, "Android", false),
                FilterItem(4, "Extensibility/Tooling", false),
                FilterItem(5, "Languages and Best Practices", false),
                FilterItem(6, "Other", false),
            )
        }
        val levels = remember {
            mutableStateListOf(
                FilterItem(11, "Introductory and overview", false),
                FilterItem(12, "Intermediate", false),
                FilterItem(13, "Advanced", false),
            )
        }
        val sessionFormats = remember {
            mutableStateListOf(
                FilterItem(21, "Workshop", false),
                FilterItem(22, "Regular session", false),
                FilterItem(23, "Lightning session", false),
            )
        }

        Filters(
            categories = categories,
            levels = levels,
            sessionFormats = sessionFormats,
            toggleItem = { item, value ->
                val newItem = item.copy(isSelected = value)
                categories.replace(item, newItem)
                levels.replace(item, newItem)
                sessionFormats.replace(item, newItem)
            }
        )
    }
}

private fun <T> MutableList<T>.replace(old: T, new: T) {
    val index = indexOf(old)
    if (index >= 0) {
        set(index, new)
    }
}
