package org.jetbrains.kotlinconf.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import kotlinconfapp.shared.generated.resources.Res
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.kotlinconf.SessionCardView
import org.jetbrains.kotlinconf.Speaker
import org.jetbrains.kotlinconf.AppController
import org.jetbrains.kotlinconf.theme.grey50
import org.jetbrains.kotlinconf.theme.grey5Black
import org.jetbrains.kotlinconf.theme.grey5Grey90
import org.jetbrains.kotlinconf.theme.greyWhite
import org.jetbrains.kotlinconf.theme.t2
import org.jetbrains.kotlinconf.theme.violet
import org.jetbrains.kotlinconf.theme.white
import org.jetbrains.kotlinconf.theme.whiteGrey
import org.jetbrains.kotlinconf.ui.components.AsyncImage
import org.jetbrains.kotlinconf.ui.components.TabButton

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

data class TagView(
    val name: String,
    val isActive: Boolean
)

@OptIn(ExperimentalResourceApi::class)
@Composable
fun Search(
    controller: AppController,
    sessions: List<SessionCardView>,
    speakers: List<Speaker>
) {
    var query by remember { mutableStateOf("") }
    var selectedTab by remember { mutableStateOf("talks") }
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
            rightIcon = Res.drawable.close.painter(),
            onLeftClick = {},
            onRightClick = { controller.back() }
        )
        SearchField(query, onTextChange = { query = it })
        TabSelector(
            selected = selectedTab,
            values = listOf("talks", "speakers"),
            onClick = { selectedTab = it }
        )
        HDivider()
        SearchResults(selected = selectedTab, sessionResults, speakerResults, controller)
    }
}

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

private fun SessionCardView.toSearchData(query: String): SessionSearchData = SessionSearchData(
    id = id,
    description = buildAnnotatedString {
        withStyle(SpanStyle(fontWeight = FontWeight.Bold)) {
            appendWithQuery(title, query)
        }
        append(" / ")
        appendWithQuery(speakerLine, query)
        if (description.isNotBlank()) {
            append(" / ")
            appendPartWithQuery(description, query)
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
    pushStyle(SpanStyle(color = white, background = violet))
    append(value.substring(startIndex, endIndex))
    pop()
    append(value.substring(endIndex))
}

private fun AnnotatedString.Builder.appendPartWithQuery(value: String, query: String) {
    val value = value.replace('\n', ' ')
    val length: Int = minOf(50, value.length)
    if (!value.contains(query, ignoreCase = true)) {
        append(value.substring(0, length))
        return
    }

    val startIndex = value.indexOf(query, ignoreCase = true)
    val endIndex = startIndex + query.length

    val start = maxOf(0, startIndex - 25)
    val end = minOf(value.length, endIndex + 25)

    append("...")
    append(value.substring(start, startIndex))
    pushStyle(SpanStyle(color = white, background = violet))
    append(value.substring(startIndex, endIndex))
    pop()
    append(value.substring(endIndex, end))
    append("...")
}

@Composable
private fun SearchField(text: String, onTextChange: (String) -> Unit) {
    Box(
        Modifier
            .fillMaxWidth()
            .padding(0.dp)
    ) {
        Row(Modifier.fillMaxWidth()) {
            TextField(
                value = text,
                onValueChange = onTextChange,
                modifier = Modifier.fillMaxWidth(),
                textStyle = MaterialTheme.typography.t2.copy(
                    color = MaterialTheme.colors.greyWhite
                ),
                maxLines = 1,
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    backgroundColor = MaterialTheme.colors.grey5Black,
                    cursorColor = violet,
                    textColor = MaterialTheme.colors.greyWhite,
                    focusedBorderColor = MaterialTheme.colors.grey5Black
                ),
            )
        }
        Row(Modifier.fillMaxWidth()) {
            Spacer(Modifier.weight(1f))
            if (text.isNotEmpty()) {
                Button(
                    onClick = { onTextChange("") },
                    colors = ButtonDefaults.buttonColors(
                        backgroundColor = MaterialTheme.colors.grey5Black
                    ),
                    elevation = ButtonDefaults.elevation(0.dp, 0.dp, 0.dp, 0.dp, 0.dp),
                ) {
                    Text(
                        "CLEAR",
                        style = MaterialTheme.typography.t2.copy(color = MaterialTheme.colors.greyWhite),
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun Tags(tags: List<TagView>, onClick: (tag: TagView) -> Unit) {
    Row(
        Modifier
            .horizontalScroll(rememberScrollState())
            .background(MaterialTheme.colors.grey5Black)
            .padding(top = 8.dp, bottom = 8.dp)
    ) {
        Spacer(Modifier.width(16.dp))
        tags.forEach { tag ->
            Tag(name = tag.name, isActive = tag.isActive, onClick = { onClick(tag) })
            Spacer(Modifier.width(8.dp))
        }
        Spacer(Modifier.width(16.dp))
    }
}

@Composable
private fun Tag(name: String, isActive: Boolean, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        elevation = ButtonDefaults.elevation(0.dp, 0.dp, 0.dp, 0.dp, 0.dp),
        colors = ButtonDefaults.buttonColors(
            backgroundColor = MaterialTheme.colors.whiteGrey,
            contentColor = if (isActive) violet else grey50
        ),
    ) {
        Text(name, style = MaterialTheme.typography.t2)
    }
}

@Composable
private fun TabSelector(selected: String, values: List<String>, onClick: (String) -> Unit) {
    Row(
        Modifier
            .background(MaterialTheme.colors.whiteGrey)
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        values.forEach { value ->
            TabButton(
                tab = value,
                isSelected = value == selected,
                onSelect = { onClick(value) }
            )
        }
    }
}

@Composable
private fun SearchResults(
    selected: String,
    talks: List<SessionSearchData>,
    speakers: List<SpeakerSearchData>,
    controller: AppController
) {
    LazyColumn(Modifier.fillMaxWidth()) {
        if (selected == "speakers") {
            items(speakers) { speaker ->
                SpeakerSearchResult(
                    speaker.photoUrl,
                    speaker.description,
                    tags = speaker.tags,
                    onClick = { controller.showSpeaker(speaker.id) }
                )
            }
        } else {
            items(talks) { session ->
                TalkSearchResult(
                    session.timeLine,
                    session.description,
                    tags = session.tags,
                    onClick = { controller.showSession(session.id) }
                )
            }
        }
    }
}

@Composable
private fun TalkSearchResult(
    timeLine: String,
    text: AnnotatedString,
    tags: List<TagView>, onClick: () -> Unit
) {
    Column(
        Modifier
            .background(MaterialTheme.colors.whiteGrey)
            .clickable { onClick() }
            .fillMaxWidth()
    ) {
        Column(Modifier.padding(16.dp)) {
            Row(
                Modifier
                    .fillMaxWidth()
            ) {
                Text(
                    timeLine, style = MaterialTheme.typography.t2.copy(
                        color = grey50
                    ),
                    maxLines = 1
                )
            }
            Text(text = text, style = MaterialTheme.typography.t2)
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
private fun SearchResultTag(tag: TagView) {
    Column(
        Modifier
            .clip(shape = RoundedCornerShape(4.dp))
            .background(MaterialTheme.colors.grey5Grey90)
    ) {
        Text(
            tag.name,
            style = MaterialTheme.typography.t2.copy(color = if (tag.isActive) violet else grey50),
            modifier = Modifier.padding(
                start = 4.dp,
                end = 4.dp,
                top = 2.dp,
                bottom = 2.dp
            )
        )
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
                Text(text = text, style = MaterialTheme.typography.t2)
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
