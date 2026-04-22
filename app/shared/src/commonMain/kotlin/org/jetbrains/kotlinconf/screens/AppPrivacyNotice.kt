package org.jetbrains.kotlinconf.screens

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.kotlinconf.MarkdownScreenWithTitle
import org.jetbrains.kotlinconf.generated.resources.Res
import org.jetbrains.kotlinconf.generated.resources.app_privacy_notice_header
import org.jetbrains.kotlinconf.generated.resources.app_privacy_notice_title
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

@Composable
fun AppPrivacyNotice(
    onBack: () -> Unit,
    onAppTermsOfUse: () -> Unit,
) {
    val viewModel = koinViewModel<DocumentsViewModel>{ parametersOf("documents/app-privacy-notice.md") }
    val state by viewModel.state.collectAsStateWithLifecycle()
    MarkdownScreenWithTitle(
        title = stringResource(Res.string.app_privacy_notice_title),
        header = stringResource(Res.string.app_privacy_notice_header),
        documentState = state,
        onBack = onBack,
        onReload = { viewModel.refresh() },
        onCustomUriClick = { uri ->
            if (uri == "app-terms.md") {
                onAppTermsOfUse()
            }
        },
    )
}
