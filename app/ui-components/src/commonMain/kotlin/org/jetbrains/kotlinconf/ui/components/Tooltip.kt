package org.jetbrains.kotlinconf.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import org.jetbrains.kotlinconf.ui.theme.KotlinConfTheme
import org.jetbrains.kotlinconf.ui.theme.PreviewHelper
import org.jetbrains.kotlinconf.ui.utils.PreviewLightDark

@Composable
fun Tooltip(
    text: String,
) {
    Text(
        text = text,
        style = KotlinConfTheme.typography.text2,
        color = KotlinConfTheme.colors.primaryTextInverted,
        modifier = Modifier
            .clip(RoundedCornerShape(4.dp))
            .background(KotlinConfTheme.colors.tooltipBackground)
            .padding(vertical = 4.dp, horizontal = 8.dp)
    )
}

@PreviewLightDark
@Composable
private fun TooltipPreview() = PreviewHelper {
    Tooltip("Tooltip")
}
