package org.jetbrains.kotlinconf.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.expandHorizontally
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
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.unit.dp
import kotlinconfapp.ui_components.generated.resources.Res
import kotlinconfapp.ui_components.generated.resources.action_state_description_collapsed
import kotlinconfapp.ui_components.generated.resources.action_state_description_expanded
import kotlinconfapp.ui_components.generated.resources.filter_by_tags
import kotlinconfapp.ui_components.generated.resources.filter_by_tags_tag_count
import kotlinconfapp.ui_components.generated.resources.filter_label_category
import kotlinconfapp.ui_components.generated.resources.filter_label_level
import kotlinconfapp.ui_components.generated.resources.filter_label_session_format
import kotlinconfapp.ui_components.generated.resources.up_24
import org.jetbrains.compose.resources.pluralStringResource
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.jetbrains.kotlinconf.ui.theme.KotlinConfTheme
import org.jetbrains.kotlinconf.ui.theme.PreviewHelper

enum class FilterItemType {
    Category, Level, Format,
}

data class FilterItem(
    val type: FilterItemType,
    val value: String,
    val isSelected: Boolean,
)

@Composable
fun Filters(
    tags: List<FilterItem>,
    toggleItem: (FilterItem, Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    var isExpanded by rememberSaveable { mutableStateOf(false) }

    Column(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(KotlinConfTheme.colors.tileBackground),
    ) {
        val stateDesc = stringResource(
            if (isExpanded) Res.string.action_state_description_expanded
            else Res.string.action_state_description_collapsed
        )
        val filterByTagsText = stringResource(Res.string.filter_by_tags)

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
                .semantics {
                    stateDescription = stateDesc
                    contentDescription = filterByTagsText
                }
        ) {
            val iconRotation by animateFloatAsState(if (isExpanded) 0f else 180f)
            Action(
                modifier = Modifier.clearAndSetSemantics {},
                label = stringResource(Res.string.filter_by_tags),
                icon = Res.drawable.up_24,
                size = ActionSize.Medium,
                enabled = true,
                onClick = { isExpanded = !isExpanded },
                iconRotation = iconRotation,
            )

            val count = remember(tags) { tags.count { it.isSelected } }
            AnimatedVisibility(
                visible = !isExpanded && count > 0,
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
                        val tagCountContentDescription = pluralStringResource(Res.plurals.filter_by_tags_tag_count, count, count)
                        Text(
                            text = count.toString(),
                            color = KotlinConfTheme.colors.primaryTextInverted,
                            style = KotlinConfTheme.typography.text2,
                            modifier = Modifier.semantics {
                                contentDescription = tagCountContentDescription
                            }
                        )
                    }
                }
            }
        }

        AnimatedVisibility(
            visible = isExpanded,
            enter = fadeIn(spring(stiffness = Spring.StiffnessLow)),
            exit = fadeOut() + shrinkVertically(clip = false, shrinkTowards = Alignment.Top),
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.padding(12.dp),
            ) {
                FilterItemGroup(
                    title = stringResource(Res.string.filter_label_category),
                    items = remember(tags) { tags.filter { it.type == FilterItemType.Category } },
                    toggle = toggleItem,
                )
                FilterItemGroup(
                    title = stringResource(Res.string.filter_label_level),
                    items = remember(tags) { tags.filter { it.type == FilterItemType.Level } },
                    toggle = toggleItem,
                )
                FilterItemGroup(
                    title = stringResource(Res.string.filter_label_session_format),
                    items = remember(tags) { tags.filter { it.type == FilterItemType.Format } },
                    toggle = toggleItem,
                )
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
        Text(
            text = title,
            style = KotlinConfTheme.typography.text2,
            color = KotlinConfTheme.colors.noteText,
            modifier = Modifier.semantics {
                heading()
            }
        )
        FlowRow(
            modifier = Modifier.selectableGroup(),
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
        val tags = remember {
            mutableStateListOf(
                FilterItem(FilterItemType.Category, "Server-side", false),
                FilterItem(FilterItemType.Category, "Multiplatform", false),
                FilterItem(FilterItemType.Category, "Android", false),
                FilterItem(FilterItemType.Category, "Extensibility/Tooling", false),
                FilterItem(FilterItemType.Category, "Language and Best Practices", false),
                FilterItem(FilterItemType.Category, "Other", false),
                FilterItem(FilterItemType.Level, "Introductory and overview", false),
                FilterItem(FilterItemType.Level, "Intermediate", false),
                FilterItem(FilterItemType.Level, "Advanced", false),
                FilterItem(FilterItemType.Format, "Workshop", false),
                FilterItem(FilterItemType.Format, "Regular session", false),
                FilterItem(FilterItemType.Format, "Lightning session", false),
            )
        }

        Filters(
            tags = tags,
            toggleItem = { item, value ->
                val newItem = item.copy(isSelected = value)
                tags.replace(item, newItem)
            },
            modifier = Modifier.height(300.dp),
        )
    }
}

private fun <T> MutableList<T>.replace(old: T, new: T) {
    val index = indexOf(old)
    if (index >= 0) {
        set(index, new)
    }
}
