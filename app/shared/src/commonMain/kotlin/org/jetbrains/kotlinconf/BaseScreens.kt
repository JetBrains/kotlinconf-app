package org.jetbrains.kotlinconf

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.kotlinconf.generated.resources.Res
import org.jetbrains.kotlinconf.generated.resources.document_error_no_data
import org.jetbrains.kotlinconf.ui.components.HorizontalDivider
import org.jetbrains.kotlinconf.ui.components.MainHeaderTitleBar
import org.jetbrains.kotlinconf.ui.components.MarkdownView
import org.jetbrains.kotlinconf.ui.components.NormalErrorWithLoading
import org.jetbrains.kotlinconf.ui.components.Text
import org.jetbrains.kotlinconf.ui.components.TopMenuButton
import org.jetbrains.kotlinconf.ui.generated.resources.UiRes
import org.jetbrains.kotlinconf.ui.generated.resources.arrow_left_24
import org.jetbrains.kotlinconf.ui.generated.resources.main_header_back
import org.jetbrains.kotlinconf.ui.theme.KotlinConfTheme
import org.jetbrains.kotlinconf.utils.FadingAnimationSpec
import org.jetbrains.kotlinconf.utils.bottomInsetPadding
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
                    icon = UiRes.drawable.arrow_left_24,
                    contentDescription = stringResource(UiRes.string.main_header_back),
                    onClick = onBack,
                )
            }
        )

        HorizontalDivider(thickness = 1.dp, color = KotlinConfTheme.colors.strokePale)

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

sealed interface DocumentState {
    data object Loading : DocumentState
    data class Success(val text: String) : DocumentState
    data object Error : DocumentState
}

@Composable
fun MarkdownScreenWithTitle(
    title: String,
    header: String,
    documentState: DocumentState,
    onBack: () -> Unit,
    onReload: () -> Unit,
    onCustomUriClick: (String) -> Unit = {},
    endContent: @Composable ColumnScope.() -> Unit = {},
) {
    val scrollState = rememberScrollState()
    ScrollToTopHandler(scrollState)
    ScreenWithTitle(title, onBack, contentScrollState = scrollState) {
        if (header.isNotEmpty()) {
            Text(
                header,
                style = KotlinConfTheme.typography.h1,
                modifier = Modifier.padding(top = 24.dp, bottom = 12.dp)
            )
        }

        AnimatedContent(
            targetState = documentState,
            modifier = Modifier.fillMaxSize().clipToBounds(),
            contentKey = {
                when (it) {
                    is DocumentState.Success -> 1
                    DocumentState.Loading, DocumentState.Error -> 2
                }
            },
            transitionSpec = { FadingAnimationSpec },
            contentAlignment = Alignment.Center,
        ) { targetState ->
            when (targetState) {
                is DocumentState.Success -> {
                    Column {
                        MarkdownView(
                            text = targetState.text,
                            modifier = Modifier.padding(vertical = 12.dp),
                            onCustomUriClick = onCustomUriClick,
                        )
                        endContent()
                    }
                }
                DocumentState.Loading, DocumentState.Error -> {
                    NormalErrorWithLoading(
                        message = stringResource(Res.string.document_error_no_data),
                        isLoading = targetState == DocumentState.Loading,
                        modifier = Modifier.padding(top = 96.dp),
                        onRetry = onReload,
                    )
                }
            }
        }
    }
}
