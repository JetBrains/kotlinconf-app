package org.jetbrains.kotlinconf.ui

import androidx.compose.runtime.Composable
import kotlinconfapp.shared.generated.resources.Res
import kotlinconfapp.shared.generated.resources.privacy_policy_for_visitors_title
import kotlinconfapp.shared.generated.resources.privacy_policy_for_visitors_version
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.jetbrains.kotlinconf.ui.components.MarkdownScreenWithTitle

@OptIn(ExperimentalResourceApi::class)
@Composable
@Preview
fun VisitorsPrivacyPolicyScreen() {
    MarkdownScreenWithTitle(
        stringResource(Res.string.privacy_policy_for_visitors_title),
        stringResource(Res.string.privacy_policy_for_visitors_version),
        "files/visitors-privacy-policy.md",
        showCloseButton = false,
        onClose = {}
    )
}

