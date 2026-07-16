package org.jetbrains.kotlinconf.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import org.jetbrains.kotlinconf.ui.theme.KotlinConfTheme
import org.jetbrains.kotlinconf.ui.utils.ComponentPreview

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

@ComponentPreview
@Composable
private fun SectionTitleTextPreview() {
    SectionTitle("Section title")
}

@ComponentPreview
@Composable
private fun SectionTitleTimePreview() {
    SectionTitle("7:30")
}
