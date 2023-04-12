package org.jetbrains.kotlinconf.android.ui

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.geometry.*
import androidx.compose.ui.layout.*
import androidx.compose.ui.tooling.preview.*
import androidx.compose.ui.unit.*
import coil.compose.*
import org.jetbrains.kotlinconf.*
import org.jetbrains.kotlinconf.android.*
import org.jetbrains.kotlinconf.android.theme.*

@Composable
fun Speakers(speakers: List<Speaker>, controller: AppController) {
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
                    SpeakerCard(
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
private fun SpeakerCard(
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
                    model = photoUrl,
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

@Composable
@Preview
private fun SpeakerCardPreview() {
    KotlinConfTheme {
        SpeakerCard(
            name = "Roman Elizarov",
            position = "JetBrains",
            photoUrl = "https://sessionize.com/image/5e77-400o400o2-c3-27df-4756-b04a-76b2d6f220c4.8fd59ff1-977b-49ce-af6e-15659c2a9d9b.jpg"
        )
    }
}