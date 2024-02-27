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
import org.jetbrains.kotlinconf.*
import org.jetbrains.kotlinconf.AppController
import org.jetbrains.kotlinconf.ui.theme.subtitle
import org.jetbrains.kotlinconf.ui.theme.title
import org.jetbrains.kotlinconf.ui.theme.whiteGrey
import org.jetbrains.kotlinconf.ui.components.AsyncImage
import org.jetbrains.kotlinconf.ui.components.NavigationBar

@OptIn(ExperimentalResourceApi::class)
@Composable
fun SpeakersView(speakers: List<Speaker>, controller: AppController) {
    Column(Modifier.background(MaterialTheme.colors.whiteGrey)) {
        NavigationBar(
            title = "Speakers",
            isLeftVisible = false,
            isRightVisible = false
        )

        LazyColumn(Modifier.background(MaterialTheme.colors.whiteGrey)) {
            items(speakers) { speaker ->
                Column(
                    Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colors.whiteGrey)
                ) {
                    KeynoteSectionSpeakerCard(
                        name = speaker.name,
                        position = speaker.position,
                        photoUrl = speaker.photoUrl,
                        onClick = {
                            controller.showSpeaker(speaker.id)
                        }
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun KeynoteSectionSpeakerCard(
    name: String,
    position: String,
    photoUrl: String,
    onClick: () -> Unit = {}
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colors.whiteGrey)
            .padding(0.dp),
        onClick = onClick
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colors.whiteGrey)
        ) {
            Row(modifier = Modifier.fillMaxWidth()) {
                AsyncImage(
                    modifier = Modifier
                        .size(85.dp)
                        .padding(0.dp),
                    imageUrl = photoUrl,
                    contentDescription = name,
                    contentScale = ContentScale.Crop,
                )
                Column(
                    modifier = Modifier
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                ) {
                    Text(
                        text = name,
                        style = MaterialTheme.typography.h4.copy(color = MaterialTheme.colors.title),
                        maxLines = 1
                    )
                    Text(
                        text = position,
                        style = MaterialTheme.typography.body2.copy(color = MaterialTheme.colors.subtitle),
                        maxLines = 1
                    )
                }
            }

            HDivider()
        }
    }
}
