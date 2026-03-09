package org.jetbrains.kotlinconf.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dev.zacsweers.metrox.viewmodel.assistedMetroViewModel
import kotlinx.datetime.Month
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.kotlinconf.ScrollToTopHandler
import org.jetbrains.kotlinconf.SessionCardView
import org.jetbrains.kotlinconf.SessionId
import org.jetbrains.kotlinconf.Speaker
import org.jetbrains.kotlinconf.SpeakerId
import org.jetbrains.kotlinconf.generated.resources.Res
import org.jetbrains.kotlinconf.generated.resources.schedule_in_x_minutes
import org.jetbrains.kotlinconf.generated.resources.speaker_detail_error_not_found
import org.jetbrains.kotlinconf.generated.resources.speaker_detail_title
import org.jetbrains.kotlinconf.toEmotion
import org.jetbrains.kotlinconf.ui.components.HorizontalDivider
import org.jetbrains.kotlinconf.ui.components.MainHeaderTitleBar
import org.jetbrains.kotlinconf.ui.components.SpeakerAvatar
import org.jetbrains.kotlinconf.ui.components.TalkCard
import org.jetbrains.kotlinconf.ui.components.TalkStatus
import org.jetbrains.kotlinconf.ui.components.Text
import org.jetbrains.kotlinconf.ui.components.TopMenuButton
import org.jetbrains.kotlinconf.ui.generated.resources.UiRes
import org.jetbrains.kotlinconf.ui.generated.resources.arrow_left_24
import org.jetbrains.kotlinconf.ui.generated.resources.main_header_back
import org.jetbrains.kotlinconf.ui.theme.KotlinConfTheme
import org.jetbrains.kotlinconf.utils.ErrorLoadingContent
import org.jetbrains.kotlinconf.utils.LocalWindowSize
import org.jetbrains.kotlinconf.utils.WindowSize
import org.jetbrains.kotlinconf.utils.bottomInsetPadding
import org.jetbrains.kotlinconf.utils.topInsetPadding

@Composable
fun SpeakerDetailScreen(
    speakerId: SpeakerId,
    onBack: () -> Unit,
    onSession: (SessionId) -> Unit,
    viewModel: SpeakerDetailViewModel =
        assistedMetroViewModel<SpeakerDetailViewModel, SpeakerDetailViewModel.Factory> {
            create(speakerId)
        }
) {
    val speakerState = viewModel.speaker.collectAsStateWithLifecycle().value
    val sessions = viewModel.sessions.collectAsStateWithLifecycle().value

    Column(
        Modifier
            .fillMaxSize()
            .background(color = KotlinConfTheme.colors.mainBackground)
            .padding(topInsetPadding())
    ) {
        MainHeaderTitleBar(
            title = stringResource(Res.string.speaker_detail_title),
            startContent = {
                TopMenuButton(
                    icon = UiRes.drawable.arrow_left_24,
                    contentDescription = stringResource(UiRes.string.main_header_back),
                    onClick = onBack,
                )
            }
        )
        HorizontalDivider(thickness = 1.dp, color = KotlinConfTheme.colors.strokePale)

        ErrorLoadingContent(
            state = speakerState,
            errorMessage = stringResource(Res.string.speaker_detail_error_not_found),
            modifier = Modifier.fillMaxSize().weight(1f),
        ) { currentSpeaker ->
            val scrollState = rememberScrollState()
            ScrollToTopHandler(scrollState)


            Box(
                Modifier
                    .verticalScroll(scrollState)
                    .padding(vertical = 16.dp, horizontal = 12.dp)
                    .padding(bottomInsetPadding())
            ) {
                when (LocalWindowSize.current) {
                    WindowSize.Compact -> {
                        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                            Name(currentSpeaker)
                            Description(currentSpeaker)
                            Talks(sessions, viewModel, onSession)
                        }
                    }

                    WindowSize.Medium -> {
                        Column(verticalArrangement = Arrangement.spacedBy(24.dp)) {
                            Description(currentSpeaker)
                            Talks(sessions, viewModel, onSession)
                        }
                    }

                    WindowSize.Large -> {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(32.dp),
                            modifier = Modifier.padding(start = 92.dp, top = 32.dp, end = 24.dp)
                        ) {
                            Description(currentSpeaker, Modifier.weight(1f))
                            Talks(sessions, viewModel, onSession, Modifier.weight(1f))
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun Name(currentSpeaker: Speaker) {
    Column {
        Text(
            text = currentSpeaker.name,
            style = KotlinConfTheme.typography.h2,
            color = KotlinConfTheme.colors.primaryText,
            selectable = true,
            modifier = Modifier.semantics {
                heading()
            }
        )
        Spacer(Modifier.height(4.dp))
        Text(
            text = currentSpeaker.position,
            style = KotlinConfTheme.typography.text2,
            color = KotlinConfTheme.colors.secondaryText,
            selectable = true,
        )
    }
}

@Composable
private fun Description(
    currentSpeaker: Speaker,
    modifier: Modifier = Modifier,
) {
    Column(modifier) {
        SpeakerAvatar(
            photoUrl = currentSpeaker.photoUrl,
            modifier = Modifier.widthIn(max = 300.dp)
                .aspectRatio(1f)
        )
        Spacer(Modifier.height(24.dp))

        Text(
            text = currentSpeaker.description,
            style = KotlinConfTheme.typography.text2,
            color = KotlinConfTheme.colors.longText,
            selectable = true,
        )
    }
}

@Composable
private fun Talks(
    sessions: List<SessionCardView>,
    viewModel: SpeakerDetailViewModel,
    onSession: (SessionId) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier) {
        sessions.forEach { session ->
            TalkCard(
                title = session.title,
                titleHighlights = emptyList(),
                bookmarked = session.isFavorite,
                onBookmark = { isBookmarked ->
                    viewModel.onBookmark(
                        session.id,
                        isBookmarked
                    )
                },
                tags = session.tags,
                tagHighlights = emptyList(),
                speakers = session.speakerLine,
                speakerHighlights = emptyList(),
                location = session.locationLine,
                lightning = session.isLightning,
                time = session.fullTimeline,
                timeNote = session.startsInMinutes?.let { count ->
                    stringResource(Res.string.schedule_in_x_minutes, count)
                },
                status = TalkStatus.Upcoming,
                initialEmotion = session.vote?.toEmotion(),
                feedbackEnabled = false, // Feedback not enabled on this screen
                onSubmitFeedback = { }, // Feedback not enabled on this screen
                onSubmitFeedbackWithComment = { _, _ -> }, // Feedback not enabled on this screen
                onClick = { onSession(session.id) },
                modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
            )
        }
    }
}
