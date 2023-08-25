package org.jetbrains.kotlinconf.ui

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.layout.*
import androidx.compose.ui.unit.*
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.kotlinconf.*
import org.jetbrains.kotlinconf.org.jetbrains.kotlinconf.AppController
import org.jetbrains.kotlinconf.theme.*
import org.jetbrains.kotlinconf.ui.components.AWSLab
import org.jetbrains.kotlinconf.ui.components.AsyncImage
import org.jetbrains.kotlinconf.ui.components.CodeLab
import org.jetbrains.kotlinconf.ui.components.LightningTalk

@Composable
fun SpeakersFlow(
    controller: AppController,
    speakers: List<Speaker>,
    focusedSpeakerId: String
) {
    val state = rememberLazyListState()
    if (focusedSpeakerId.isNotEmpty()) {
        val index = speakers.indexOfFirst { it.id == focusedSpeakerId }
        if (index >= 0) {
            LaunchedEffect(Unit) {
                state.scrollToItem(index)
            }
        }
    }

    Column(Modifier.fillMaxWidth()) {
        NavigationBar(title = "SPEAKERS", isLeftVisible = true, onLeftClick = {
            controller.back()
        }, isRightVisible = false)
        LazyColumn(state = state) {
            items(speakers) {
                val sessions = controller.sessionsForSpeaker(it.id)
                SpeakerDetailed(
                    it.name,
                    it.position,
                    it.photoUrl,
                    it.description,
                    sessions,
                    showSession = { session ->
                        controller.showSession(session)
                    },
                    onFavoriteClick = { session ->
                        controller.toggleFavorite(session.id)
                    }
                )
            }
        }
    }
}

@Composable
private fun SpeakerDetailed(
    name: String,
    position: String,
    photoUrl: String,
    description: String,
    sessions: List<SessionCardView>,
    showSession: (String) -> Unit,
    onFavoriteClick: (SessionCardView) -> Unit = {},
) {
    Column(
        Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colors.grey5Black)
    ) {
        HDivider()
        Column(Modifier.padding(16.dp)) {
            Text(
                name.uppercase(), style = MaterialTheme.typography.h2.copy(
                    color = MaterialTheme.colors.greyGrey5
                )
            )

            Text(
                modifier = Modifier.padding(top = 16.dp),
                text = position,
                style = MaterialTheme.typography.t2.copy(
                    color = MaterialTheme.colors.greyGrey20
                )
            )
        }

        AsyncImage(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 0.dp, end = 0.dp),
            imageUrl = photoUrl,
            contentDescription = "Speaker photo",
            contentScale = ContentScale.FillWidth,
        )
        Column(
            Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colors.whiteGrey)
        ) {
            Column(Modifier.padding(16.dp)) {
                Text(
                    modifier = Modifier.padding(top = 8.dp),
                    text = description,
                    style = MaterialTheme.typography.t2.copy(
                        color = MaterialTheme.colors.greyGrey20
                    )
                )

                if (sessions.isNotEmpty()) {
                    Text(
                        "TALKS: ", style = MaterialTheme.typography.t2.copy(
                            color = grey50
                        ), modifier = Modifier.padding(top = 24.dp)
                    )
                }
            }

            sessions.forEach {
                SessionCardCompact(
                    it.title,
                    it.locationLine,
                    it.timeLine,
                    it.isCodeLab,
                    it.isLightning,
                    it.isAWSLab,
                    it.isFavorite,
                    onFavoriteClick = { onFavoriteClick(it) },
                    onSessionClick = { showSession(it.id) }
                )
            }
        }
    }
}

@OptIn(ExperimentalResourceApi::class)
@Composable
private fun SessionCardCompact(
    title: String,
    locationLine: String,
    timeLine: String,
    isCodeLab: Boolean,
    isLightning: Boolean,
    isAWSLab: Boolean,
    favorite: Boolean,
    onSessionClick: () -> Unit,
    onFavoriteClick: () -> Unit,
) {
    Column(
        Modifier
            .background(MaterialTheme.colors.whiteGrey)
            .clickable {
                onSessionClick()
            }
    ) {
        HDivider()
        Box(Modifier.fillMaxWidth()) {
            Column(
                modifier = Modifier
                    .padding(
                        start = 16.dp,
                        end = 48.dp,
                        top = 16.dp,
                        bottom = 16.dp
                    )
                    .fillMaxWidth()
            ) {
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
                Text(
                    title,
                    style = MaterialTheme.typography.h4.copy(
                        color = MaterialTheme.colors.primary
                    )
                )
                LocationRow(location = locationLine, modifier = Modifier.padding(top = 16.dp))
                Row(
                    Modifier
                        .fillMaxWidth()
                ) {
                    if (isLightning) {
                        LightningTalk("Lightning Talk", dimmed = false)
                    } else if (isCodeLab) {
                        CodeLab(dimmed = false)
                    } else if (isAWSLab) {
                        AWSLab(dimmed = false)
                    }
                }
            }
            Row {
                Spacer(modifier = Modifier.weight(1.0f))
                IconButton(onClick = {
                    onFavoriteClick()
                }) {
                    val icon = if (favorite) "bookmark_active.xml" else "bookmark.xml"
                    Icon(
                        painter = painterResource(icon),
                        contentDescription = "bookmark",
                        tint = if (favorite) orange else MaterialTheme.colors.greyWhite
                    )
                }
            }
        }
    }
}
