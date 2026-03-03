package org.jetbrains.kotlinconf.screens

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dev.zacsweers.metrox.viewmodel.assistedMetroViewModel
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.kotlinconf.MarkdownScreenWithTitle
import org.jetbrains.kotlinconf.generated.resources.Res
import org.jetbrains.kotlinconf.generated.resources.privacy_notice_for_visitors
import org.jetbrains.kotlinconf.generated.resources.privacy_notice_for_visitors_title

@Composable
fun VisitorPrivacyNotice(onBack: () -> Unit) {
    val viewModel = assistedMetroViewModel<DocumentsViewModel, DocumentsViewModel.Factory> {
        create("documents/visitors-privacy-notice.md")
    }
    val state by viewModel.state.collectAsStateWithLifecycle()
    MarkdownScreenWithTitle(
        title = stringResource(Res.string.privacy_notice_for_visitors),
        header = stringResource(Res.string.privacy_notice_for_visitors_title),
        documentState = state,
        onBack = onBack,
        onReload = { viewModel.refresh() },
    )
}
