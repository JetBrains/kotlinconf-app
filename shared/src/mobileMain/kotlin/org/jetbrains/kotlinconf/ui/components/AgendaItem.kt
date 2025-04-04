package org.jetbrains.kotlinconf.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import kotlinconfapp.shared.generated.resources.Res
import kotlinconfapp.shared.generated.resources.bookmark
import kotlinconfapp.shared.generated.resources.bookmark_active
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.kotlinconf.Score
import org.jetbrains.kotlinconf.ui.HDivider
import org.jetbrains.kotlinconf.ui.painter
import org.jetbrains.kotlinconf.ui.theme.grey50
import org.jetbrains.kotlinconf.ui.theme.greyGrey5
import org.jetbrains.kotlinconf.ui.theme.greyWhite
import org.jetbrains.kotlinconf.ui.theme.orange
import org.jetbrains.kotlinconf.ui.theme.whiteGrey

@Composable
private fun TagChip(tag: String) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colors.greyGrey5.copy(alpha = 0.2f),
        modifier = Modifier.height(24.dp)
    ) {
        Text(
            text = tag,
            style = MaterialTheme.typography.caption.copy(
                color = MaterialTheme.colors.greyWhite
            ),
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}


@OptIn(ExperimentalResourceApi::class)
@Composable
fun AgendaItem(
    title: String,
    speakerLine: String,
    locationLine: String,
    timeLine: String,
    isFavorite: Boolean,
    isFinished: Boolean,
    isLightning: Boolean,
    vote: Score?,
    tags: List<String> = emptyList(),
    onSessionClick: () -> Unit = {},
    onFavoriteClick: () -> Unit = {},
    onVote: (Score?) -> Unit = {},
    onFeedback: (String) -> Unit = {}
) {
    var showFeedback by remember { mutableStateOf(false) }

    Column(Modifier.background(MaterialTheme.colors.whiteGrey).clickable {
        onSessionClick()
    }) {
        Row(Modifier.fillMaxWidth().padding(top = 16.dp, start = 16.dp, end = 16.dp)) {
            Column(
                modifier = Modifier.weight(1.0f, true)
            ) {
                Text(
                    title, 
                    style = MaterialTheme.typography.h4.copy(
                        color = if (isFinished) grey50 else MaterialTheme.colors.greyWhite
                    ),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    speakerLine,
                    style = MaterialTheme.typography.body2.copy(
                        color = if (isFinished) grey50 else MaterialTheme.colors.greyGrey5
                    ),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            if (!isFinished) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(start = 8.dp)
                ) {
                    IconButton(
                        onClick = { onFavoriteClick() },
                        modifier = Modifier.size(48.dp)
                    ) {
                        val icon =
                            if (isFavorite) Res.drawable.bookmark_active else Res.drawable.bookmark
                        Icon(
                            painter = icon.painter(),
                            contentDescription = if (isFavorite) "Remove from favorites" else "Add to favorites",
                            tint = if (isFavorite) orange else MaterialTheme.colors.greyWhite,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
            }
        }

        Column(
            Modifier.padding(horizontal = 16.dp, vertical = 8.dp).fillMaxWidth()
        ) {
            if (!isFinished) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(bottom = 8.dp)
                ) {
                    Text(
                        locationLine,
                        style = MaterialTheme.typography.body2.copy(
                            color = grey50
                        ),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )

                    if (isLightning) {
                        Spacer(modifier = Modifier.width(8.dp))
                        LightningTalk(timeLine, dimmed = isFinished)
                    }
                }
            }

            // Display tags if available and not finished
            if (tags.isNotEmpty() && !isFinished) {
                Row(
                    modifier = Modifier.padding(bottom = 8.dp)
                ) {
                    tags.take(3).forEach { tag ->
                        TagChip(tag)
                        Spacer(modifier = Modifier.width(8.dp))
                    }
                }
            }
        }

        if (isFinished) {
            VoteAndFeedback(vote = vote, showFeedbackBlock = showFeedback, onVote = {
                showFeedback = true
                onVote(it)
            }, onSubmitFeedback = {
                showFeedback = false
                onFeedback(it)
            }) {
                showFeedback = false
            }
        }

        HDivider()
    }
}
