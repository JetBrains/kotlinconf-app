package org.jetbrains.kotlinconf.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import kotlinconfapp.shared.generated.resources.*
import kotlinconfapp.shared.generated.resources.Res
import kotlinconfapp.shared.generated.resources.speakers
import kotlinconfapp.shared.generated.resources.talks
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.kotlinconf.AppController
import org.jetbrains.kotlinconf.SessionCardView
import org.jetbrains.kotlinconf.Speaker
import org.jetbrains.kotlinconf.ui.components.*
import org.jetbrains.kotlinconf.ui.theme.blackWhite
import org.jetbrains.kotlinconf.ui.theme.grey50
import org.jetbrains.kotlinconf.ui.theme.grey5Black
import org.jetbrains.kotlinconf.ui.theme.grey80Grey20
import org.jetbrains.kotlinconf.ui.theme.greyGrey5
import org.jetbrains.kotlinconf.ui.theme.orange
import org.jetbrains.kotlinconf.ui.theme.white
import org.jetbrains.kotlinconf.ui.theme.whiteGrey

data class SessionSearchData(
    val id: String,
    val description: AnnotatedString,
    val tags: List<String>,
    val timeLine: String
)

data class SpeakerSearchData(
    val id: String,
    val description: AnnotatedString,
    val photoUrl: String,
)

@OptIn(ExperimentalResourceApi::class)
enum class SearchTab(override val title: StringResource) : Tab {
    TALKS(Res.string.talks),
    SPEAKERS(Res.string.speakers)
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
    val tags = sessions.flatMap { it.tags }.distinct()
    val activeTags = remember { mutableStateListOf<String>() }
    val sessionResults = sessions.searchSessions(query, activeTags)

    Column(
        Modifier
            .background(MaterialTheme.colors.grey5Black)
            .fillMaxHeight()
    ) {
        Box {
            TabBar(
                SearchTab.entries,
                selectedTab, onSelect = {
                    selectedTab = it
                }
            )
            Row(horizontalArrangement = Arrangement.Start, modifier = Modifier.fillMaxWidth()) {
                IconButton(onClick = { controller.back() }) {
                    Icon(
                        painter = Res.drawable.back.painter(),
                        "Back",
                        tint = MaterialTheme.colors.greyGrey5
                    )
                }
            }
        }
        SearchField(query, onTextChange = { query = it })
        HDivider()
        if (selectedTab == SearchTab.TALKS) {
            SearchSessionTags(tags, activeTags, onClick = {
                if (it in activeTags) {
                    activeTags.remove(it)
                } else {
                    activeTags.add(it)
                }
            })
            HDivider()
        }

        SearchResults(
            selected = selectedTab,
            sessionResults,
            speakerResults,
            activeTags,
            controller
        )
    }
}

@Composable
private fun List<SessionCardView>.searchSessions(
    query: String,
    activeTags: SnapshotStateList<String>
): List<SessionSearchData> {
    var result = this
    if (activeTags.isNotEmpty()) {
        result = result.filter { session -> session.tags.any { it in activeTags } }
    }
    return result.filterNot { it.isBreak || it.isParty || it.isLunch }
        .filter { session ->
            session.title.contains(query, ignoreCase = true) ||
                    session.description.contains(query, ignoreCase = true) ||
                    session.speakerLine.contains(query, ignoreCase = true)
        }.map { it.toSearchData(query) }
}

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
    tags = tags,
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
                tab = entry,
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
    activeTags: List<String>,
    controller: AppController
) {
    LazyColumn(Modifier.fillMaxWidth()) {
        when (selected) {
            SearchTab.SPEAKERS -> items(speakers) { speaker ->
                SpeakerSearchResult(
                    speaker.photoUrl,
                    speaker.description,
                    onClick = { controller.showSpeaker(speaker.id) }
                )
            }

            SearchTab.TALKS -> items(talks) { session ->
                TalkSearchResult(
                    session.description,
                    tags = session.tags,
                    activeTags = activeTags,
                ) { controller.showSession(session.id) }
            }
        }
    }
}

@OptIn(ExperimentalResourceApi::class, ExperimentalLayoutApi::class)
@Composable
private fun TalkSearchResult(
    text: AnnotatedString,
    tags: List<String>,
    activeTags: List<String>,
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
            FlowRow {
                activeTags.forEach { tag ->
                    if (tag !in tags) return@forEach
                    Tag(
                        icon = null,
                        tag,
                        modifier = Modifier.padding(end = 4.dp),
                        isActive = true
                    )
                }
                tags.forEach { tag ->
                    if (tag in activeTags) return@forEach
                    Tag(
                        icon = null,
                        tag,
                        modifier = Modifier.padding(end = 4.dp),
                        isActive = false
                    )
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
            }
        }
        HDivider()
    }
}
