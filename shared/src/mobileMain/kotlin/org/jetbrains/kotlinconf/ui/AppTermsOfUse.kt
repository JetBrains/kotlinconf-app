package org.jetbrains.kotlinconf.ui

import androidx.compose.runtime.Composable
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.jetbrains.kotlinconf.ui.components.MarkdownScreenWithTitle

@Composable
@Preview
fun AppTermsOfUseScreen() {
    MarkdownScreenWithTitle(
        "KotlinConf App Terms of Use",
        "Version 1.1, effective as of February 28, 2024",
        "files/app-terms.md",
        showCloseButton = false,
        onClose = {}
    )
}

