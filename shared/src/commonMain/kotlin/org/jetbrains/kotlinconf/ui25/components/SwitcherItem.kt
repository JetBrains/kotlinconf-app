package org.jetbrains.kotlinconf.ui25.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.jetbrains.kotlinconf.ui25.theme.KotlinConfTheme
import org.jetbrains.kotlinconf.ui25.theme.PreviewHelper


enum class SwitcherItemState {
    Active, Normal, Selected,
}

@Composable
fun SwitcherItem(
    label: String,
    onClick: () -> Unit,
    state: SwitcherItemState,
    modifier: Modifier = Modifier,
) {
    val backgroundColor = when (state) {
        SwitcherItemState.Active -> KotlinConfTheme.colors.activeBackground
        SwitcherItemState.Normal -> KotlinConfTheme.colors.tileBackground
        SwitcherItemState.Selected -> null
    }
    val textColor = when (state) {
        SwitcherItemState.Active -> KotlinConfTheme.colors.primaryText
        SwitcherItemState.Normal -> KotlinConfTheme.colors.secondaryText
        SwitcherItemState.Selected -> KotlinConfTheme.colors.primaryText
    }
    val shape = remember { RoundedCornerShape(percent = 50) }

    Box(
        modifier = modifier
            .heightIn(min = 40.dp)
            .clip(shape)
            .then(
                when (state) {
                    SwitcherItemState.Selected -> Modifier.border(
                        width = 2.dp,
                        color = KotlinConfTheme.colors.strokeFull,
                        shape = shape,
                    )

                    else -> Modifier
                }
            )
            .clickable(onClick = onClick)
            .then(
                if (backgroundColor != null) Modifier.background(backgroundColor) else Modifier
            )
            .padding(horizontal = 16.dp, vertical = 8.dp),
        contentAlignment = Alignment.Center,
    ) {
        KCText(
            label,
            style = KotlinConfTheme.typography.text2,
            color = textColor,
        )
    }
}

@Preview
@Composable
fun SwitcherItemPreview() {
    PreviewHelper {
        SwitcherItem("Normal item", {}, SwitcherItemState.Normal)
        SwitcherItem("Active item", {}, SwitcherItemState.Active)
        SwitcherItem("Selected item", {}, SwitcherItemState.Selected)
    }
}
