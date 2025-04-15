package org.jetbrains.kotlinconf.screens

import androidx.compose.runtime.Composable
import kotlinconfapp.shared.generated.resources.Res
import kotlinconfapp.shared.generated.resources.privacy_notice_for_visitors
import kotlinconfapp.shared.generated.resources.privacy_notice_for_visitors_title
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.kotlinconf.MarkdownScreenWithTitle

@OptIn(ExperimentalResourceApi::class)
@Composable
fun VisitorPrivacyNotice(onBack: () -> Unit) {
    MarkdownScreenWithTitle(
        title = stringResource(Res.string.privacy_notice_for_visitors),
        header = stringResource(Res.string.privacy_notice_for_visitors_title),
        loadText = { Res.readBytes("files/visitors-privacy-notice.md") },
        onBack = onBack
    )
}
