package org.jetbrains.kotlinconf

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinconfapp.ui_components.generated.resources.Res
import kotlinconfapp.ui_components.generated.resources.arrow_left_24
import kotlinconfapp.ui_components.generated.resources.main_header_back
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.kotlinconf.ui.components.Divider
import org.jetbrains.kotlinconf.ui.components.MainHeaderTitleBar
import org.jetbrains.kotlinconf.ui.components.MarkdownView
import org.jetbrains.kotlinconf.ui.components.StyledText
import org.jetbrains.kotlinconf.ui.components.TopMenuButton
import org.jetbrains.kotlinconf.ui.theme.KotlinConfTheme

@Composable
fun ScreenWithTitle(
    title: String,
    onBack: () -> Unit,
    content: @Composable () -> Unit,
) {
    Column(Modifier.fillMaxSize()) {
        MainHeaderTitleBar(
            title = title,
            startContent = {
                TopMenuButton(
                    icon = Res.drawable.arrow_left_24,
                    contentDescription = stringResource(Res.string.main_header_back),
                    onClick = onBack,
                )
            }
        )

        Divider(thickness = 1.dp, color = KotlinConfTheme.colors.strokePale)

        Column(
            Modifier
                .fillMaxSize()
                .background(color = KotlinConfTheme.colors.mainBackground)
                .padding(horizontal = 12.dp)
                .verticalScroll(rememberScrollState())
        ) {
            content()
        }
    }
}

@Composable
fun MarkdownScreenWithTitle(
    title: String,
    header: String,
    subheader: String,
    loadText: suspend () -> ByteArray,
    onBack: () -> Unit,
    endContent: @Composable () -> Unit = {},
) {
    ScreenWithTitle(title, onBack) {
        if (header.isNotEmpty()) {
            StyledText(header, style = KotlinConfTheme.typography.h1, modifier = Modifier.padding(vertical = 12.dp))
        }

        if (subheader.isNotEmpty()) {
            StyledText(
                subheader,
                style = KotlinConfTheme.typography.text2,
                color = KotlinConfTheme.colors.noteText,
            )
        }

        MarkdownView(loadText, modifier = Modifier.padding(vertical = 12.dp))

        endContent()
    }
}
