package org.jetbrains.kotlinconf.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dev.zacsweers.metrox.viewmodel.assistedMetroViewModel
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.kotlinconf.MarkdownScreenWithTitle
import org.jetbrains.kotlinconf.generated.resources.Res
import org.jetbrains.kotlinconf.generated.resources.code_of_conduct
import org.jetbrains.kotlinconf.generated.resources.kodee_code_of_conduct

@Composable
fun CodeOfConduct(onBack: () -> Unit) {
    val viewModel = assistedMetroViewModel<DocumentsViewModel, DocumentsViewModel.Factory> {
        create("documents/code-of-conduct.md")
    }
    val state by viewModel.state.collectAsStateWithLifecycle()
    MarkdownScreenWithTitle(
        title = stringResource(Res.string.code_of_conduct),
        header = stringResource(Res.string.code_of_conduct),
        documentState = state,
        onBack = onBack,
        onReload = { viewModel.refresh() },
    ) {
        Image(
            painter = painterResource(Res.drawable.kodee_code_of_conduct),
            contentDescription = null,
            modifier = Modifier.align(Alignment.CenterHorizontally).padding(bottom = 12.dp)
        )
    }
}
