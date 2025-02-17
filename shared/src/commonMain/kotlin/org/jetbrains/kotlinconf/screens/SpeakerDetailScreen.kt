package org.jetbrains.kotlinconf.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinconfapp.shared.generated.resources.Res
import kotlinconfapp.shared.generated.resources.schedule_in_x_minutes
import kotlinconfapp.shared.generated.resources.speakers_title
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.kotlinconf.ScreenWithTitle
import org.jetbrains.kotlinconf.SessionId
import org.jetbrains.kotlinconf.SpeakerId
import org.jetbrains.kotlinconf.ui.components.SpeakerAvatar
import org.jetbrains.kotlinconf.ui.components.StyledText
import org.jetbrains.kotlinconf.ui.components.TalkCard
import org.jetbrains.kotlinconf.ui.components.TalkStatus
import org.jetbrains.kotlinconf.ui.theme.KotlinConfTheme
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

@Composable
fun SpeakerDetailScreen(
    speakerId: SpeakerId,
    onBack: () -> Unit,
    onSession: (SessionId) -> Unit,
    viewModel: SpeakerDetailViewModel = koinViewModel { parametersOf(speakerId) }
) {
    val speaker = viewModel.speaker.collectAsStateWithLifecycle().value
    val sessions = viewModel.sessions.collectAsStateWithLifecycle().value

    ScreenWithTitle(
        title = stringResource(Res.string.speakers_title),
        onBack = onBack,
    ) {
        if (speaker != null) {
            Column(
                modifier = Modifier.padding(vertical = 16.dp),
            ) {
                StyledText(
                    text = speaker.name,
                    style = KotlinConfTheme.typography.h2,
                    color = KotlinConfTheme.colors.primaryText,
                )

                Spacer(Modifier.height(4.dp))

                StyledText(
                    text = speaker.position,
                    style = KotlinConfTheme.typography.text2,
                    color = KotlinConfTheme.colors.secondaryText,
                )

                Spacer(Modifier.height(16.dp))

                SpeakerAvatar(
                    photoUrl = speaker.photoUrl,
                    modifier = Modifier.widthIn(max = 300.dp)
                        .aspectRatio(1f)
                )

                Spacer(Modifier.height(24.dp))

                StyledText(
                    text = speaker.description,
                    style = KotlinConfTheme.typography.text2,
                    color = KotlinConfTheme.colors.longText,
                )

                Spacer(Modifier.height(16.dp))

                sessions.forEach { session ->
                    TalkCard(
                        title = session.title,
                        titleHighlights = emptyList(),
                        bookmarked = session.isFavorite,
                        onBookmark = { isBookmarked -> viewModel.onBookmark(session.id, isBookmarked) },
                        tags = session.tags,
                        tagHighlights = emptyList(),
                        speakers = session.speakerLine,
                        speakerHighlights = emptyList(),
                        location = session.locationLine,
                        lightning = session.isLightning,
                        time = session.badgeTimeLine,
                        timeNote = session.startsInMinutes?.let { count ->
                            stringResource(Res.string.schedule_in_x_minutes, count)
                        },
                        status = TalkStatus.Upcoming,
                        feedbackEnabled = false,
                        onSubmitFeedback = { /* Not enabled on this screen */ },
                        onSubmitFeedbackWithComment = { _, _ -> /* Not enabled on this screen */ },
                        onClick = { onSession(session.id) },
                        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                    )
                }
            }
        }
    }
}
