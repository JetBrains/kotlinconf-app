package org.jetbrains.kotlinconf.screens

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.kotlinconf.MarkdownScreenWithTitle
import org.jetbrains.kotlinconf.generated.resources.Res
import org.jetbrains.kotlinconf.generated.resources.general_terms
import org.jetbrains.kotlinconf.generated.resources.visitors_terms_title
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

@Composable
fun VisitorTermsOfUse(
    onBack: () -> Unit,
    onCodeOfConduct: () -> Unit,
    onVisitorPrivacyNotice: () -> Unit,
) {
    val viewModel = koinViewModel<DocumentsViewModel> { parametersOf("documents/visitors-terms.md") }
    val state by viewModel.state.collectAsStateWithLifecycle()
    MarkdownScreenWithTitle(
        title = stringResource(Res.string.general_terms),
        header = stringResource(Res.string.visitors_terms_title),
        documentState = state,
        onBack = onBack,
        onReload = { viewModel.refresh() },
        onCustomUriClick = { uri ->
            when (uri) {
                "code-of-conduct.md" -> onCodeOfConduct()
                "visitors-privacy-notice.md" -> onVisitorPrivacyNotice()
            }
        },
    )
}
