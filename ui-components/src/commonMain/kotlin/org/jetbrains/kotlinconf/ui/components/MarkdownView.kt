package org.jetbrains.kotlinconf.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.platform.UriHandler
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLinkStyles
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.mikepenz.markdown.compose.Markdown
import com.mikepenz.markdown.model.DefaultMarkdownColors
import com.mikepenz.markdown.model.DefaultMarkdownTypography
import com.mikepenz.markdown.model.State
import com.mikepenz.markdown.model.markdownAnimations
import com.mikepenz.markdown.model.markdownPadding
import com.mikepenz.markdown.model.parseMarkdownFlow
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.stateIn
import org.jetbrains.kotlinconf.ui.theme.KotlinConfTheme
import kotlin.random.Random

@Composable
fun MarkdownView(
    loadText: suspend () -> ByteArray,
    modifier: Modifier = Modifier,
    onCustomUriClick: (String) -> Unit = {},
) {
    MarkdownImpl(
        loadText = { loadText().decodeToString() },
        modifier = modifier,
        onCustomUriClick = onCustomUriClick,
    )
}

@Composable
fun MarkdownView(
    text: String,
    modifier: Modifier = Modifier,
    onCustomUriClick: (String) -> Unit = {},
) {
    MarkdownImpl(
        loadText = { text },
        modifier = modifier,
        onCustomUriClick = onCustomUriClick,
    )
}

private class MarkdownViewModel(loadText: suspend () -> String) : ViewModel() {
    @OptIn(ExperimentalCoroutinesApi::class)
    val markdownState: StateFlow<State> =
        flow { emit(loadText()) }
            .flatMapLatest { parseMarkdownFlow(it) }
            .flowOn(Dispatchers.Default)
            .stateIn(viewModelScope, SharingStarted.Eagerly, State.Loading())
}

@Composable
private fun MarkdownImpl(
    loadText: suspend () -> String,
    modifier: Modifier = Modifier,
    onCustomUriClick: (String) -> Unit = {},
) {
    val vmKey = rememberSaveable { Random.nextInt().toString() }
    val vm = viewModel(key = vmKey) { MarkdownViewModel(loadText) }
    val state by vm.markdownState.collectAsStateWithLifecycle()

    val regularUriHandler = LocalUriHandler.current
    val customUriHandler = object : UriHandler {
        override fun openUri(uri: String) {
            if (uri.startsWith("http://") || uri.startsWith("https://")) {
                regularUriHandler.openUri(uri)
            } else {
                onCustomUriClick(uri)
            }
        }
    }

    CompositionLocalProvider(LocalUriHandler provides customUriHandler) {
        Markdown(
            state = state,
            colors = DefaultMarkdownColors(
                text = KotlinConfTheme.colors.longText,
                codeText = KotlinConfTheme.colors.secondaryText,
                linkText = KotlinConfTheme.colors.purpleText,
                codeBackground = KotlinConfTheme.colors.mainBackground,
                inlineCodeText = KotlinConfTheme.colors.secondaryText,
                inlineCodeBackground = KotlinConfTheme.colors.mainBackground,
                dividerColor = KotlinConfTheme.colors.strokePale,
                tableText = KotlinConfTheme.colors.longText,
                tableBackground = KotlinConfTheme.colors.mainBackground,
            ),
            typography = DefaultMarkdownTypography(
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
                table = KotlinConfTheme.typography.text1,
                textLink = TextLinkStyles(
                    style = SpanStyle(
                        color = KotlinConfTheme.colors.primaryText,
                        textDecoration = TextDecoration.Underline,
                    ),
                ),
            ),
            animations = markdownAnimations { this },
            padding = markdownPadding(block = 6.dp),
            modifier = modifier,
        )
    }
}
