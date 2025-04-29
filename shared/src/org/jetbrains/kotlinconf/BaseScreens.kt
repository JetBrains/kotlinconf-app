package org.jetbrains.kotlinconf

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
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
import org.jetbrains.kotlinconf.ui.components.Text
import org.jetbrains.kotlinconf.ui.components.TopMenuButton
import org.jetbrains.kotlinconf.ui.theme.KotlinConfTheme
import org.jetbrains.kotlinconf.utils.bottomInsetPadding
import org.jetbrains.kotlinconf.utils.plus
import org.jetbrains.kotlinconf.utils.topInsetPadding

@Composable
fun ScreenWithTitle(
    title: String,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    contentScrollState: ScrollState = rememberScrollState(),
    content: @Composable ColumnScope.() -> Unit,
) {
    Column(
        modifier
            .fillMaxSize()
            .background(color = KotlinConfTheme.colors.mainBackground)
            .padding(topInsetPadding())
    ) {
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
                .verticalScroll(contentScrollState)
                .padding(bottomInsetPadding())
        ) {
            content()
        }
    }
}

@Composable
fun MarkdownScreenWithTitle(
    title: String,
    header: String,
    loadText: suspend () -> ByteArray,
    onBack: () -> Unit,
    onCustomUriClick: (String) -> Unit = {},
    endContent: @Composable ColumnScope.() -> Unit = {},
) {
    val scrollState = rememberScrollState()
    ScrollToTopHandler(scrollState)
    ScreenWithTitle(title, onBack, contentScrollState = scrollState) {
        if (header.isNotEmpty()) {
            Text(header, style = KotlinConfTheme.typography.h1, modifier = Modifier.padding(top = 24.dp, bottom = 12.dp))
        }

        MarkdownView(loadText, modifier = Modifier.padding(vertical = 12.dp), onCustomUriClick = onCustomUriClick)

        endContent()
    }
}
