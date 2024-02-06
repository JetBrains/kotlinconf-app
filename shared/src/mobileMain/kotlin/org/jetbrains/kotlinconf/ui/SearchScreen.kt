package org.jetbrains.kotlinconf.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import kotlinconfapp.shared.generated.resources.Res
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.kotlinconf.AppController
import org.jetbrains.kotlinconf.SessionCardView
import org.jetbrains.kotlinconf.Speaker
import org.jetbrains.kotlinconf.theme.*
import org.jetbrains.kotlinconf.ui.components.*

data class SessionSearchData(
    val id: String,
    val description: AnnotatedString,
    val tags: List<TagView>,
    val timeLine: String
)

data class SpeakerSearchData(
    val id: String,
    val description: AnnotatedString,
    val photoUrl: String,
    val tags: List<TagView>
)

enum class SearchTab(val value: String) {
    TALKS("Talks"),
    SPEAKERS("Speakers")
}

@OptIn(ExperimentalResourceApi::class)
@Composable
fun SearchScreen(
    controller: AppController,
    sessions: List<SessionCardView>,
    speakers: List<Speaker>
) {
    var query by remember { mutableStateOf("") }
    var selectedTab by remember { mutableStateOf(SearchTab.TALKS) }
    val speakerResults = speakers.searchSpeakers(query)
    val sessionResults = sessions.searchSessions(query)

    Column(
        Modifier
            .background(MaterialTheme.colors.grey5Black)
            .fillMaxHeight()
    ) {
        NavigationBar(
            title = "Search",
            isLeftVisible = false,
            rightIcon = Res.drawable.close,
            onLeftClick = {},
            onRightClick = { controller.back() }
        )
        SearchField(query, onTextChange = { query = it })
        SearchSessionTags(MOCK_TAGS, onClick = {})
        HDivider()
        SearchTagSelector(
            selected = selectedTab,
            onClick = { selectedTab = it }
        )
        HDivider()
        SearchResults(selected = selectedTab, sessionResults, speakerResults, controller)
    }
}

@Composable
private fun List<SessionCardView>.searchSessions(query: String): List<SessionSearchData> =
    filterNot { it.isBreak || it.isParty || it.isLunch }
        .filter { session ->
            session.title.contains(query, ignoreCase = true) ||
                    session.description.contains(query, ignoreCase = true) ||
                    session.speakerLine.contains(query, ignoreCase = true)
        }.map { it.toSearchData(query) }

private fun List<Speaker>.searchSpeakers(query: String): List<SpeakerSearchData> =
    filter { speaker ->
        speaker.name.contains(query, ignoreCase = true) ||
                speaker.description.contains(query, ignoreCase = true)
    }.map { it.toSearchData(query) }

private fun Speaker.toSearchData(query: String): SpeakerSearchData = SpeakerSearchData(
    id = id,
    description = buildAnnotatedString {
        withStyle(SpanStyle(fontWeight = FontWeight.Bold)) {
            appendWithQuery(name, query)
        }
        append(" / ")
        appendWithQuery(position, query)

        if (description.isNotBlank() && description.contains(query, ignoreCase = true)) {
            append(" / ")
            appendPartWithQuery(description, query)
        }
    },
    photoUrl = photoUrl,
    tags = emptyList()
)

@Composable
private fun SessionCardView.toSearchData(query: String): SessionSearchData = SessionSearchData(
    id = id,
    description = buildAnnotatedString {
        withStyle(SpanStyle(color = MaterialTheme.colors.blackWhite)) {
            appendWithQuery(title, query)
        }
        append(" / ")
        withStyle(SpanStyle(color = MaterialTheme.colors.grey80Grey20)) {
            appendWithQuery(speakerLine, query)
        }
        withStyle(SpanStyle(color = grey50)) {
            if (description.isNotBlank()) {
                append(" / ")
                appendPartWithQuery(description, query)
            }
        }
    },
    tags = emptyList(),
    timeLine = timeLine
)

private fun AnnotatedString.Builder.appendWithQuery(value: String, query: String) {
    if (!value.contains(query, ignoreCase = true)) {
        append(value)
        return
    }

    val startIndex = value.indexOf(query, ignoreCase = true)
    val endIndex = startIndex + query.length

    append(value.substring(0, startIndex))
    pushStyle(SpanStyle(color = white, background = orange))
    append(value.substring(startIndex, endIndex))
    pop()
    append(value.substring(endIndex))
}

private fun AnnotatedString.Builder.appendPartWithQuery(value: String, query: String) {
    val value = value.replace('\n', ' ')
    val length: Int = minOf(150, value.length)
    if (!value.contains(query, ignoreCase = true)) {
        append(value.substring(0, length))
        return
    }

    val startIndex = value.indexOf(query, ignoreCase = true)
    val endIndex = startIndex + query.length

    val start = maxOf(0, startIndex - 75)
    val end = minOf(value.length, endIndex + 75)

    append("...")
    append(value.substring(start, startIndex))
    pushStyle(SpanStyle(color = white, background = orange))
    append(value.substring(startIndex, endIndex))
    pop()
    append(value.substring(endIndex, end))
    append("...")
}


@Composable
fun SearchTagSelector(selected: SearchTab, onClick: (SearchTab) -> Unit) {
    Row(
        Modifier
            .background(MaterialTheme.colors.whiteGrey)
            .fillMaxWidth()
            .padding(start = 12.dp, top = 16.dp, bottom = 16.dp)
    ) {
        SearchTab.entries.forEach { entry ->
            TabButton(
                tab = entry.value,
                isSelected = entry == selected,
                onSelect = { onClick(entry) }
            )
        }
    }
}

@Composable
private fun SearchResults(
    selected: SearchTab,
    talks: List<SessionSearchData>,
    speakers: List<SpeakerSearchData>,
    controller: AppController
) {
    LazyColumn(Modifier.fillMaxWidth()) {
        when (selected) {
            SearchTab.SPEAKERS -> items(speakers) { speaker ->
                SpeakerSearchResult(
                    speaker.photoUrl,
                    speaker.description,
                    tags = speaker.tags,
                    onClick = { controller.showSpeaker(speaker.id) }
                )
            }
            SearchTab.TALKS -> items(talks) { session ->
                TalkSearchResult(
                    session.description,
                    tags = session.tags
                ) { controller.showSession(session.id) }
            }
        }
    }
}

@Composable
private fun TalkSearchResult(
    text: AnnotatedString,
    tags: List<TagView>,
    onClick: () -> Unit
) {
    Column(
        Modifier
            .background(MaterialTheme.colors.whiteGrey)
            .clickable { onClick() }
            .fillMaxWidth()
    ) {
        Column(Modifier.padding(16.dp)) {
            Text(text = text, style = MaterialTheme.typography.body2)
            Spacer(Modifier.height(8.dp))
            Row {
                tags.forEach { tag ->
                    SearchResultTag(tag)
                    Spacer(Modifier.width(8.dp))
                }
            }
        }
        HDivider()
    }
}

@Composable
private fun SpeakerSearchResult(
    photoUrl: String,
    text: AnnotatedString,
    tags: List<TagView>,
    onClick: () -> Unit
) {
    Column(
        Modifier
            .background(MaterialTheme.colors.whiteGrey)
            .clickable { onClick() }) {
        Row {
            AsyncImage(
                imageUrl = photoUrl,
                contentDescription = "avatar",
                modifier = Modifier.size(60.dp)
            )
            Column(Modifier.padding(16.dp)) {
                Text(text = text, style = MaterialTheme.typography.body2)
                Spacer(Modifier.height(8.dp))
                Row {
                    tags.forEach { tag ->
                        SearchResultTag(tag)
                        Spacer(Modifier.width(8.dp))
                    }
                }
            }
        }
        HDivider()
    }
}
