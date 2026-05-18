package org.jetbrains.kotlinconf.screens

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dev.zacsweers.metrox.viewmodel.assistedMetroViewModel
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.kotlinconf.MarkdownScreenWithTitle
import org.jetbrains.kotlinconf.generated.resources.Res
import org.jetbrains.kotlinconf.generated.resources.app_terms
import org.jetbrains.kotlinconf.generated.resources.app_terms_header

@Composable
fun AppTermsOfUse(
    onBack: () -> Unit,
    onAppPrivacyNotice: () -> Unit,
) {
    val viewModel = assistedMetroViewModel<DocumentsViewModel, DocumentsViewModel.Factory> {
        create("documents/app-terms.md")
    }
    val state by viewModel.state.collectAsStateWithLifecycle()
    MarkdownScreenWithTitle(
        title = stringResource(Res.string.app_terms),
        header = stringResource(Res.string.app_terms_header),
        documentState = state,
        onBack = onBack,
        onReload = { viewModel.refresh() },
        onCustomUriClick = { uri ->
            if (uri == "app-privacy-notice.md") {
                onAppPrivacyNotice()
            }
        },
    )
}
