package org.jetbrains.kotlinconf.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigationevent.NavigationEventInfo
import androidx.navigationevent.compose.NavigationBackHandler
import androidx.navigationevent.compose.rememberNavigationEventState
import dev.zacsweers.metrox.viewmodel.metroViewModel
import org.jetbrains.compose.resources.pluralStringResource
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.kotlinconf.HideKeyboardOnDragHandler
import org.jetbrains.kotlinconf.ScrollToTopHandler
import org.jetbrains.kotlinconf.Speaker
import org.jetbrains.kotlinconf.generated.resources.Res
import org.jetbrains.kotlinconf.generated.resources.speakers_error_no_data
import org.jetbrains.kotlinconf.generated.resources.speakers_number_of_results
import org.jetbrains.kotlinconf.generated.resources.speakers_title
import org.jetbrains.kotlinconf.navigation.LocalUseNativeNavigation
import org.jetbrains.kotlinconf.ui.components.HorizontalDivider
import org.jetbrains.kotlinconf.ui.components.MainHeaderContainer
import org.jetbrains.kotlinconf.ui.components.MainHeaderContainerState
import org.jetbrains.kotlinconf.ui.components.MainHeaderSearchBar
import org.jetbrains.kotlinconf.ui.components.MainHeaderTitleBar
import org.jetbrains.kotlinconf.ui.components.SpeakerCard
import org.jetbrains.kotlinconf.ui.components.Text
import org.jetbrains.kotlinconf.ui.components.TopMenuButton
import org.jetbrains.kotlinconf.ui.generated.resources.UiRes
import org.jetbrains.kotlinconf.ui.generated.resources.main_header_search_hint
import org.jetbrains.kotlinconf.ui.generated.resources.search_24
import org.jetbrains.kotlinconf.ui.theme.KotlinConfTheme
import org.jetbrains.kotlinconf.utils.ErrorLoadingContent
import org.jetbrains.kotlinconf.utils.bottomInsetPadding
import org.jetbrains.kotlinconf.utils.topInsetPadding
import org.jetbrains.kotlinconf.utils.verticalInsetPadding

@Composable
fun SpeakersScreen(
    onSpeaker: (Speaker) -> Unit,
    viewModel: SpeakersViewModel = metroViewModel(),
) {
    var searchState by rememberSaveable { mutableStateOf(MainHeaderContainerState.Title) }
    var searchText by rememberSaveable { mutableStateOf("") }

    val uiState = viewModel.speakers.collectAsStateWithLifecycle().value
    val useNativeNavigation = LocalUseNativeNavigation.current

    val gridState = rememberLazyGridState()

    LaunchedEffect(searchState, searchText) {
        if (searchState == MainHeaderContainerState.Search) {
            if (gridState.firstVisibleItemIndex > 1) {
                gridState.scrollToItem(0)
            } else {
                gridState.animateScrollToItem(0)
            }
        }
        viewModel.setSearchText(searchText)
    }

    Column(
        Modifier.fillMaxSize()
            .background(color = KotlinConfTheme.colors.mainBackground)
            .then(if (useNativeNavigation) Modifier else Modifier.padding(topInsetPadding()))
    ) {
        if (!useNativeNavigation) {
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
                    NavigationBackHandler(
                        state = rememberNavigationEventState(NavigationEventInfo.None),
                        isBackEnabled = true,
                        onBackCompleted = {
                            searchState = MainHeaderContainerState.Title
                            searchText = ""
                        },
                    )

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

            HorizontalDivider(1.dp, KotlinConfTheme.colors.strokePale)
        }

        ErrorLoadingContent(
            state = uiState,
            errorMessage = stringResource(Res.string.speakers_error_no_data),
            onRetry = { viewModel.refresh() },
            modifier = Modifier.fillMaxSize(),
        ) { speakers ->
            ScrollToTopHandler(gridState)
            HideKeyboardOnDragHandler(gridState)

            LazyVerticalGrid(
                state = gridState,
                columns = GridCells.Adaptive(300.dp),
                modifier = Modifier.fillMaxSize(),
                contentPadding = if (useNativeNavigation) verticalInsetPadding() else bottomInsetPadding(),
            ) {
                if (searchState == MainHeaderContainerState.Search) {
                    item(span = { GridItemSpan(maxLineSpan) }, key = "number-of-speakers") {
                        Text(
                            text = pluralStringResource(
                                Res.plurals.speakers_number_of_results,
                                speakers.size,
                                speakers.size
                            ),
                            color = KotlinConfTheme.colors.secondaryText,
                            style = KotlinConfTheme.typography.text2,
                            modifier = Modifier
                                .animateItem()
                                .padding(horizontal = 12.dp)
                                .padding(top = 12.dp, bottom = 4.dp)
                                .semantics { liveRegion = LiveRegionMode.Polite }
                        )
                    }
                }
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
                        onClick = { onSpeaker(speaker) },
                    )
                }
            }
        }
    }
}
