package org.jetbrains.kotlinconf.ui.components

import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.jetbrains.kotlinconf.ui.theme.KotlinConfTheme
import org.jetbrains.kotlinconf.ui.theme.PreviewHelper

@Composable
fun EmptySection(
    text: String,
    modifier: Modifier = Modifier,
) {
    StyledText(
        text = text,
        style = KotlinConfTheme.typography.text1,
        color = KotlinConfTheme.colors.noteText,
        modifier = modifier.padding(12.dp),
    )
}

@Preview
@Composable
internal fun EmptySectionPreview() {
    PreviewHelper {
        EmptySection("No bookmarks added here")
    }
}
