package org.jetbrains.kotlinconf.screens

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.kotlinconf.MarkdownScreenWithTitle
import org.jetbrains.kotlinconf.generated.resources.Res
import org.jetbrains.kotlinconf.generated.resources.privacy_notice_for_visitors
import org.jetbrains.kotlinconf.generated.resources.privacy_notice_for_visitors_title
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

@Composable
fun VisitorPrivacyNotice(onBack: () -> Unit) {
    val viewModel = koinViewModel<DocumentsViewModel> { parametersOf("documents/visitors-privacy-notice.md") }
    val state by viewModel.state.collectAsStateWithLifecycle()
    MarkdownScreenWithTitle(
        title = stringResource(Res.string.privacy_notice_for_visitors),
        header = stringResource(Res.string.privacy_notice_for_visitors_title),
        documentState = state,
        onBack = onBack,
        onReload = { viewModel.refresh() },
    )
}
