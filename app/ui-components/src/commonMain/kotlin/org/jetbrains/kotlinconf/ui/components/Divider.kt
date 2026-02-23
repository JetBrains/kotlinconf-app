package org.jetbrains.kotlinconf.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import org.jetbrains.kotlinconf.ui.theme.KotlinConfTheme
import org.jetbrains.kotlinconf.ui.theme.PreviewHelper
import org.jetbrains.kotlinconf.ui.utils.PreviewLightDark

@Composable
fun HorizontalDivider(
    thickness: Dp,
    color: Color,
) {
    Canvas(Modifier.fillMaxWidth().height(thickness)) {
        drawLine(
            color = color,
            strokeWidth = thickness.toPx(),
            start = Offset(0f, thickness.toPx() / 2),
            end = Offset(size.width, thickness.toPx() / 2),
        )
    }
}

@Composable
fun VerticalDivider(
    thickness: Dp,
    color: Color,
) {
    Canvas(Modifier.fillMaxHeight().width(thickness)) {
        drawLine(
            color = color,
            strokeWidth = thickness.toPx(),
            start = Offset(thickness.toPx() / 2, 0f),
            end = Offset(thickness.toPx() / 2, size.height),
        )
    }
}

@PreviewLightDark
@Composable
private fun HorizontalDividerPreview() = PreviewHelper(Modifier.width(100.dp)) {
    HorizontalDivider(thickness = 1.dp, color = KotlinConfTheme.colors.strokePale)
}

@PreviewLightDark
@Composable
private fun VerticalDividerPreview() = PreviewHelper(Modifier.height(100.dp)) {
    VerticalDivider(thickness = 1.dp, color = KotlinConfTheme.colors.strokePale)
}
