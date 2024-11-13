package org.jetbrains.kotlinconf.ui25.components

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.jetbrains.kotlinconf.ui25.theme.KotlinConfTheme
import org.jetbrains.kotlinconf.ui25.theme.PreviewHelper

@Composable
fun SectionTitle(
    text: String,
    modifier: Modifier = Modifier,
) {
    KCText(
        text = text,
        modifier = modifier,
        style = KotlinConfTheme.typography.h2,
        color = KotlinConfTheme.colors.primaryText,
    )
}

@Preview
@Composable
private fun SectionTitlePreview() {
    PreviewHelper {
        SectionTitle("Section title")
        SectionTitle("7:30")
    }
}
