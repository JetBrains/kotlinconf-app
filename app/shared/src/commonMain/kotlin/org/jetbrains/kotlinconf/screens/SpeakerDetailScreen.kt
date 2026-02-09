package org.jetbrains.kotlinconf.screens

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
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
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.kotlinconf.ScrollToTopHandler
import org.jetbrains.kotlinconf.SessionId
import org.jetbrains.kotlinconf.SpeakerId
import org.jetbrains.kotlinconf.generated.resources.Res
import org.jetbrains.kotlinconf.generated.resources.schedule_in_x_minutes
import org.jetbrains.kotlinconf.generated.resources.speaker_detail_error_not_found
import org.jetbrains.kotlinconf.generated.resources.speaker_detail_title
import org.jetbrains.kotlinconf.toEmotion
import org.jetbrains.kotlinconf.ui.components.Divider
import org.jetbrains.kotlinconf.ui.components.MainHeaderTitleBar
import org.jetbrains.kotlinconf.ui.components.MajorError
import org.jetbrains.kotlinconf.ui.components.SpeakerAvatar
import org.jetbrains.kotlinconf.ui.components.TalkCard
import org.jetbrains.kotlinconf.ui.components.TalkStatus
import org.jetbrains.kotlinconf.ui.components.Text
import org.jetbrains.kotlinconf.ui.components.TopMenuButton
import org.jetbrains.kotlinconf.ui.generated.resources.UiRes
import org.jetbrains.kotlinconf.ui.generated.resources.arrow_left_24
import org.jetbrains.kotlinconf.ui.generated.resources.main_header_back
import org.jetbrains.kotlinconf.ui.theme.KotlinConfTheme
import org.jetbrains.kotlinconf.utils.FadingAnimationSpec
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
    val speaker = viewModel.speaker.collectAsStateWithLifecycle().value
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
        Divider(thickness = 1.dp, color = KotlinConfTheme.colors.strokePale)

        AnimatedContent(
            targetState = speaker,
            contentKey = { speaker != null },
            transitionSpec = { FadingAnimationSpec },
            modifier = Modifier.fillMaxSize().weight(1f)
        ) { currentSpeaker ->
            if (currentSpeaker == null) {
                MajorError(
                    message = stringResource(Res.string.speaker_detail_error_not_found),
                    modifier = Modifier.fillMaxSize(),
                )
            } else {
                val scrollState = rememberScrollState()
                ScrollToTopHandler(scrollState)
                Column(
                    modifier = Modifier
                        .verticalScroll(scrollState)
                        .padding(vertical = 16.dp, horizontal = 12.dp)
                        .padding(bottomInsetPadding()),
                ) {
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

                    Spacer(Modifier.height(16.dp))

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

                    Spacer(Modifier.height(16.dp))

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
                            userSignedIn = false, // Feedback not enabled on this screen
                            onSubmitFeedback = { }, // Feedback not enabled on this screen
                            onSubmitFeedbackWithComment = { _, _ -> }, // Feedback not enabled on this screen
                            onRequestFeedbackWithComment = null, // Feedback not enabled on this screen
                            onClick = { onSession(session.id) },
                            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                        )
                    }
                }
            }
        }
    }
}
