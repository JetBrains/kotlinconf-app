package org.jetbrains.kotlinconf.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinconfapp.shared.generated.resources.Res
import kotlinconfapp.shared.generated.resources.code_of_conduct
import kotlinconfapp.shared.generated.resources.kodee_code_of_conduct
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.kotlinconf.MarkdownScreenWithTitle

@OptIn(ExperimentalResourceApi::class)
@Composable
fun CodeOfConduct(onBack: () -> Unit) {
    MarkdownScreenWithTitle(
        title = stringResource(Res.string.code_of_conduct),
        header = stringResource(Res.string.code_of_conduct),
        loadText = { Res.readBytes("files/code-of-conduct.md") },
        onBack = onBack,
    ) {
        Image(
            painter = painterResource(Res.drawable.kodee_code_of_conduct),
            contentDescription = null,
            modifier = Modifier.align(Alignment.CenterHorizontally).padding(bottom = 12.dp)
        )
    }
}
