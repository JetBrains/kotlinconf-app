package org.jetbrains.kotlinconf.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.jetbrains.kotlinconf.ui.theme.KotlinConfTheme
import org.jetbrains.kotlinconf.ui.theme.PreviewHelper

enum class TopMenuTitleState {
    Header, Completed, Placeholder
}

@Composable
fun TopMenuTitle(
    text: String,
    state: TopMenuTitleState,
    modifier: Modifier = Modifier,
) {
    StyledText(
        text = text,
        modifier = modifier,
        style = when (state) {
            TopMenuTitleState.Header ->
                KotlinConfTheme.typography.h3

            TopMenuTitleState.Completed,
            TopMenuTitleState.Placeholder ->
                KotlinConfTheme.typography.text1
        },
        color = when (state) {
            TopMenuTitleState.Header,
            TopMenuTitleState.Completed ->
                KotlinConfTheme.colors.primaryText

            TopMenuTitleState.Placeholder ->
                KotlinConfTheme.colors.placeholderText
        },
    )
}

@Preview
@Composable
internal fun TopMenuTitlePreview() {
    PreviewHelper {
        TopMenuTitle("Top Menu Title", TopMenuTitleState.Header)
        TopMenuTitle("Top Menu Title", TopMenuTitleState.Completed)
        TopMenuTitle("Top Menu Title", TopMenuTitleState.Placeholder)
    }
}
