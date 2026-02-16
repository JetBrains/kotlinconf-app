package org.jetbrains.kotlinconf.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import org.jetbrains.kotlinconf.ui.theme.KotlinConfTheme
import org.jetbrains.kotlinconf.ui.theme.PreviewHelper
import org.jetbrains.kotlinconf.ui.utils.PreviewLightDark

@Composable
fun SectionTitle(
    text: String,
    modifier: Modifier = Modifier,
) {
    Text(
        text = text,
        modifier = modifier,
        style = KotlinConfTheme.typography.h2,
        color = KotlinConfTheme.colors.primaryText,
    )
}

@PreviewLightDark
@Composable
private fun SectionTitleTextPreview() = PreviewHelper {
    SectionTitle("Section title")
}

@PreviewLightDark
@Composable
private fun SectionTitleTimePreview() = PreviewHelper {
    SectionTitle("7:30")
}
