package org.jetbrains.kotlinconf.screens

import androidx.compose.runtime.Composable
import kotlinconfapp.shared.generated.resources.Res
import kotlinconfapp.shared.generated.resources.app_terms
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.kotlinconf.MarkdownScreenWithTitle

@Composable
fun AppTermsOfUse(
    onBack: () -> Unit,
    onAppPrivacyPolicy: () -> Unit,
) {
    MarkdownScreenWithTitle(
        title = stringResource(Res.string.app_terms),
        loadText = {
            @OptIn(ExperimentalResourceApi::class)
            Res.readBytes("files/app-terms.md")
        },
        onBack = onBack,
        onUriClick = { uri ->
            if (uri == "app-privacy-policy.md") {
                onAppPrivacyPolicy()
            }
        }
    )
}
