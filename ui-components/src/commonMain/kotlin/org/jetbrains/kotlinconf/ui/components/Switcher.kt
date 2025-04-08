package org.jetbrains.kotlinconf.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.jetbrains.kotlinconf.ui.theme.KotlinConfTheme
import org.jetbrains.kotlinconf.ui.theme.PreviewHelper

private val SwitcherItemShape = RoundedCornerShape(percent = 50)

@Composable
private fun SwitcherItem(
    label: String,
    onClick: () -> Unit,
    selected: Boolean,
    modifier: Modifier = Modifier,
) {
    val backgroundColor by animateColorAsState(
        if (selected) Color.Transparent
        else KotlinConfTheme.colors.tileBackground,
    )
    val textColor by animateColorAsState(
        if (selected) KotlinConfTheme.colors.primaryText
        else KotlinConfTheme.colors.secondaryText,
    )
    val strokeColor by animateColorAsState(
        if (selected) KotlinConfTheme.colors.strokeFull
        else Color.Transparent,
    )

    Box(
        modifier = modifier
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
        )
    }
}

@Preview
@Composable
internal fun SwitcherItemPreview() {
    PreviewHelper {
        SwitcherItem("Normal item", {}, selected = false)
        SwitcherItem("Selected item", {}, selected = true)
    }
}

@Composable
fun Switcher(
    items: List<String>,
    selectedIndex: Int,
    onSelect: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.selectableGroup(),
        horizontalArrangement = Arrangement
            .spacedBy(4.dp)
    ) {
        items.forEachIndexed { index, label ->
            SwitcherItem(
                label = label,
                onClick = { onSelect(index) },
                selected = index == selectedIndex,
                modifier = Modifier.weight(1f),
            )
        }
    }
}

@Composable
@Preview
internal fun SwitcherPreview() {
    PreviewHelper {
        var selectedIndex by remember { mutableIntStateOf(0) }
        Switcher(
            items = listOf("May 21", "May 22", "May 23"),
            selectedIndex = selectedIndex,
            onSelect = { selectedIndex = it },
            modifier = Modifier.width(300.dp),
        )
    }
}
