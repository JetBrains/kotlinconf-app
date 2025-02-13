package org.jetbrains.kotlinconf.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import kotlinconfapp.shared.generated.resources.Res
import kotlinconfapp.shared.generated.resources.speakers_title
import kotlinconfapp.ui_components.generated.resources.main_header_search_hint
import kotlinconfapp.ui_components.generated.resources.search_24
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.kotlinconf.SpeakerId
import org.jetbrains.kotlinconf.ui.components.Divider
import org.jetbrains.kotlinconf.ui.components.MainHeaderContainer
import org.jetbrains.kotlinconf.ui.components.MainHeaderContainerState
import org.jetbrains.kotlinconf.ui.components.MainHeaderSearchBar
import org.jetbrains.kotlinconf.ui.components.MainHeaderTitleBar
import org.jetbrains.kotlinconf.ui.components.SpeakerCard
import org.jetbrains.kotlinconf.ui.components.TopMenuButton
import org.jetbrains.kotlinconf.ui.theme.KotlinConfTheme
import org.koin.compose.viewmodel.koinViewModel
import kotlinconfapp.ui_components.generated.resources.Res as UiRes

@Composable
fun SpeakersScreen(
    onSpeaker: (SpeakerId) -> Unit,
    viewModel: SpeakersViewModel = koinViewModel()
) {
    var searchState by rememberSaveable { mutableStateOf(MainHeaderContainerState.Title) }
    var searchText by rememberSaveable { mutableStateOf("") }
    val speakers by viewModel.speakers.collectAsState()

    LaunchedEffect(searchText) {
        viewModel.setSearchText(searchText)
    }

    Column(Modifier.fillMaxSize()) {
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

        LazyColumn(Modifier.fillMaxSize()) {
            items(speakers) { speaker ->
                SpeakerCard(
                    name = speaker.name,
                    nameHighlights = speaker.nameHighlights,
                    title = speaker.position,
                    titleHighlights = speaker.titleHighlights,
                    photoUrl = speaker.photoUrl,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp)
                        .clip(RoundedCornerShape(8.dp)),
                    onClick = { onSpeaker(speaker.id) },
                )
            }
        }
    }
}
