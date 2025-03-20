package org.jetbrains.kotlinconf.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextLinkStyles
import com.mikepenz.markdown.compose.Markdown
import com.mikepenz.markdown.model.DefaultMarkdownColors
import com.mikepenz.markdown.model.DefaultMarkdownTypography
import org.jetbrains.kotlinconf.ui.theme.KotlinConfTheme

@Composable
fun MarkdownView(
    loadText: suspend () -> ByteArray,
    modifier: Modifier = Modifier,
) {
    var text by remember { mutableStateOf("") }
    LaunchedEffect(loadText) {
        text = loadText().decodeToString()
    }

    MarkdownView(text, modifier)
}

@Composable
fun MarkdownView(
    text: String,
    modifier: Modifier = Modifier,
) {
    Markdown(
        text, DefaultMarkdownColors(
            text = KotlinConfTheme.colors.longText,
            codeText = KotlinConfTheme.colors.secondaryText,
            linkText = KotlinConfTheme.colors.primaryText,
            codeBackground = KotlinConfTheme.colors.mainBackground,
            inlineCodeText = KotlinConfTheme.colors.secondaryText,
            inlineCodeBackground = KotlinConfTheme.colors.mainBackground,
            dividerColor = KotlinConfTheme.colors.strokePale,
            tableText = KotlinConfTheme.colors.longText,
            tableBackground = KotlinConfTheme.colors.mainBackground,
        ), typography = DefaultMarkdownTypography(
            text = KotlinConfTheme.typography.text1,
            code = KotlinConfTheme.typography.text1,
            h1 = KotlinConfTheme.typography.h1,
            h2 = KotlinConfTheme.typography.h2,
            h3 = KotlinConfTheme.typography.h3,
            h4 = KotlinConfTheme.typography.h4,
            h5 = KotlinConfTheme.typography.h4,
            h6 = KotlinConfTheme.typography.h4,
            quote = KotlinConfTheme.typography.text2,
            paragraph = KotlinConfTheme.typography.text1,
            ordered = KotlinConfTheme.typography.text1,
            bullet = KotlinConfTheme.typography.text1,
            list = KotlinConfTheme.typography.text1,
            link = KotlinConfTheme.typography.text1,
            inlineCode = KotlinConfTheme.typography.text1,
            textLink = TextLinkStyles(),
        ),
        modifier = modifier
    )
}
