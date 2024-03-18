package org.jetbrains.kotlinconf.ui

import androidx.compose.runtime.Composable
import kotlinconfapp.shared.generated.resources.Res
import kotlinconfapp.shared.generated.resources.app_terms_of_use
import kotlinconfapp.shared.generated.resources.app_terms_of_use_version
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.jetbrains.kotlinconf.ui.components.MarkdownScreenWithTitle

@OptIn(ExperimentalResourceApi::class)
@Composable
@Preview
fun AppTermsOfUseScreen() {
    MarkdownScreenWithTitle(
        stringResource(Res.string.app_terms_of_use),
        stringResource(Res.string.app_terms_of_use_version),
        "files/app-terms.md",
        showCloseButton = false,
        onClose = {}
    )
}

