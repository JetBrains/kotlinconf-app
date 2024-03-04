package org.jetbrains.kotlinconf.ui

import androidx.compose.runtime.Composable
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.jetbrains.kotlinconf.ui.components.MarkdownScreenWithTitle

@Composable
@Preview
fun VisitorsTermsScreen() {
    MarkdownScreenWithTitle(
        "KotlinConf 2024 General Terms and Conditions for Visitors",
        "Version 1.0 of February 2, 2024",
        "files/visitors-terms.md",
        showCloseButton = false,
        onClose = {}
    )
}
