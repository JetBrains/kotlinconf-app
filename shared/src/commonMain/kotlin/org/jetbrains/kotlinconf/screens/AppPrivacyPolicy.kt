package org.jetbrains.kotlinconf.screens

import androidx.compose.runtime.Composable
import kotlinconfapp.shared.generated.resources.Res
import kotlinconfapp.shared.generated.resources.app_privacy_policy_header
import kotlinconfapp.shared.generated.resources.app_privacy_policy_title
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.kotlinconf.MarkdownScreenWithTitle

@OptIn(ExperimentalResourceApi::class)
@Composable
fun AppPrivacyPolicy(
    onBack: () -> Unit,
    onAppTermsOfUse: () -> Unit,
) {
    MarkdownScreenWithTitle(
        title = stringResource(Res.string.app_privacy_policy_title),
        header = stringResource(Res.string.app_privacy_policy_header),
        loadText = { Res.readBytes("files/app-privacy-policy.md") },
        onBack = onBack,
        onCustomUriClick = { uri ->
            if (uri == "app-terms.md") {
                onAppTermsOfUse()
            }
        },
    )
}
