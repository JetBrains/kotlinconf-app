@file:OptIn(ExperimentalResourceApi::class)

package org.jetbrains.kotlinconf.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import kotlinconfapp.shared.generated.resources.past
import kotlinconfapp.shared.generated.resources.upcoming
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.kotlinconf.AppController
import org.jetbrains.kotlinconf.Score
import org.jetbrains.kotlinconf.SessionCardView
import org.jetbrains.kotlinconf.ui.theme.grey50
import org.jetbrains.kotlinconf.ui.theme.grey5Black
import org.jetbrains.kotlinconf.ui.theme.greyGrey5
import org.jetbrains.kotlinconf.ui.theme.orange
import org.jetbrains.kotlinconf.ui.theme.whiteGrey
import org.jetbrains.kotlinconf.ui.components.Tab
import org.jetbrains.kotlinconf.ui.components.TabBar
import org.jetbrains.kotlinconf.ui.components.VoteAndFeedback

enum class Bookmark(override val title: StringResource) : Tab {
    PAST(Res.string.past), UPCOMING(Res.string.upcoming)
}

@Composable
fun BookmarksScreen(
    favoriteSessions: List<SessionCardView>,
    controller: AppController
) {
    var selectedTab by remember { mutableStateOf(Bookmark.UPCOMING) }

    Column(Modifier.background(MaterialTheme.colors.grey5Black)) {
        TabBar(Bookmark.entries, selectedTab, onSelect = {
            selectedTab = it
        })

        Text(
            stringResource(selectedTab.title),
            style = MaterialTheme.typography.h2.copy(color = MaterialTheme.colors.greyGrey5),
            modifier = Modifier.padding(16.dp)
        )

        HDivider()

        if (selectedTab == Bookmark.UPCOMING) {
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

@OptIn(ExperimentalResourceApi::class)
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
                text = timeLine,
                color = grey50,
                style = MaterialTheme.typography.body2,
                modifier = Modifier.padding(16.dp)
            )
            Text(
                text = title,
                style = MaterialTheme.typography.h4,
                modifier = Modifier.padding(start = 16.dp, end = 16.dp)
            )
            Text(
                text = speakerLine,
                style = MaterialTheme.typography.body2.copy(
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
                Icon(
                    painter = Res.drawable.bookmark_active.painter(),
                    contentDescription = "bookmark",
                    tint = orange
                )
            }
        }
    }
}


@OptIn(ExperimentalResourceApi::class)
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
                text = timeLine,
                color = grey50,
                style = MaterialTheme.typography.body2,
                modifier = Modifier.padding(16.dp)
            )
            Text(
                text = title,
                style = MaterialTheme.typography.h4,
                modifier = Modifier.padding(start = 16.dp, end = 16.dp)
            )
            Text(
                text = speakerLine,
                style = MaterialTheme.typography.body2.copy(
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
                Icon(
                    painter = Res.drawable.bookmark_active.painter(),
                    contentDescription = "bookmark",
                    tint = orange
                )
            }
        }
    }
}
