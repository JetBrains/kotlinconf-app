package org.jetbrains.kotlinconf.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.jetbrains.kotlinconf.ui.theme.KotlinConfTheme
import org.jetbrains.kotlinconf.ui.theme.PreviewHelper

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

@Preview
@Composable
internal fun SectionTitlePreview() {
    PreviewHelper {
        SectionTitle("Section title")
        SectionTitle("7:30")
    }
}
