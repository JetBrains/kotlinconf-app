package org.jetbrains.kotlinconf.ui

import androidx.compose.runtime.Composable
import kotlinconfapp.shared.generated.resources.Res
import kotlinconfapp.shared.generated.resources.visitors_terms
import kotlinconfapp.shared.generated.resources.visitors_terms_version
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.jetbrains.kotlinconf.ui.components.MarkdownScreenWithTitle

@OptIn(ExperimentalResourceApi::class)
@Composable
@Preview
fun VisitorsTermsScreen() {
    MarkdownScreenWithTitle(
        stringResource(Res.string.visitors_terms),
        stringResource(Res.string.visitors_terms_version),
        "files/visitors-terms.md",
        showCloseButton = false,
        onClose = {}
    )
}
