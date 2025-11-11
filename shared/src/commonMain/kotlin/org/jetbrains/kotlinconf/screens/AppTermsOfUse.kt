package org.jetbrains.kotlinconf.screens

import androidx.compose.runtime.Composable
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.kotlinconf.MarkdownScreenWithTitle
import org.jetbrains.kotlinconf.generated.resources.Res
import org.jetbrains.kotlinconf.generated.resources.app_terms
import org.jetbrains.kotlinconf.generated.resources.app_terms_header

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
