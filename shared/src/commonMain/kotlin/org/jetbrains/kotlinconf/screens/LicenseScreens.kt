package org.jetbrains.kotlinconf.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.backhandler.BackHandler
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.unit.dp
import com.mikepenz.aboutlibraries.entity.Library
import kotlinconfapp.shared.generated.resources.Res
import kotlinconfapp.shared.generated.resources.licenses_number_of_results
import kotlinconfapp.shared.generated.resources.licenses_title
import kotlinconfapp.ui_components.generated.resources.arrow_left_24
import kotlinconfapp.ui_components.generated.resources.main_header_back
import kotlinconfapp.ui_components.generated.resources.main_header_search_hint
import kotlinconfapp.ui_components.generated.resources.search_24
import org.jetbrains.compose.resources.pluralStringResource
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.kotlinconf.HideKeyboardOnDragHandler
import org.jetbrains.kotlinconf.ScreenWithTitle
import org.jetbrains.kotlinconf.ScrollToTopHandler
import org.jetbrains.kotlinconf.ui.components.Divider
import org.jetbrains.kotlinconf.ui.components.MainHeaderContainer
import org.jetbrains.kotlinconf.ui.components.MainHeaderContainerState
import org.jetbrains.kotlinconf.ui.components.MainHeaderSearchBar
import org.jetbrains.kotlinconf.ui.components.MainHeaderTitleBar
import org.jetbrains.kotlinconf.ui.components.Text
import org.jetbrains.kotlinconf.ui.components.TopMenuButton
import org.jetbrains.kotlinconf.ui.theme.KotlinConfTheme
import org.jetbrains.kotlinconf.utils.bottomInsetPadding
import org.jetbrains.kotlinconf.utils.plus
import org.koin.compose.viewmodel.koinViewModel
import kotlinconfapp.ui_components.generated.resources.Res as UiRes

private val Library.licenseName: String
    get() = licenses.firstOrNull()?.name ?: "Unknown license"

private val Library.licenseContent: String
    get() = licenses.firstOrNull()?.licenseContent ?: ""

private val Library.author: String
    get() = when {
        developers.isNotEmpty() -> developers.joinToString { it.name.toString() }
        else -> organization?.name ?: ""
    }

@Composable
fun LicensesScreen(
    onLicenseClick: (licenseName: String, licenseText: String) -> Unit,
    onBack: () -> Unit,
    viewModel: LicensesViewModel = koinViewModel(),
) {
    var searchState by rememberSaveable { mutableStateOf(MainHeaderContainerState.Title) }
    var searchText by rememberSaveable { mutableStateOf("") }

    val libraries = viewModel.licensesState.collectAsState().value
    val listState = rememberLazyListState()

    LaunchedEffect(searchState, searchText) {
        if (searchState == MainHeaderContainerState.Search) {
            if (listState.firstVisibleItemIndex > 1) {
                listState.scrollToItem(0)
            } else {
                listState.animateScrollToItem(0)
            }
        }
        viewModel.setSearchText(searchText)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(color = KotlinConfTheme.colors.mainBackground)
    ) {
        MainHeaderContainer(
            state = searchState,
            titleContent = {
                MainHeaderTitleBar(
                    title = stringResource(Res.string.licenses_title),
                    startContent = {
                        TopMenuButton(
                            icon = UiRes.drawable.arrow_left_24,
                            contentDescription = stringResource(UiRes.string.main_header_back),
                            onClick = onBack,
                        )
                    },
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

        LibraryList(
            libraries = libraries,
            isSearch = searchState == MainHeaderContainerState.Search,
            onLicenseClick = { library ->
                onLicenseClick(library.licenseName, library.licenseContent)
            },
            listState = listState,
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 12.dp)
                .clipToBounds(),
        )
    }
}

@Composable
fun SingleLicenseScreen(
    licenseName: String,
    licenseContent: String,
    onBack: () -> Unit,
) {
    ScreenWithTitle(title = licenseName, onBack = onBack) {
        Text(
            licenseContent,
            style = KotlinConfTheme.typography.text2,
            color = KotlinConfTheme.colors.noteText,
            modifier = Modifier.padding(PaddingValues(vertical = 12.dp) + bottomInsetPadding()),
        )
    }
}

@Composable
private fun LibraryList(
    libraries: List<LibraryWithHighlights>,
    onLicenseClick: (library: Library) -> Unit,
    modifier: Modifier = Modifier,
    isSearch: Boolean = false,
    listState: LazyListState = rememberLazyListState(),
) {
    ScrollToTopHandler(listState)
    HideKeyboardOnDragHandler(listState)

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        state = listState,
    ) {
        if (isSearch) {
            item(key = "item-count") {
                AnimatedVisibility(isSearch) {
                    Text(
                        text = pluralStringResource(
                            Res.plurals.licenses_number_of_results,
                            libraries.size,
                            libraries.size
                        ),
                        color = KotlinConfTheme.colors.secondaryText,
                        style = KotlinConfTheme.typography.text2,
                        modifier = Modifier
                            .padding(horizontal = 12.dp)
                            .padding(bottom = 4.dp)
                            .semantics { liveRegion = LiveRegionMode.Polite }
                            .animateItem()
                    )
                }
            }
        }

        items(libraries, key = { it.library.uniqueId }) { libraryWithHighlights ->
            val library = libraryWithHighlights.library
            LibraryItem(
                library = library,
                nameHighlights = libraryWithHighlights.nameHighlights,
                authorHighlights = libraryWithHighlights.authorHighlights,
                licenseHighlights = libraryWithHighlights.licenseHighlights,
                onLicenseClick = { onLicenseClick(library) },
                modifier = Modifier.animateItem(),
            )
        }
    }
}

@Composable
private fun LibraryItem(
    library: Library,
    onLicenseClick: () -> Unit,
    modifier: Modifier = Modifier,
    nameHighlights: List<IntRange> = emptyList(),
    authorHighlights: List<IntRange> = emptyList(),
    licenseHighlights: List<IntRange> = emptyList(),
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(
                color = KotlinConfTheme.colors.tileBackground,
                shape = RoundedCornerShape(8.dp)
            )
            .clickable(onClick = onLicenseClick)
            .padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = highlightText(library.name, nameHighlights),
                style = KotlinConfTheme.typography.h3,
                color = KotlinConfTheme.colors.primaryText,
                modifier = Modifier.weight(1f)
            )
            Text(
                text = library.artifactVersion ?: "",
                style = KotlinConfTheme.typography.text2,
                color = KotlinConfTheme.colors.secondaryText,
                maxLines = 1,
            )
        }
        Text(
            text = highlightText(library.author, authorHighlights),
            style = KotlinConfTheme.typography.text2,
            color = KotlinConfTheme.colors.secondaryText,
        )
        Text(
            text = highlightText(library.licenseName, licenseHighlights),
            style = KotlinConfTheme.typography.text2,
            color = KotlinConfTheme.colors.primaryText,
        )
    }
}

@Composable
private fun highlightText(text: String, highlights: List<IntRange>): AnnotatedString {
    if (highlights.isEmpty()) return AnnotatedString(text)

    return buildAnnotatedString {
        append(text)
        highlights.forEach { range ->
            // Ignore invalid ranges
            if (!range.isEmpty()) {
                addStyle(
                    style = SpanStyle(
                        color = KotlinConfTheme.colors.primaryTextInverted,
                        background = KotlinConfTheme.colors.primaryBackground,
                    ),
                    start = range.first,
                    end = range.last + 1,
                )
            }
        }
    }
}
