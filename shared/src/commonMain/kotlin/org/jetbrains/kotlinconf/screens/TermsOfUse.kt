package org.jetbrains.kotlinconf.screens

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinconfapp.shared.generated.resources.Res
import kotlinconfapp.shared.generated.resources.general_terms
import kotlinconfapp.shared.generated.resources.visitors_terms_title
import kotlinconfapp.shared.generated.resources.visitors_terms_version
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.kotlinconf.MarkdownScreenWithTitle

@Composable
fun TermsOfUse(onBack: () -> Unit) {
    MarkdownScreenWithTitle(
        title = stringResource(Res.string.general_terms),
        header = stringResource(Res.string.visitors_terms_title),
        subheader = stringResource(Res.string.visitors_terms_version),
        loadText = {
            @OptIn(ExperimentalResourceApi::class)
            Res.readBytes("files/visitors-terms.md")
        },
        onBack = onBack
    ) {
        Spacer(Modifier.height(24.dp))
    }
}
