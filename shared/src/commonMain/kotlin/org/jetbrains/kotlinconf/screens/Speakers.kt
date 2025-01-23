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
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import kotlinconfapp.shared.generated.resources.Res
import kotlinconfapp.shared.generated.resources.speakers_title
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.kotlinconf.ui.components.Divider
import org.jetbrains.kotlinconf.ui.components.MainHeaderTitleBar
import org.jetbrains.kotlinconf.ui.theme.KotlinConfTheme

@Composable
fun Speakers(
    speakers: Speakers,
    onSpeaker: (SpeakerId) -> Unit,
) {
    Column(Modifier.fillMaxSize()) {
        MainHeaderTitleBar(stringResource(Res.string.speakers_title))
        Divider(1.dp, KotlinConfTheme.colors.strokePale)

        LazyColumn(Modifier.fillMaxSize()) {
            items(speakers.all) { speaker ->
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
