package org.jetbrains.kotlinconf.android.ui.components

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.res.*
import androidx.compose.ui.tooling.preview.*
import androidx.compose.ui.unit.*
import com.jetbrains.kotlinconf.R
import org.jetbrains.kotlinconf.*
import org.jetbrains.kotlinconf.android.theme.*
import org.jetbrains.kotlinconf.android.ui.*


@Composable
fun AgendaItem(
    title: String,
    speakerLine: String,
    locationLine: String,
    timeLine: String,
    isFavorite: Boolean,
    isFinished: Boolean,
    isLightning: Boolean,
    isCodeLab: Boolean,
    isAWS: Boolean,
    vote: Score?,
    onSessionClick: () -> Unit = {},
    onFavoriteClick: () -> Unit = {},
    onVote: (Score?) -> Unit = {},
    onFeedback: (String) -> Unit = {}
) {
    var showFeedback by remember { mutableStateOf(false) }

    Column(
        Modifier
            .background(MaterialTheme.colors.whiteGrey)
            .clickable {
                onSessionClick()
            }
    ) {
        Row(Modifier.fillMaxWidth()) {
            Column(
                modifier = Modifier
                    .padding(
                        start = 16.dp,
                        end = 48.dp,
                        top = 16.dp,
                    )
                    .weight(1.0f, true)
            ) {
                Text(
                    title,
                    style = MaterialTheme.typography.h4.copy(
                        color = if (isFinished) grey50 else MaterialTheme.colors.greyWhite
                    )
                )
                Text(
                    speakerLine,
                    Modifier.padding(top = 4.dp),
                    style = MaterialTheme.typography.t2.copy(
                        color = if (isFinished) grey50 else MaterialTheme.colors.greyGrey5
                    )
                )
            }
            if (!isFinished) {
                IconButton(onClick = {
                    onFavoriteClick()
                }) {
                    val icon = if (isFavorite) R.drawable.bookmark_active else R.drawable.bookmark
                    Icon(
                        painter = painterResource(id = icon),
                        contentDescription = "bookmark",
                        tint = if (isFavorite) orange else MaterialTheme.colors.greyWhite
                    )
                }
            }
        }

        Row(
            Modifier
                .padding(16.dp)
                .fillMaxWidth()
        ) {
            if (!isFinished) {
                Text(
                    locationLine.uppercase(), style = MaterialTheme.typography.t2.copy(
                        color = grey50
                    ),
                    maxLines = 1
                )
            }
            Spacer(modifier = Modifier.weight(1f))
            if (isLightning) {
                LightningTalk(timeLine, dimmed = isFinished)
            } else if (isCodeLab) {
                CodeLab(dimmed = isFinished)
            } else if (isAWS) {
                AWSLab(dimmed = isFinished)
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

@Composable
@Preview
fun AgendaItemPreview() {
    KotlinConfTheme {
        Column(Modifier.verticalScroll(rememberScrollState())) {
            AgendaItem(
                title = "Hello world",
                speakerLine = "Speaker 1, Speaker 2",
                locationLine = "At my home",
                timeLine = "",
                isFavorite = false,
                isFinished = false,
                isLightning = false,
                isCodeLab = false,
                isAWS = false,
                vote = null
            )
            AgendaItem(
                title = "Hello world",
                speakerLine = "Speaker 1, Speaker 2",
                locationLine = "At my home",
                timeLine = "",
                isFavorite = false,
                isFinished = false,
                isLightning = true,
                isCodeLab = false,
                isAWS = false,
                vote = null
            )
            AgendaItem(
                title = "Hello world",
                speakerLine = "Speaker 1, Speaker 2",
                locationLine = "At my home",
                timeLine = "",
                isFavorite = false,
                isFinished = true,
                isLightning = true,
                isCodeLab = false,
                isAWS = false,
                vote = null
            )
            AgendaItem(
                title = "Hello world",
                speakerLine = "Speaker 1, Speaker 2",
                locationLine = "At my home",
                timeLine = "",
                isFavorite = false,
                isFinished = false,
                isCodeLab = true,
                isLightning = false,
                isAWS = false,
                vote = null
            )
            AgendaItem(
                title = "Hello world",
                speakerLine = "Speaker 1, Speaker 2",
                locationLine = "At my home",
                timeLine = "",
                isFavorite = false,
                isFinished = false,
                isLightning = true,
                isCodeLab = false,
                isAWS = false,
                vote = null
            )
            AgendaItem(
                title = "Hello world",
                speakerLine = "Speaker 1, Speaker 2",
                locationLine = "At my home",
                timeLine = "",
                isFavorite = false,
                isFinished = false,
                isAWS = true,
                isLightning = false,
                isCodeLab = false,
                vote = null
            )
            AgendaItem(
                title = "Hello world",
                speakerLine = "Speaker 1, Speaker 2",
                locationLine = "At my home",
                timeLine = "",
                isFavorite = false,
                isFinished = true,
                isCodeLab = false,
                isAWS = false,
                isLightning = false,
                vote = null
            )
            AgendaItem(
                title = "Hello world",
                speakerLine = "Speaker 1, Speaker 2",
                locationLine = "At my home",
                timeLine = "",
                isFavorite = true,
                isFinished = false,
                isLightning = false,
                isCodeLab = false,
                isAWS = false,
                vote = null
            )
            AgendaItem(
                title = "Hello world",
                speakerLine = "Speaker 1, Speaker 2",
                locationLine = "At my home",
                timeLine = "",
                isFavorite = true,
                isFinished = true,
                isLightning = false,
                isCodeLab = false,
                isAWS = false,
                vote = null
            )
        }
    }
}