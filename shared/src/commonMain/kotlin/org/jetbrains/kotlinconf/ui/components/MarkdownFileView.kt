package org.jetbrains.kotlinconf.ui.components

import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.mikepenz.markdown.compose.Markdown
import com.mikepenz.markdown.model.DefaultMarkdownColors
import com.mikepenz.markdown.model.DefaultMarkdownTypography
import kotlinconfapp.shared.generated.resources.Res
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.kotlinconf.ui.theme.blackWhite
import org.jetbrains.kotlinconf.ui.theme.greyGrey20
import org.jetbrains.kotlinconf.ui.theme.greyWhite

@OptIn(ExperimentalResourceApi::class)
@Composable
fun MarkdownFileView(file: String) {
    var text by remember { mutableStateOf("") }
    LaunchedEffect(file) {
        text = Res.readBytes(file).decodeToString()
    }

    Markdown(
        text, DefaultMarkdownColors(
            text = MaterialTheme.colors.greyWhite,
            codeText = MaterialTheme.colors.greyWhite,
            linkText = MaterialTheme.colors.blackWhite,
            codeBackground = MaterialTheme.colors.greyGrey20,
            inlineCodeBackground = MaterialTheme.colors.greyGrey20,
            dividerColor = MaterialTheme.colors.greyGrey20,
        ), typography = DefaultMarkdownTypography(
            text = MaterialTheme.typography.body2,
            code = MaterialTheme.typography.body2,
            h1 = MaterialTheme.typography.h1,
            h2 = MaterialTheme.typography.h2,
            h3 = MaterialTheme.typography.h3,
            h4 = MaterialTheme.typography.h4,
            h5 = MaterialTheme.typography.h5,
            h6 = MaterialTheme.typography.h6,
            quote = MaterialTheme.typography.body2,
            paragraph = MaterialTheme.typography.body2,
            ordered = MaterialTheme.typography.body2,
            bullet = MaterialTheme.typography.body2,
            list = MaterialTheme.typography.body2,
        ),
        modifier = Modifier.padding(horizontal = 16.dp, vertical = 24.dp)
    )
}
