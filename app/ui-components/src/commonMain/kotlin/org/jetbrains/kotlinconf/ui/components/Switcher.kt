package org.jetbrains.kotlinconf.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import org.jetbrains.kotlinconf.ui.theme.KotlinConfTheme
import org.jetbrains.kotlinconf.ui.theme.sharedElementModifier
import org.jetbrains.kotlinconf.ui.theme.PreviewHelper
import org.jetbrains.kotlinconf.ui.utils.PreviewLightDark

private val SwitcherItemShape = RoundedCornerShape(percent = 50)

@Composable
private fun SwitcherItem(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
    onLayout: (hasOverflow: Boolean) -> Unit,
    modifier: Modifier = Modifier,
    sharedTransitionKey: String? = null,
    index: Int = 0,
) {
    val containerMod = if (sharedTransitionKey != null) {
        sharedElementModifier("$sharedTransitionKey-$index-container")
    } else Modifier

    val textMod = if (sharedTransitionKey != null) {
        sharedElementModifier("$sharedTransitionKey-$index-text")
    } else Modifier

    val backgroundColor by animateColorAsState(
        if (selected) Color.Transparent
        else KotlinConfTheme.colors.tileBackground,
    )
    val textColor by animateColorAsState(
        if (selected) KotlinConfTheme.colors.primaryText
        else KotlinConfTheme.colors.secondaryText,
    )
    val strokeColor by animateColorAsState(
        if (selected) KotlinConfTheme.colors.strokeAccent
        else Color.Transparent,
    )

    Box(
        modifier = modifier
            .then(containerMod)
            .heightIn(min = 40.dp)
            .clip(SwitcherItemShape)
            .border(
                width = 2.dp,
                color = strokeColor,
                shape = SwitcherItemShape,
            )
            .selectable(
                selected = selected,
                onClick = { onClick() },
                role = Role.Tab
            )
            .background(backgroundColor)
            .padding(horizontal = 16.dp, vertical = 8.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            label,
            style = KotlinConfTheme.typography.text1,
            color = textColor,
            maxLines = 1,
            onTextLayout = { result ->
                onLayout.invoke(result.hasVisualOverflow)
            },
            modifier = textMod,
        )
    }
}

@PreviewLightDark
@Composable
private fun SwitcherItemNormalPreview() = PreviewHelper {
    SwitcherItem("Normal item", selected = false, {}, {})
}

@PreviewLightDark
@Composable
private fun SwitcherItemSelectedPreview() = PreviewHelper {
    SwitcherItem("Selected item", selected = true, {}, {})
}

@Composable
fun Switcher(
    items: List<String>,
    shortItems: List<String>?,
    selectedIndex: Int,
    onSelect: (Int) -> Unit,
    modifier: Modifier = Modifier,
    sharedTransitionKey: String? = null,
) {
    BoxWithConstraints(modifier = modifier) {
        var useShortItems by remember(items, constraints.maxWidth) { mutableStateOf(false) }
        val displayItems = if (useShortItems && shortItems != null) shortItems else items

        Row(
            modifier = Modifier.selectableGroup(),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            displayItems.forEachIndexed { index, label ->
                SwitcherItem(
                    label = label,
                    selected = index == selectedIndex,
                    onClick = { onSelect(index) },
                    onLayout = { hasOverflow ->
                        if (hasOverflow) useShortItems = true
                    },
                    modifier = Modifier.weight(1f),
                    sharedTransitionKey = sharedTransitionKey,
                    index = index,
                )
            }
        }
    }
}

@Composable
@PreviewLightDark
private fun SwitcherShortPreview() = PreviewHelper {
    var selectedIndex by remember { mutableIntStateOf(0) }
    Switcher(
        items = listOf("May 21", "May 22", "May 23"),
        shortItems = listOf("M21", "M22", "M23"),
        selectedIndex = selectedIndex,
        onSelect = { selectedIndex = it },
        modifier = Modifier.width(230.dp),
    )
}


@Composable
@PreviewLightDark
private fun SwitcherPreview() = PreviewHelper {
    var selectedIndex by remember { mutableIntStateOf(0) }
    Switcher(
        items = listOf("May 21", "May 22", "May 23"),
        shortItems = listOf("M21", "M22", "M23"),
        selectedIndex = selectedIndex,
        onSelect = { selectedIndex = it },
        modifier = Modifier.width(300.dp),
    )
}
