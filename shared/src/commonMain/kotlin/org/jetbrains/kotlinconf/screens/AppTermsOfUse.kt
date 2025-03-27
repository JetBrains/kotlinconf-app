package org.jetbrains.kotlinconf.screens

import androidx.compose.runtime.Composable
import kotlinconfapp.shared.generated.resources.Res
import kotlinconfapp.shared.generated.resources.app_terms
import kotlinconfapp.shared.generated.resources.app_terms_title
import kotlinconfapp.shared.generated.resources.app_terms_version
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.kotlinconf.MarkdownScreenWithTitle

@Composable
fun AppTermsOfUse(onBack: () -> Unit) {
    MarkdownScreenWithTitle(
        title = stringResource(Res.string.app_terms),
        header = stringResource(Res.string.app_terms_title),
        subheader = stringResource(Res.string.app_terms_version),
        loadText = {
            @OptIn(ExperimentalResourceApi::class)
            Res.readBytes("files/app-terms.md")
        },
        onBack = onBack
    )
}
