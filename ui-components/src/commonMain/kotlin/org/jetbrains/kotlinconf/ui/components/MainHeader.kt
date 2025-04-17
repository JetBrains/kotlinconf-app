package org.jetbrains.kotlinconf.ui.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedContentTransitionScope.SlideDirection
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import kotlinconfapp.ui_components.generated.resources.Res
import kotlinconfapp.ui_components.generated.resources.arrow_left_24
import kotlinconfapp.ui_components.generated.resources.bookmark_24
import kotlinconfapp.ui_components.generated.resources.close_24
import kotlinconfapp.ui_components.generated.resources.main_header_back
import kotlinconfapp.ui_components.generated.resources.main_header_search_clear
import kotlinconfapp.ui_components.generated.resources.main_header_search_hint
import kotlinconfapp.ui_components.generated.resources.search_24
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.jetbrains.kotlinconf.ui.theme.KotlinConfTheme
import org.jetbrains.kotlinconf.ui.theme.PreviewHelper

@Composable
fun MainHeaderSearchBar(
    searchValue: String,
    onSearchValueChange: (String) -> Unit,
    onClear: () -> Unit,
    onClose: () -> Unit,
    modifier: Modifier = Modifier,
    hasAdditionalInputs: Boolean = false,
) {
    Row(
        modifier = modifier
            .height(48.dp)
            .fillMaxWidth()
            .background(KotlinConfTheme.colors.mainBackground),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        TopMenuButton(
            icon = Res.drawable.arrow_left_24,
            onClick = {
                onClose()
                onSearchValueChange("")
            },
            contentDescription = stringResource(Res.string.main_header_back),
        )

        var focusRequested by rememberSaveable { mutableStateOf(false) }
        val focusRequester = remember { FocusRequester() }
        if (!focusRequested) {
            LaunchedEffect(Unit) {
                focusRequester.requestFocus()
                focusRequested = true
            }
        }

        Box(
            modifier = Modifier.weight(1f).fillMaxHeight(),
            contentAlignment = Alignment.CenterStart,
        ) {
            BasicTextField(
                value = searchValue,
                onValueChange = { onSearchValueChange(it) },
                modifier = Modifier
                    .fillMaxWidth()
                    .focusRequester(focusRequester),
                singleLine = true,
                textStyle = KotlinConfTheme.typography.text1
                    .copy(color = KotlinConfTheme.colors.primaryText),
                cursorBrush = SolidColor(KotlinConfTheme.colors.primaryText),
            )
            androidx.compose.animation.AnimatedVisibility(
                searchValue.isEmpty(),
                enter = fadeIn(tween(10)),
                exit = fadeOut(tween(10)),
            ) {
                Text(
                    text = stringResource(Res.string.main_header_search_hint),
                    style = KotlinConfTheme.typography.text1,
                    color = KotlinConfTheme.colors.placeholderText
                )
            }
        }

        AnimatedVisibility(
            visible = searchValue.isNotEmpty() || hasAdditionalInputs,
            enter = fadeIn(tween(200)),
            exit = fadeOut(tween(100)),
        ) {
            TopMenuButton(
                icon = Res.drawable.close_24,
                onClick = {
                    onSearchValueChange("")
                    onClear()
                    focusRequester.requestFocus()
                },
                contentDescription = stringResource(Res.string.main_header_search_clear),
            )
        }
    }
}

@Composable
fun MainHeaderTitleBar(
    title: String,
    modifier: Modifier = Modifier,
    startContent: @Composable RowScope.() -> Unit = {},
    endContent: @Composable RowScope.() -> Unit = {},
) {
    Box(
        modifier = modifier
            .height(48.dp)
            .fillMaxWidth()
            .background(KotlinConfTheme.colors.mainBackground),
        contentAlignment = Alignment.Center,
    ) {
        Row(Modifier.align(Alignment.CenterStart)) {
            startContent()
        }
        Text(
            text = title,
            modifier = Modifier.align(Alignment.Center).semantics { heading() },
            style = KotlinConfTheme.typography.h3,
            color = KotlinConfTheme.colors.primaryText,
        )
        Row(Modifier.align(Alignment.CenterEnd)) {
            endContent()
        }
    }
}

enum class MainHeaderContainerState {
    Title,
    Search,
}

@Composable
fun MainHeaderContainer(
    state: MainHeaderContainerState,
    modifier: Modifier = Modifier,
    titleContent: @Composable () -> Unit = {},
    searchContent: @Composable () -> Unit = {},
) {
    AnimatedContent(
        targetState = state,
        transitionSpec = {
            (fadeIn(tween(50)) + slideIntoContainer(SlideDirection.Down)) togetherWith
                    (fadeOut(tween(50)) + slideOutOfContainer(SlideDirection.Up))
        },
        modifier = modifier
            .height(48.dp)
            .fillMaxWidth()
            .background(KotlinConfTheme.colors.mainBackground),
    ) { target ->
        when (target) {
            MainHeaderContainerState.Title -> titleContent()
            MainHeaderContainerState.Search -> searchContent()
        }
    }
}

@Preview
@Composable
internal fun MainHeaderPreview() {
    // Example with Now button, bookmark and search action
    PreviewHelper(paddingEnabled = false) {
        var nowState by remember { mutableStateOf(NowButtonState.Before) }
        var state1 by remember { mutableStateOf(MainHeaderContainerState.Title) }
        var search1 by remember { mutableStateOf("") }
        var bookmarkFilter1 by remember { mutableStateOf(false) }
        MainHeaderContainer(
            state = state1,
            titleContent = {
                MainHeaderTitleBar(
                    title = "Schedule",
                    startContent = {
                        NowButton(
                            time = nowState,
                            onClick = {
                                // Rotate through states as a demo
                                nowState = when (nowState) {
                                    NowButtonState.Before -> NowButtonState.Current
                                    NowButtonState.Current -> NowButtonState.After
                                    NowButtonState.After -> NowButtonState.Before
                                }
                            },
                            enabled = true,
                        )
                    },
                    endContent = {
                        TopMenuButton(
                            icon = Res.drawable.bookmark_24,
                            selected = bookmarkFilter1,
                            onToggle = { bookmarkFilter1 = it },
                            contentDescription = "Bookmark filter",
                        )
                        TopMenuButton(
                            icon = Res.drawable.search_24,
                            onClick = { state1 = MainHeaderContainerState.Search },
                            contentDescription = "Search",
                        )
                    }
                )
            },
            searchContent = {
                MainHeaderSearchBar(
                    searchValue = search1,
                    onSearchValueChange = { search1 = it },
                    onClose = { state1 = MainHeaderContainerState.Title },
                    onClear = {},
                    hasAdditionalInputs = bookmarkFilter1,
                )
            }
        )

        // Example with search action
        var state2 by remember { mutableStateOf(MainHeaderContainerState.Title) }
        var search2 by remember { mutableStateOf("") }
        MainHeaderContainer(
            state = state2,
            titleContent = {
                MainHeaderTitleBar(
                    title = "Speaker",
                    endContent = {
                        TopMenuButton(
                            icon = Res.drawable.search_24,
                            onClick = { state2 = MainHeaderContainerState.Search },
                            contentDescription = "Search",
                        )
                    }
                )
            },
            searchContent = {
                MainHeaderSearchBar(
                    searchValue = search2,
                    onSearchValueChange = { search2 = it },
                    onClose = { state2 = MainHeaderContainerState.Title },
                    onClear = {},
                    hasAdditionalInputs = false,
                )
            }
        )

        // Example with back action
        MainHeaderTitleBar(
            title = "Privacy notice",
            startContent = {
                TopMenuButton(
                    icon = Res.drawable.arrow_left_24,
                    contentDescription = "Back",
                    onClick = {},
                )
            }
        )

        // Exaple with title only
        MainHeaderTitleBar(title = "Info")
    }
}
