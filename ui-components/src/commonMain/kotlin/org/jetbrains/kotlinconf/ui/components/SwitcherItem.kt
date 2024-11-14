package org.jetbrains.kotlinconf.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.jetbrains.kotlinconf.ui.theme.KotlinConfTheme
import org.jetbrains.kotlinconf.ui.theme.PreviewHelper

private val SwitcherItemShape = RoundedCornerShape(percent = 50)

@Composable
fun SwitcherItem(
    label: String,
    onClick: () -> Unit,
    selected: Boolean,
    modifier: Modifier = Modifier,
) {
    val backgroundColor by animateColorAsState(if (selected) Color.Transparent else KotlinConfTheme.colors.tileBackground)
    val textColor by animateColorAsState(if (selected) KotlinConfTheme.colors.primaryText else KotlinConfTheme.colors.secondaryText)
    val strokeColor by animateColorAsState(if (selected) KotlinConfTheme.colors.strokeFull else Color.Transparent)

    Box(
        modifier = modifier
            .heightIn(max = 40.dp)
            .clip(SwitcherItemShape)
            .border(
                width = 2.dp,
                color = strokeColor,
                shape = SwitcherItemShape,
            )
            .clickable(onClick = onClick)
            .background(backgroundColor)
            .padding(horizontal = 16.dp, vertical = 8.dp),
        contentAlignment = Alignment.Center,
    ) {
        StyledText(
            label,
            style = KotlinConfTheme.typography.text1,
            color = textColor,
        )
    }
}

@Preview
@Composable
private fun SwitcherItemPreview() {
    PreviewHelper {
        SwitcherItem("Normal item", {}, selected = false)
        SwitcherItem("Selected item", {}, selected = true)
    }
}
