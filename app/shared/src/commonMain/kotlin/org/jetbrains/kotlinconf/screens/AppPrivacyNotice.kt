package org.jetbrains.kotlinconf.screens

import androidx.compose.runtime.Composable
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.kotlinconf.MarkdownScreenWithTitle
import org.jetbrains.kotlinconf.generated.resources.Res
import org.jetbrains.kotlinconf.generated.resources.app_privacy_notice_header
import org.jetbrains.kotlinconf.generated.resources.app_privacy_notice_title

@OptIn(ExperimentalResourceApi::class)
@Composable
fun AppPrivacyNotice(
    onBack: () -> Unit,
    onAppTermsOfUse: () -> Unit,
) {
    MarkdownScreenWithTitle(
        title = stringResource(Res.string.app_privacy_notice_title),
        header = stringResource(Res.string.app_privacy_notice_header),
        loadText = { Res.readBytes("files/app-privacy-notice.md") },
        onBack = onBack,
        onCustomUriClick = { uri ->
            if (uri == "app-terms.md") {
                onAppTermsOfUse()
            }
        },
    )
}
