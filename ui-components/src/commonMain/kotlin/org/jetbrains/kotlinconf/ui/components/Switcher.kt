package org.jetbrains.kotlinconf.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.modifier.modifierLocalConsumer
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.jetbrains.kotlinconf.ui.theme.PreviewHelper

@Composable
fun Switcher(
    items: List<String>,
    selectedIndex: Int,
    onSelect: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
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
private fun SwitcherPreview() {
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
