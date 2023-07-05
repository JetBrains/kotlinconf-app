package org.jetbrains.kotlinconf.android.ui

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.res.*
import androidx.compose.ui.tooling.preview.*
import androidx.compose.ui.unit.*
import com.jetbrains.kotlinconf.R
import org.jetbrains.kotlinconf.*
import org.jetbrains.kotlinconf.android.*
import org.jetbrains.kotlinconf.android.theme.*

@Composable
fun Bookmarks(
    favoriteSessions: List<SessionCardView>,
    controller: AppController
) {
    var selectedTab by remember { mutableStateOf("upcoming") }

    Column(Modifier.background(MaterialTheme.colors.grey5Black)) {
        TabBar(tabs = listOf("past", "upcoming"), selectedTab, onSelect = {
            selectedTab = it
        })

        Text(
            selectedTab.uppercase(),
            style = MaterialTheme.typography.h2.copy(color = MaterialTheme.colors.greyGrey5),
            modifier = Modifier.padding(16.dp)
        )

        HDivider()

        if (selectedTab == "upcoming") {
            val upcoming = favoriteSessions.filter { !it.isFinished }
            Upcoming(
                upcoming,
                onSessionClick = { controller.showSession(it) },
                { controller.toggleFavorite(it) })
        } else {
            val finished = favoriteSessions.filter { it.isFinished }
            Finished(
                finished,
                onSessionClick = { controller.showSession(it) },
                onVote = { sessionId, vote -> controller.vote(sessionId, vote) },
                onFeedback = { sessionId, feedback ->
                    controller.sendFeedback(sessionId, feedback)
                },
                onBookmarkClick = { controller.toggleFavorite(it) }
            )
        }
    }
}

@Composable
private fun Upcoming(
    sessions: List<SessionCardView>,
    onSessionClick: (String) -> Unit,
    onBookmarkClick: (String) -> Unit
) {
    LazyColumn {
        items(sessions) {
            UpcomingCard(
                title = it.title,
                timeLine = it.timeLine,
                speakerLine = it.speakerLine,
                locationLine = it.locationLine,
                onClick = { onSessionClick(it.id) },
                onBookmarkClick = { onBookmarkClick(it.id) }
            )
        }
    }
}

@Composable
private fun Finished(
    sessions: List<SessionCardView>,
    onSessionClick: (String) -> Unit,
    onVote: (String, Score?) -> Unit,
    onFeedback: (String, String) -> Unit,
    onBookmarkClick: (String) -> Unit
) {
    LazyColumn {
        items(sessions) { session ->
            FinishedCard(
                title = session.title,
                timeLine = session.timeLine,
                speakerLine = session.speakerLine,
                vote = session.vote,
                onClick = { onSessionClick(session.id) },
                onVote = { onVote(session.id, it) },
                onFeedback = { onFeedback(session.id, it) },
                onBookmarkClick = { onBookmarkClick(session.id) }
            )
        }
    }
}

@Composable
private fun UpcomingCard(
    title: String,
    timeLine: String,
    speakerLine: String,
    locationLine: String,
    onClick: () -> Unit = {},
    onBookmarkClick: () -> Unit = {}
) {
    Box(Modifier.fillMaxWidth()) {
        Column(
            Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colors.whiteGrey)
                .clickable { onClick() }
        ) {
            Text(
                text = timeLine.uppercase(),
                color = grey50,
                style = MaterialTheme.typography.t2,
                modifier = Modifier.padding(16.dp)
            )
            Text(
                text = title,
                style = MaterialTheme.typography.h4,
                modifier = Modifier.padding(start = 16.dp, end = 16.dp)
            )
            Text(
                text = speakerLine,
                style = MaterialTheme.typography.t2.copy(
                    color = MaterialTheme.colors.greyGrey5
                ),
                modifier = Modifier.padding(top = 4.dp, start = 16.dp, end = 16.dp)
            )

            LocationRow(location = locationLine, modifier = Modifier.padding(16.dp))
            HDivider()
        }
        Row(horizontalArrangement = Arrangement.End, modifier = Modifier.fillMaxWidth()) {
            IconButton(onClick = {
                onBookmarkClick()
            }) {
                val icon = R.drawable.bookmark_active
                Icon(
                    painter = painterResource(id = icon),
                    contentDescription = "bookmark",
                    tint = orange
                )
            }
        }
    }
}


@Composable
private fun FinishedCard(
    title: String,
    timeLine: String,
    speakerLine: String,
    vote: Score?,
    onClick: () -> Unit = {},
    onVote: (Score?) -> Unit = {},
    onFeedback: (String) -> Unit = {},
    onBookmarkClick: () -> Unit = {}
) {
    var isFeedbackOpen by remember { mutableStateOf(false) }
    Box(Modifier.fillMaxWidth()) {
        Column(
            Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colors.whiteGrey)
                .clickable { onClick() }
        ) {
            Text(
                text = timeLine.uppercase(),
                color = grey50,
                style = MaterialTheme.typography.t2,
                modifier = Modifier.padding(16.dp)
            )
            Text(
                text = title,
                style = MaterialTheme.typography.h4,
                modifier = Modifier.padding(start = 16.dp, end = 16.dp)
            )
            Text(
                text = speakerLine,
                style = MaterialTheme.typography.t2.copy(
                    color = MaterialTheme.colors.greyGrey5
                ),
                modifier = Modifier.padding(top = 4.dp, start = 16.dp, end = 16.dp)
            )

            VoteAndFeedback(vote = vote,
                onVote = {
                    if (it != vote) {
                        isFeedbackOpen = true
                        onVote(it)
                    }
                },
                showFeedbackBlock = isFeedbackOpen,
                onSubmitFeedback = {
                    isFeedbackOpen = false
                    onFeedback(it)
                },
                onCloseFeedback = {
                    isFeedbackOpen = false
                }
            )
        }
        Row(horizontalArrangement = Arrangement.End, modifier = Modifier.fillMaxWidth()) {
            IconButton(onClick = {
                onBookmarkClick()
            }) {
                val icon = R.drawable.bookmark_active
                Icon(
                    painter = painterResource(id = icon),
                    contentDescription = "bookmark",
                    tint = orange
                )
            }
        }
    }
}

@Composable
@Preview
private fun PastCardPreview() {
    KotlinConfTheme {
        FinishedCard(
            title = "Kotlin for Android",
            speakerLine = "John Doe",
            timeLine = "10:00 - 11:00",
            vote = null
        )
    }
}

@Composable
@Preview(showBackground = true)
private fun UpcomingCardSoonPreview() {
    KotlinConfTheme {
        UpcomingCard(
            title = "Kotlin for Android",
            timeLine = "in 5 minutes",
            speakerLine = "John Doe",
            locationLine = "Room 1",
        )
    }
}


@Composable
@Preview(showBackground = true)
private fun UpcomingCardPreview() {
    KotlinConfTheme {
        UpcomingCard(
            title = "Kotlin for Android",
            timeLine = "10:00 - 11:00",
            speakerLine = "John Doe",
            locationLine = "Room 1",
        )
    }
}
