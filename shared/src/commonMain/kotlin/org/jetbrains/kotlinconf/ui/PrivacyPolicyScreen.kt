package org.jetbrains.kotlinconf.ui

import androidx.compose.runtime.Composable
import kotlinconfapp.shared.generated.resources.Res
import kotlinconfapp.shared.generated.resources.privacy_policy_for_app_title
import kotlinconfapp.shared.generated.resources.privacy_policy_for_app_version
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.kotlinconf.ui.components.MarkdownScreenWithTitle

@OptIn(ExperimentalResourceApi::class)
@Composable
fun AppPrivacyPolicyScreen(showCloseButton: Boolean, onClose: () -> Unit) {
    MarkdownScreenWithTitle(
        title = stringResource(Res.string.privacy_policy_for_app_title),
        subtitle = stringResource(Res.string.privacy_policy_for_app_version),
        markdownFile = "files/app-privacy-policy.md",
        showCloseButton = showCloseButton,
        onClose = onClose
    )
}
