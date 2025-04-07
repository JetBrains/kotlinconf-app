package org.jetbrains.kotlinconf.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import coil3.compose.LocalPlatformContext
import coil3.request.ImageRequest
import coil3.request.crossfade
import kotlinconfapp.ui_components.generated.resources.Res
import kotlinconfapp.ui_components.generated.resources.kodee_emotion_neutral
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.jetbrains.kotlinconf.ui.theme.KotlinConfTheme
import org.jetbrains.kotlinconf.ui.theme.PreviewHelper

@Composable
fun SpeakerCard(
    name: String,
    title: String,
    photoUrl: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    nameHighlights: List<IntRange> = emptyList(),
    titleHighlights: List<IntRange> = emptyList(),
) {
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .clickable(onClick = onClick),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        SpeakerAvatar(
            photoUrl = photoUrl,
            modifier = Modifier.size(96.dp),
        )
        Column {
            Text(
                text = buildHighlightedString(name, nameHighlights),
                style = KotlinConfTheme.typography.h3,
                color = KotlinConfTheme.colors.primaryText,
            )
            Spacer(modifier = Modifier.size(6.dp))
            Text(
                text = buildHighlightedString(title, titleHighlights),
                style = KotlinConfTheme.typography.text2,
                color = KotlinConfTheme.colors.secondaryText,
            )
        }
    }
}

@Composable
fun SpeakerAvatar(
    photoUrl: String,
    modifier: Modifier = Modifier,
) {
    AsyncImage(
        model = ImageRequest.Builder(LocalPlatformContext.current)
            .data(photoUrl)
            .crossfade(true)
            .build(),
        contentDescription = null,
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(KotlinConfTheme.colors.tileBackground),
        contentScale = ContentScale.Crop,
        error = painterResource(Res.drawable.kodee_emotion_neutral),
    )
}

@Preview
@Composable
internal fun SpeakerCardPreview() {
    PreviewHelper {
        SpeakerCard(
            name = "John Doe",
            title = "Whatever Role Name at That Company",
            photoUrl = "https://example.com/not-an-image.jpg",
            onClick = {},
        )
        SpeakerCard(
            name = "John Doe",
            nameHighlights = listOf(0..3),  // Highlight "John"
            title = "Whatever Role Name at That Company",
            titleHighlights = listOf(9..12),  // Highlight "Role"
            photoUrl = "https://sessionize.com/image/2e2f-0o0o0-XGxKBoqZvxxQxosrZHQHTT.png?download=sebastian-aigner.png",
            onClick = {},
        )
    }
}
