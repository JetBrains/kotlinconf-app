package org.jetbrains.kotlinconf.ui.components

import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.jetbrains.kotlinconf.ui.theme.KotlinConfTheme
import org.jetbrains.kotlinconf.ui.utils.ComponentPreview

@Composable
fun ParagraphTitle(
    text: String,
    modifier: Modifier = Modifier,
) {
    Text(
        text = text,
        style = KotlinConfTheme.typography.h2,
        modifier = modifier
            .padding(
                top = 24.dp,
                end = 12.dp,
                start = 12.dp,
                bottom = 16.dp,
            ),
    )
}

@ComponentPreview
@Composable
private fun ParagraphTitlePreview() {
    ParagraphTitle("Title")
}
