package org.jetbrains.kotlinconf.screens

import androidx.compose.runtime.Composable
import kotlinconfapp.shared.generated.resources.Res
import kotlinconfapp.shared.generated.resources.app_terms
import kotlinconfapp.shared.generated.resources.app_terms_header
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.kotlinconf.MarkdownScreenWithTitle

@Composable
fun AppTermsOfUse(
    onBack: () -> Unit,
    onAppPrivacyNotice: () -> Unit,
) {
    MarkdownScreenWithTitle(
        title = stringResource(Res.string.app_terms),
        header = stringResource(Res.string.app_terms_header),
        loadText = {
            @OptIn(ExperimentalResourceApi::class)
            Res.readBytes("files/app-terms.md")
        },
        onBack = onBack,
        onCustomUriClick = { uri ->
            if (uri == "app-privacy-notice.md") {
                onAppPrivacyNotice()
            }
        }
    )
}
