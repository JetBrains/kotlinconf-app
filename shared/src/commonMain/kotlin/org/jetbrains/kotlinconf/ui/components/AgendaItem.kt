package org.jetbrains.kotlinconf.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
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
    onSessionClick: () -> Unit = {},
    onFavoriteClick: () -> Unit = {},
    onVote: (Score?) -> Unit = {},
    onFeedback: (String) -> Unit = {}
) {
    var showFeedback by remember { mutableStateOf(false) }

    Column(Modifier.background(MaterialTheme.colors.whiteGrey).clickable {
        onSessionClick()
    }) {
        Row(Modifier.fillMaxWidth()) {
            Column(
                modifier = Modifier.padding(
                    start = 16.dp,
                    end = 48.dp,
                    top = 16.dp,
                ).weight(1.0f, true)
            ) {
                Text(
                    title, style = MaterialTheme.typography.h4.copy(
                        color = if (isFinished) grey50 else MaterialTheme.colors.greyWhite
                    )
                )
                Text(
                    speakerLine,
                    Modifier.padding(top = 4.dp),
                    style = MaterialTheme.typography.body2.copy(
                        color = if (isFinished) grey50 else MaterialTheme.colors.greyGrey5
                    )
                )
            }
            if (!isFinished) {
                IconButton(onClick = {
                    onFavoriteClick()
                }) {
                    val icon =
                        if (isFavorite) Res.drawable.bookmark_active else Res.drawable.bookmark
                    Icon(
                        painter = icon.painter(),
                        contentDescription = "bookmark",
                        tint = if (isFavorite) orange else MaterialTheme.colors.greyWhite
                    )
                }
            }
        }

        Column(
            Modifier.padding(16.dp).fillMaxWidth()
        ) {
            if (!isFinished) {
                Text(
                    locationLine,
                    style = MaterialTheme.typography.body2.copy(
                        color = grey50
                    ),
                    maxLines = 1,
                    modifier = Modifier.padding(bottom = 4.dp)
                )
            }
            if (isLightning) {
                LightningTalk(timeLine, dimmed = isFinished)
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
