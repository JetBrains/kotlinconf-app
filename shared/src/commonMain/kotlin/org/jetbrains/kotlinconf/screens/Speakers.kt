package org.jetbrains.kotlinconf.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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
import org.jetbrains.kotlinconf.Speakers
import org.jetbrains.kotlinconf.ui.components.Divider
import org.jetbrains.kotlinconf.ui.components.MainHeaderContainer
import org.jetbrains.kotlinconf.ui.components.MainHeaderContainerState
import org.jetbrains.kotlinconf.ui.components.MainHeaderSearchBar
import org.jetbrains.kotlinconf.ui.components.MainHeaderTitleBar
import org.jetbrains.kotlinconf.ui.components.TopMenuButton
import org.jetbrains.kotlinconf.ui.theme.KotlinConfTheme
import org.jetbrains.kotlinconf.utils.containsDiacritics
import org.jetbrains.kotlinconf.utils.removeDiacritics
import kotlinconfapp.ui_components.generated.resources.Res as UiRes

@Composable
fun Speakers(
    speakers: Speakers,
    onSpeaker: (SpeakerId) -> Unit,
) {
    var searchState by remember { mutableStateOf(MainHeaderContainerState.Title) }
    var searchText by remember { mutableStateOf("") }

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

        val filtered = speakers.all.filter {
            // Look for exact matches if diacritics are present, ignore all diacritics otherwise
            val diacriticsSearch = searchText.containsDiacritics()
            val targetName = if (diacriticsSearch) it.name else it.name.removeDiacritics()
            val targetPosition = if (diacriticsSearch) it.position else it.position.removeDiacritics()
            val searchPattern = searchText.toRegex(RegexOption.IGNORE_CASE)

            searchText.isEmpty() || searchPattern.containsMatchIn(targetName) || searchPattern.containsMatchIn(targetPosition)
        }

        LazyColumn(Modifier.fillMaxSize()) {
            items(filtered) { speaker ->
                org.jetbrains.kotlinconf.ui.components.Speaker(
                    name = speaker.name,
                    title = speaker.position,
                    photoUrl = speaker.photoUrl,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .clickable { onSpeaker(speaker.id) }
                )
            }
        }
    }
}
