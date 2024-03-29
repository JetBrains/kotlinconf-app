package org.jetbrains.kotlinconf.ui

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.layout.*
import androidx.compose.ui.unit.*
import kotlinconfapp.shared.generated.resources.Res
import kotlinconfapp.shared.generated.resources.bookmark
import kotlinconfapp.shared.generated.resources.bookmark_active
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.kotlinconf.*
import org.jetbrains.kotlinconf.AppController
import org.jetbrains.kotlinconf.ui.components.AsyncImage
import org.jetbrains.kotlinconf.ui.components.NavigationBar
import org.jetbrains.kotlinconf.ui.components.Room
import org.jetbrains.kotlinconf.ui.components.RoomMap
import org.jetbrains.kotlinconf.ui.components.Tag
import org.jetbrains.kotlinconf.ui.components.VoteAndFeedback
import org.jetbrains.kotlinconf.ui.theme.grey50Grey20
import org.jetbrains.kotlinconf.ui.theme.grey5Black
import org.jetbrains.kotlinconf.ui.theme.greyGrey20
import org.jetbrains.kotlinconf.ui.theme.greyGrey5
import org.jetbrains.kotlinconf.ui.theme.greyWhite
import org.jetbrains.kotlinconf.ui.theme.orange
import org.jetbrains.kotlinconf.ui.theme.whiteGrey

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun SessionScreen(
    id: String,
    time: String,
    title: String,
    description: String,
    location: String,
    isFavorite: Boolean,
    vote: Score?,
    isFinished: Boolean,
    speakers: List<Speaker>,
    tags: List<String>,
    controller: AppController
) {
    var showFeedbackBlock by remember { mutableStateOf(false) }

    val session = @Composable {
        SessionContent(
            time = time,
            title = title,
            description = description,
            location = location,
            isFavorite = isFavorite,
            speakers = speakers,
            tags = tags,
            onFavoriteClick = { controller.toggleFavorite(id) },
            onSpeakerClick = { speakerId -> controller.showSpeaker(speakerId) }
        ) { controller.back() }
    }

    if (isFinished) {
        BottomSheetScaffold(
            sheetContent = {
                Column {
                    Spacer(Modifier.height(4.dp))
                    SheetBar()
                    VoteAndFeedback(
                        vote = vote,
                        showFeedbackBlock = showFeedbackBlock,
                        onVote = {
                            controller.vote(id, it)
                            showFeedbackBlock = true
                        },
                        onSubmitFeedback = {
                            controller.sendFeedback(id, it)
                            showFeedbackBlock = false
                        }, onCloseFeedback = {
                            showFeedbackBlock = false
                        }
                    )

                    Spacer(Modifier.height(4.dp))
                }
            },
            sheetPeekHeight = 100.dp,
            sheetBackgroundColor = MaterialTheme.colors.whiteGrey,
        ) {
            session()
        }
    } else {
        session()
    }
}

@OptIn(ExperimentalResourceApi::class)
@Composable
private fun SessionContent(
    time: String,
    title: String,
    description: String,
    location: String,
    isFavorite: Boolean,
    speakers: List<Speaker>,
    tags: List<String>,
    onFavoriteClick: () -> Unit,
    onSpeakerClick: (String) -> Unit,
    onBackClick: () -> Unit
) {
    Column(Modifier.fillMaxWidth()) {
        NavigationBar(
            title = "",
            isLeftVisible = true,
            onLeftClick = onBackClick,
            isRightVisible = false,
        )
        Column(
            Modifier
                .verticalScroll(rememberScrollState())
                .fillMaxWidth()
                .fillMaxHeight()
                .background(MaterialTheme.colors.whiteGrey)
        ) {
            SessionHead(time, title, isFavorite, onFavoriteClick, onSpeakerClick, speakers)
            SessionDetails(description, location, tags)
        }
    }
}

@Composable
private fun SessionHead(
    time: String,
    title: String,
    isFavorite: Boolean,
    favoriteClick: () -> Unit,
    speakerClick: (String) -> Unit,
    speakers: List<Speaker>
) {
    Column(
        Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colors.grey5Black)
    ) {
        Column(Modifier.padding(start = 16.dp, end = 16.dp)) {
            Row {
                Text(
                    modifier = Modifier.padding(top = 12.dp),
                    text = time,
                    style = MaterialTheme.typography.body2.copy(
                        color = MaterialTheme.colors.grey50Grey20
                    ),
                )
                Spacer(modifier = Modifier.weight(1f))

                FavoriteButton(isFavorite, favoriteClick)
            }
            Text(
                title,
                style = MaterialTheme.typography.h2.copy(
                    color = MaterialTheme.colors.greyWhite
                )
            )

            speakers.forEach {
                Text(
                    it.name,
                    style = MaterialTheme.typography.body2.copy(
                        color = MaterialTheme.colors.greyGrey20
                    )
                )
            }
        }
        SpeakerPhotos(speakers, speakerClick)
        HDivider()
    }
}

@OptIn(ExperimentalResourceApi::class)
@Composable
private fun FavoriteButton(isFavorite: Boolean, favoriteClick: () -> Unit) {
    IconButton(onClick = favoriteClick) {
        val icon = if (isFavorite) Res.drawable.bookmark_active else Res.drawable.bookmark
        Icon(
            painter = icon.painter(),
            contentDescription = "favorite",
            tint = if (isFavorite) orange else MaterialTheme.colors.greyGrey5
        )
    }
}

@Composable
private fun SpeakerPhotos(speakers: List<Speaker>, speakerClick: (String) -> Unit) {
    Row(Modifier.padding(top = 24.dp, bottom = 0.dp, start = 16.dp, end = 16.dp)) {
        speakers.forEach { speaker ->
            AsyncImage(
                modifier = Modifier
                    .size(if (speakers.size < 3) 120.dp else 78.dp)
                    .padding(end = 8.dp)
                    .clickable { speakerClick(speaker.id) },
                imageUrl = speaker.photoUrl,
                contentDescription = speaker.name,
                contentScale = ContentScale.Crop,
            )
        }
    }
}

@OptIn(ExperimentalLayoutApi::class, ExperimentalResourceApi::class)
@Composable
private fun SessionDetails(
    description: String,
    location: String,
    tags: List<String>
) {
    Column(Modifier.background(MaterialTheme.colors.whiteGrey)) {
        FlowRow(
            Modifier
                .padding(16.dp)
                .fillMaxWidth()
        ) {
            tags.forEach {
                Tag(null, it, modifier = Modifier.padding(end = 4.dp))
            }
        }

        Column(Modifier.padding(16.dp)) {
            Text(
                description,
                style = MaterialTheme.typography.body2.copy(
                    color = MaterialTheme.colors.greyGrey20
                )
            )
            LocationRow(location, Modifier.padding(top = 24.dp, bottom = 48.dp))
            val room = Room.forName(location)
            if (room != null) {
                RoomMap(room)
            }
        }
    }
}

