package org.jetbrains.kotlinconf.screens

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.backhandler.BackHandler
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.unit.dp
import kotlinconfapp.shared.generated.resources.Res
import kotlinconfapp.shared.generated.resources.speakers_error_no_data
import kotlinconfapp.shared.generated.resources.speakers_error_no_results
import kotlinconfapp.shared.generated.resources.speakers_title
import kotlinconfapp.ui_components.generated.resources.main_header_search_hint
import kotlinconfapp.ui_components.generated.resources.search_24
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.kotlinconf.ScrollToTopHandler
import org.jetbrains.kotlinconf.SpeakerId
import org.jetbrains.kotlinconf.ui.components.Divider
import org.jetbrains.kotlinconf.ui.components.MainHeaderContainer
import org.jetbrains.kotlinconf.ui.components.MainHeaderContainerState
import org.jetbrains.kotlinconf.ui.components.MainHeaderSearchBar
import org.jetbrains.kotlinconf.ui.components.MainHeaderTitleBar
import org.jetbrains.kotlinconf.ui.components.MinorError
import org.jetbrains.kotlinconf.ui.components.NormalErrorWithLoading
import org.jetbrains.kotlinconf.ui.components.SpeakerCard
import org.jetbrains.kotlinconf.ui.components.TopMenuButton
import org.jetbrains.kotlinconf.ui.theme.KotlinConfTheme
import org.jetbrains.kotlinconf.utils.FadingAnimationSpec
import org.koin.compose.viewmodel.koinViewModel
import kotlinconfapp.ui_components.generated.resources.Res as UiRes

@Composable
fun SpeakersScreen(
    onSpeaker: (SpeakerId) -> Unit,
    viewModel: SpeakersViewModel = koinViewModel(),
) {
    var searchState by rememberSaveable { mutableStateOf(MainHeaderContainerState.Title) }
    var searchText by rememberSaveable { mutableStateOf("") }

    val uiState = viewModel.speakers.collectAsState().value

    LaunchedEffect(searchText) {
        viewModel.setSearchText(searchText)
    }

    Column(Modifier.fillMaxSize().background(color = KotlinConfTheme.colors.mainBackground)) {
        MainHeaderContainer(
            state = searchState,
            titleContent = {
                MainHeaderTitleBar(
                    title = stringResource(Res.string.speakers_title),
                    endContent = {
                        TopMenuButton(
                            icon = UiRes.drawable.search_24,
                            onClick = { searchState = MainHeaderContainerState.Search },
                            contentDescription = stringResource(UiRes.string.main_header_search_hint)
                        )
                    }
                )
            },
            searchContent = {
                @OptIn(ExperimentalComposeUiApi::class)
                BackHandler(true) {
                    searchState = MainHeaderContainerState.Title
                    searchText = ""
                }
                MainHeaderSearchBar(
                    searchValue = searchText,
                    onSearchValueChange = { searchText = it },
                    onClose = {
                        searchState = MainHeaderContainerState.Title
                        searchText = ""
                    },
                    onClear = { searchText = "" }
                )
            }
        )

        Divider(1.dp, KotlinConfTheme.colors.strokePale)

        AnimatedContent(
            uiState,
            modifier = Modifier.fillMaxSize().clipToBounds(),
            contentKey = {
                when (uiState) {
                    is SpeakersUiState.Content -> 1
                    SpeakersUiState.Error, SpeakersUiState.Loading -> 2
                    SpeakersUiState.NoSearchResults -> 3
                }
            },
            transitionSpec = { FadingAnimationSpec }
        ) { targetState ->
            when (targetState) {
                is SpeakersUiState.Content -> {
                    val listState = rememberLazyListState()
                    ScrollToTopHandler(listState)
                    LazyColumn(
                        state = listState,
                        modifier = Modifier.fillMaxSize(),
                    ) {
                        items(targetState.speakers, key = { it.speaker.id.id }) { speakerWithHighlights ->
                            val speaker = speakerWithHighlights.speaker
                            SpeakerCard(
                                name = speaker.name,
                                nameHighlights = speakerWithHighlights.nameHighlights,
                                title = speaker.position,
                                titleHighlights = speakerWithHighlights.titleHighlights,
                                photoUrl = speaker.photoUrl,
                                modifier = Modifier
                                    .animateItem()
                                    .fillMaxWidth()
                                    .padding(12.dp)
                                    .clip(RoundedCornerShape(8.dp)),
                                onClick = { onSpeaker(speaker.id) },
                            )
                        }
                    }
                }

                SpeakersUiState.Error, SpeakersUiState.Loading -> {
                    NormalErrorWithLoading(
                        message = stringResource(Res.string.speakers_error_no_data),
                        isLoading = uiState is SpeakersUiState.Loading,
                        modifier = Modifier.fillMaxSize(),
                        onRetry = { viewModel.refresh() },
                    )
                }

                SpeakersUiState.NoSearchResults -> {
                    MinorError(
                        message = stringResource(Res.string.speakers_error_no_results),
                        modifier = Modifier.fillMaxSize(),
                    )
                }
            }
        }
    }
}
