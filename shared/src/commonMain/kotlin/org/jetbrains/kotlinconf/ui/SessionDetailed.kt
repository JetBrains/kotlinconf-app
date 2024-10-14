package org.jetbrains.kotlinconf.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.BottomSheetScaffold
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.rememberBottomSheetScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import kotlinconfapp.shared.generated.resources.Res
import kotlinconfapp.shared.generated.resources.bookmark
import kotlinconfapp.shared.generated.resources.bookmark_active
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.kotlinconf.AppController
import org.jetbrains.kotlinconf.Score
import org.jetbrains.kotlinconf.Speaker
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
import org.jetbrains.kotlinconf.utils.Screen
import org.jetbrains.kotlinconf.utils.isTooWide

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
    var showBottomSheet by remember { mutableStateOf(true) }

    val session = @Composable {
        SessionContent(time = time,
            title = title,
            description = description,
            location = location,
            isFavorite = isFavorite,
            speakers = speakers,
            tags = tags,
            isFinished = isFinished,
            onFavoriteClick = { controller.toggleFavorite(id) },
            onSpeakerClick = { speakerId -> controller.showSpeaker(speakerId) }) { controller.back() }
    }

    if (isFinished && showBottomSheet) {
        val scaffoldState = rememberBottomSheetScaffoldState()
        val context = rememberCoroutineScope()

        BottomSheetScaffold(
            scaffoldState = scaffoldState,
            sheetContent = {
                Column {
                    Spacer(Modifier.height(4.dp))
                    SheetBar()
                    VoteAndFeedback(vote = vote, showFeedbackBlock = true, onVote = {
                        controller.vote(id, it)

                        context.launch {
                            scaffoldState.bottomSheetState.expand()
                        }
                    }, onSubmitFeedback = {
                        controller.sendFeedback(id, it)
                        showBottomSheet = false
                    }, onCloseFeedback = {
                        showBottomSheet = false
                    })

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
    isFinished: Boolean,
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
            Modifier.verticalScroll(rememberScrollState()).fillMaxWidth().fillMaxHeight()
                .background(MaterialTheme.colors.whiteGrey)
        ) {
            SessionHead(time, title, isFavorite, onFavoriteClick, onSpeakerClick, speakers)
            SessionDetails(description, location, tags, isFinished)
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
        Modifier.fillMaxWidth().background(MaterialTheme.colors.grey5Black)
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
                title, style = MaterialTheme.typography.h2.copy(
                    color = MaterialTheme.colors.greyWhite
                )
            )

            speakers.forEach {
                Text(
                    it.name, style = MaterialTheme.typography.body2.copy(
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
    val screenSizeIsTooWide = Screen.isTooWide()
    val smallImageSize = if (screenSizeIsTooWide) 156.dp else 78.dp
    val largeImageSize = if (screenSizeIsTooWide) 240.dp else 120.dp

    Row(
        Modifier.padding(top = 24.dp, bottom = 0.dp, start = 16.dp, end = 16.dp)
            .horizontalScroll(rememberScrollState())
    ) {
        speakers.forEach { speaker ->
            AsyncImage(
                modifier = Modifier.size(if (speakers.size < 3) largeImageSize else smallImageSize)
                    .padding(end = 8.dp).clickable { speakerClick(speaker.id) },
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
    description: String, location: String, tags: List<String>, isFinished: Boolean
) {
    Column(Modifier.background(MaterialTheme.colors.whiteGrey)) {
        FlowRow(
            Modifier.padding(12.dp).fillMaxWidth()
        ) {
            tags.forEach {
                Tag(null, it, modifier = Modifier.padding(top=4.dp, end = 4.dp))
            }
        }

        Column(Modifier.padding(16.dp)) {
            Text(
                description, style = MaterialTheme.typography.body2.copy(
                    color = MaterialTheme.colors.greyGrey20
                )
            )
            LocationRow(location, Modifier.padding(top = 24.dp, bottom = 48.dp))
            val room = Room.forName(location)
            if (room != null) {
                RoomMap(room)
            }
            if (isFinished) {
                Spacer(Modifier.height(100.dp))
            }
        }
    }
}

