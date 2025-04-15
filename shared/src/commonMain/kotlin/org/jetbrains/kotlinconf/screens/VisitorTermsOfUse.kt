package org.jetbrains.kotlinconf.screens

import androidx.compose.runtime.Composable
import kotlinconfapp.shared.generated.resources.Res
import kotlinconfapp.shared.generated.resources.general_terms
import kotlinconfapp.shared.generated.resources.visitors_terms_title
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.kotlinconf.MarkdownScreenWithTitle

@Composable
fun VisitorTermsOfUse(
    onBack: () -> Unit,
    onCodeOfConduct: () -> Unit,
    onVisitorPrivacyNotice: () -> Unit,
) {
    MarkdownScreenWithTitle(
        title = stringResource(Res.string.general_terms),
        header = stringResource(Res.string.visitors_terms_title),
        loadText = {
            @OptIn(ExperimentalResourceApi::class)
            Res.readBytes("files/visitors-terms.md")
        },
        onBack = onBack,
        onCustomUriClick = { uri ->
            when (uri) {
                "code-of-conduct.md" -> onCodeOfConduct()
                "visitors-privacy-notice.md" -> onVisitorPrivacyNotice()
            }
        },
    )
}
