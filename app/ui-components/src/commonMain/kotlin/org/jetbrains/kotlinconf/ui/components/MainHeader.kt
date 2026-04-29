package org.jetbrains.kotlinconf.ui.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedContentTransitionScope.SlideDirection
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.kotlinconf.ui.generated.resources.UiRes
import org.jetbrains.kotlinconf.ui.generated.resources.arrow_left_24
import org.jetbrains.kotlinconf.ui.generated.resources.bookmark_24
import org.jetbrains.kotlinconf.ui.generated.resources.close_24
import org.jetbrains.kotlinconf.ui.generated.resources.main_header_back
import org.jetbrains.kotlinconf.ui.generated.resources.main_header_search_clear
import org.jetbrains.kotlinconf.ui.generated.resources.main_header_search_hint
import org.jetbrains.kotlinconf.ui.generated.resources.search_24
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
            icon = UiRes.drawable.arrow_left_24,
            onClick = {
                onClose()
                onSearchValueChange("")
            },
            contentDescription = stringResource(UiRes.string.main_header_back),
        )

        var focusRequested by rememberSaveable { mutableStateOf(false) }
        val focusRequester = remember { FocusRequester() }
        if (!focusRequested) {
            LaunchedEffect(Unit) {
                focusRequester.requestFocus()
                focusRequested = true
            }
        }

        SearchInput(
            searchValue = searchValue,
            onSearchValueChange = onSearchValueChange,
            hint = stringResource(UiRes.string.main_header_search_hint),
            focusRequester = focusRequester,
            modifier = Modifier.weight(1f).fillMaxHeight(),
        )

        AnimatedVisibility(
            visible = searchValue.isNotEmpty() || hasAdditionalInputs,
            enter = fadeIn(tween(200)),
            exit = fadeOut(tween(100)),
        ) {
            TopMenuButton(
                icon = UiRes.drawable.close_24,
                onClick = {
                    onSearchValueChange("")
                    onClear()
                    focusRequester.requestFocus()
                },
                contentDescription = stringResource(UiRes.string.main_header_search_clear),
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

@PreviewLightDark
@Composable
private fun MainHeaderSchedulePreview() = PreviewHelper(paddingEnabled = false) {
    // Example with Now button, bookmark and search action
    var nowState by remember { mutableStateOf(NowButtonState.Before) }
    var state by remember { mutableStateOf(MainHeaderContainerState.Title) }
    var search by remember { mutableStateOf("") }
    var bookmarkFilter by remember { mutableStateOf(false) }
    MainHeaderContainer(
        state = state,
        titleContent = {
            MainHeaderTitleBar(
                title = "Schedule",
                startContent = {
                    NowButton(
                        time = nowState,
                        onClick = {
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
                        icon = UiRes.drawable.bookmark_24,
                        selected = bookmarkFilter,
                        onToggle = { bookmarkFilter = it },
                        contentDescription = "Bookmark filter",
                    )
                    TopMenuButton(
                        icon = UiRes.drawable.search_24,
                        onClick = { state = MainHeaderContainerState.Search },
                        contentDescription = "Search",
                    )
                }
            )
        },
        searchContent = {
            MainHeaderSearchBar(
                searchValue = search,
                onSearchValueChange = { search = it },
                onClose = { state = MainHeaderContainerState.Title },
                onClear = {},
                hasAdditionalInputs = bookmarkFilter,
            )
        }
    )
}

@PreviewLightDark
@Composable
private fun MainHeaderSpeakerPreview() = PreviewHelper(paddingEnabled = false) {
    var state by remember { mutableStateOf(MainHeaderContainerState.Title) }
    var search by remember { mutableStateOf("") }
    MainHeaderContainer(
        state = state,
        titleContent = {
            MainHeaderTitleBar(
                title = "Speaker",
                endContent = {
                    TopMenuButton(
                        icon = UiRes.drawable.search_24,
                        onClick = { state = MainHeaderContainerState.Search },
                        contentDescription = "Search",
                    )
                }
            )
        },
        searchContent = {
            MainHeaderSearchBar(
                searchValue = search,
                onSearchValueChange = { search = it },
                onClose = { state = MainHeaderContainerState.Title },
                onClear = {},
                hasAdditionalInputs = false,
            )
        }
    )
}

@PreviewLightDark
@Composable
private fun MainHeaderSearchStatePreview() = PreviewHelper(paddingEnabled = false) {
    var state by remember { mutableStateOf(MainHeaderContainerState.Search) }
    var search by remember { mutableStateOf("") }
    MainHeaderContainer(
        state = state,
        titleContent = {},
        searchContent = {
            MainHeaderSearchBar(
                searchValue = search,
                onSearchValueChange = { search = it },
                onClose = { state = MainHeaderContainerState.Title },
                onClear = {},
                hasAdditionalInputs = false,
            )
        }
    )
}

@PreviewLightDark
@Composable
private fun MainHeaderBackPreview() = PreviewHelper(paddingEnabled = false) {
    MainHeaderTitleBar(
        title = "Privacy notice",
        startContent = {
            TopMenuButton(
                icon = UiRes.drawable.arrow_left_24,
                contentDescription = "Back",
                onClick = {},
            )
        }
    )
}

@PreviewLightDark
@Composable
private fun MainHeaderTitleOnlyPreview() = PreviewHelper(paddingEnabled = false) {
    MainHeaderTitleBar(title = "Info")
}
