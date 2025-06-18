package org.jetbrains.kotlinconf.screens

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.backhandler.BackHandler
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import kotlinconfapp.shared.generated.resources.Res
import kotlinconfapp.shared.generated.resources.speakers_error_no_data
import kotlinconfapp.shared.generated.resources.speakers_number_of_results
import kotlinconfapp.shared.generated.resources.speakers_title
import kotlinconfapp.ui_components.generated.resources.main_header_search_hint
import kotlinconfapp.ui_components.generated.resources.search_24
import org.jetbrains.compose.resources.pluralStringResource
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.kotlinconf.HideKeyboardOnDragHandler
import org.jetbrains.kotlinconf.ScrollToTopHandler
import org.jetbrains.kotlinconf.SpeakerId
import org.jetbrains.kotlinconf.ui.components.Divider
import org.jetbrains.kotlinconf.ui.components.MainHeaderContainer
import org.jetbrains.kotlinconf.ui.components.MainHeaderContainerState
import org.jetbrains.kotlinconf.ui.components.MainHeaderSearchBar
import org.jetbrains.kotlinconf.ui.components.MainHeaderTitleBar
import org.jetbrains.kotlinconf.ui.components.NormalErrorWithLoading
import org.jetbrains.kotlinconf.ui.components.SpeakerCard
import org.jetbrains.kotlinconf.ui.components.Text
import org.jetbrains.kotlinconf.ui.components.TopMenuButton
import org.jetbrains.kotlinconf.ui.theme.KotlinConfTheme
import org.jetbrains.kotlinconf.utils.FadingAnimationSpec
import org.koin.compose.viewmodel.koinViewModel
import kotlinconfapp.ui_components.generated.resources.UiRes

@Composable
fun SpeakersScreen(
    onSpeaker: (SpeakerId) -> Unit,
    viewModel: SpeakersViewModel = koinViewModel(),
) {
    var searchState by remember { mutableStateOf(MainHeaderContainerState.Title) }
    var searchText by remember { mutableStateOf("") }

    val uiState = viewModel.speakers.collectAsState().value

    val gridState = rememberLazyGridState()

    LaunchedEffect(searchState, searchText) {
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
                    onClear = { searchText = "" },
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
                }
            },
            transitionSpec = { FadingAnimationSpec }
        ) { targetState ->
            when (targetState) {
                is SpeakersUiState.Content -> {
                    ScrollToTopHandler(gridState)
                    HideKeyboardOnDragHandler(gridState)

                    val speakers = targetState.speakers
                    LazyVerticalGrid(
                        state = gridState,
                        columns = GridCells.Fixed(2),
                        modifier = Modifier.fillMaxSize(),
                    ) {
                        items(speakers, key = { it.speaker.id.id }) { speakerWithHighlights ->
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
                                    .padding(12.dp),
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
            }
        }
    }
}
