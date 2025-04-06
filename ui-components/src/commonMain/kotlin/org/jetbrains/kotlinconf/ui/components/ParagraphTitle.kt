package org.jetbrains.kotlinconf.ui.components

import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.jetbrains.kotlinconf.ui.theme.KotlinConfTheme
import org.jetbrains.kotlinconf.ui.theme.PreviewHelper

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

@Preview
@Composable
internal fun ParagraphTitlePreview() {
    PreviewHelper {
        ParagraphTitle("Title")
    }
}
