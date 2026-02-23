package org.jetbrains.kotlinconf.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import org.jetbrains.kotlinconf.ui.theme.Brand
import org.jetbrains.kotlinconf.ui.theme.KotlinConfTheme
import org.jetbrains.kotlinconf.ui.theme.PreviewHelper
import org.jetbrains.kotlinconf.ui.utils.PreviewLightDark
import org.jetbrains.kotlinconf.ui.utils.WidePreviewLightDark

private val LargeSwitcherItemShape = RoundedCornerShape(percent = 100)

@Composable
private fun LargeSwitcherItem(
    label1: String,
    label2: String,
    onClick: () -> Unit,
    selected: Boolean,
    modifier: Modifier = Modifier,
) {
    val gradientAlpha by animateFloatAsState(
        if (selected) 1f else 0f,
    )

    val textColor by animateColorAsState(
        if (selected) KotlinConfTheme.colors.primaryTextWhiteFixed
        else KotlinConfTheme.colors.secondaryText,
    )

    Row(
        modifier = modifier
            .fillMaxWidth()
            .heightIn(min = 48.dp)
            .clip(LargeSwitcherItemShape)
            .selectable(
                selected = selected,
                onClick = { onClick() },
                role = Role.Tab
            )
            .background(
                Brand.colorGradient,
                alpha = gradientAlpha,
            )
            .background(KotlinConfTheme.colors.tileBackground)
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp, Alignment.CenterHorizontally),
    ) {
        Text(
            text = label1,
            style = KotlinConfTheme.typography.text1,
            color = textColor,
        )
        Text(
            text = label2,
            style = KotlinConfTheme.typography.h3,
            color = textColor,
        )
    }
}

data class LargeSwitcherOption(val label1: String, val label2: String)

@Composable
fun LargeSwitcher(
    options: List<LargeSwitcherOption>,
    selectedIndex: Int,
    onSelect: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .selectableGroup(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        options.forEachIndexed { index, option ->
            LargeSwitcherItem(
                label1 = option.label1,
                label2 = option.label2,
                onClick = { onSelect(index) },
                selected = index == selectedIndex,
                modifier = Modifier.weight(1f),
            )
        }
    }
}

@PreviewLightDark
@Composable
private fun LargeSwitcherItemUnselectedPreview() = PreviewHelper {
    LargeSwitcherItem("May 21", "Conference day 1", {}, selected = false)
}

@PreviewLightDark
@Composable
private fun LargeSwitcherItemSelectedPreview() = PreviewHelper {
    LargeSwitcherItem("May 22", "Conference day 2", {}, selected = true)
}


@Composable
@WidePreviewLightDark
private fun LargeSwitcherPreview() = PreviewHelper {
    var selectedIndex by remember { mutableIntStateOf(0) }
    LargeSwitcher(
        options = listOf(
            LargeSwitcherOption("May 21", "Workshop day"),
            LargeSwitcherOption("May 22", "Conference day 1"),
            LargeSwitcherOption("May 23", "Conference day 2"),
        ),
        selectedIndex = selectedIndex,
        onSelect = { selectedIndex = it },
    )
}
