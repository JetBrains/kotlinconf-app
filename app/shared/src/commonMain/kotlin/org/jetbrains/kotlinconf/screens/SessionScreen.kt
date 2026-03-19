package org.jetbrains.kotlinconf.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import org.jetbrains.kotlinconf.utils.ErrorLoadingState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dev.zacsweers.metrox.viewmodel.assistedMetroViewModel
import dev.zacsweers.metrox.viewmodel.metroViewModel
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.kotlinconf.HideKeyboardOnDragHandler
import org.jetbrains.kotlinconf.ScrollToTopHandler
import org.jetbrains.kotlinconf.SessionId
import org.jetbrains.kotlinconf.SessionState
import org.jetbrains.kotlinconf.SpeakerId
import org.jetbrains.kotlinconf.generated.resources.Res
import org.jetbrains.kotlinconf.generated.resources.schedule_in_x_minutes
import org.jetbrains.kotlinconf.generated.resources.arrow_left_24
import org.jetbrains.kotlinconf.generated.resources.arrow_up_right_24
import org.jetbrains.kotlinconf.generated.resources.down_24
import org.jetbrains.kotlinconf.generated.resources.navigate_back
import org.jetbrains.kotlinconf.generated.resources.play_video
import org.jetbrains.kotlinconf.generated.resources.session_room_state_description_collapsed
import org.jetbrains.kotlinconf.generated.resources.session_room_state_description_expanded
import org.jetbrains.kotlinconf.generated.resources.session_screen_error
import org.jetbrains.kotlinconf.generated.resources.session_title
import org.jetbrains.kotlinconf.generated.resources.session_watch_video
import org.jetbrains.kotlinconf.toEmotion
import org.jetbrains.kotlinconf.ui.components.Action
import org.jetbrains.kotlinconf.ui.components.ActionSize
import org.jetbrains.kotlinconf.ui.components.HorizontalDivider
import org.jetbrains.kotlinconf.ui.components.MainHeaderTitleBar
import org.jetbrains.kotlinconf.ui.components.PageMenuItem
import org.jetbrains.kotlinconf.ui.components.PageTitle
import org.jetbrains.kotlinconf.ui.components.SpeakerCard
import org.jetbrains.kotlinconf.ui.components.Text
import org.jetbrains.kotlinconf.ui.components.TopMenuButton
import org.jetbrains.kotlinconf.ui.theme.KotlinConfTheme
import org.jetbrains.kotlinconf.utils.ErrorLoadingContent
import org.jetbrains.kotlinconf.utils.bottomInsetPadding
import org.jetbrains.kotlinconf.utils.topInsetPadding

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun SessionScreen(
    sessionId: SessionId,
    onBack: () -> Unit,
    onSpeaker: (SpeakerId) -> Unit,
    onPrivacyNoticeNeeded: () -> Unit,
    onNavigateToMap: (String) -> Unit,
    onWatchVideo: (String) -> Unit,
    viewModel: SessionViewModel =
        assistedMetroViewModel<SessionViewModel, SessionViewModel.Factory> { create(sessionId) }
) {
    val sessionState = viewModel.session.collectAsStateWithLifecycle().value
    val speakers = viewModel.speakers.collectAsStateWithLifecycle().value

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(color = KotlinConfTheme.colors.mainBackground)
            .padding(topInsetPadding())
    ) {
        MainHeaderTitleBar(
            title = stringResource(Res.string.session_title),
            startContent = {
                TopMenuButton(
                    icon = Res.drawable.arrow_left_24,
                    contentDescription = stringResource(Res.string.navigate_back),
                    onClick = onBack,
                )
            },
        )

        HorizontalDivider(
            thickness = 1.dp,
            color = KotlinConfTheme.colors.strokePale
        )

        ErrorLoadingContent(
            state = sessionState,
            errorMessage = stringResource(Res.string.session_screen_error),
            modifier = Modifier.fillMaxSize(),
        ) { session ->
            val scrollState = rememberScrollState()

            ScrollToTopHandler(scrollState)
            HideKeyboardOnDragHandler(scrollState)

            Column(
                modifier = Modifier
                    .padding(horizontal = 12.dp)
                    .verticalScroll(scrollState)
                    .padding(bottomInsetPadding())
            ) {
                PageTitle(
                    time = session.fullTimeline,
                    title = session.title,
                    lightning = session.isLightning,
                    tags = session.tags,
                    bookmarked = session.isFavorite,
                    onBookmark = { viewModel.toggleFavorite(it) },
                    modifier = Modifier.padding(vertical = 24.dp),
                    timeNote = session.startsInMinutes?.let { count ->
                        stringResource(Res.string.schedule_in_x_minutes, count)
                    },
                    isLive = session.state == SessionState.Live,
                )
                if (session.videoUrl != null) {
                    PageMenuItem(
                        label = stringResource(Res.string.session_watch_video),
                        onClick = { onWatchVideo(session.videoUrl) },
                        drawableStart = Res.drawable.play_video,
                        drawableEnd = Res.drawable.arrow_up_right_24,
                        modifier = Modifier.padding(bottom = 12.dp),
                    )
                }

                if (session.state != SessionState.Upcoming) {
                    FeedbackPanel(
                        sessionId = sessionId,
                        tags = session.tags,
                        onPrivacyNoticeNeeded = onPrivacyNoticeNeeded,
                        modifier = Modifier.padding(bottom = 20.dp),
                    )
                }

                speakers.forEach { speaker ->
                    SpeakerCard(
                        name = speaker.name,
                        title = speaker.position,
                        photoUrl = speaker.photoUrl,
                        modifier = Modifier.padding(vertical = 12.dp).fillMaxWidth(),
                        onClick = { onSpeaker(speaker.id) }
                    )
                }

                RoomSection(
                    roomName = session.locationLine,
                    onNavigateToMap = onNavigateToMap
                )

                Text(
                    text = session.description,
                    style = KotlinConfTheme.typography.text1,
                    selectable = true,
                )

                Spacer(Modifier.height(24.dp))
            }
        }
    }
}

@Composable
private fun RoomSection(
    roomName: String,
    onNavigateToMap: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    var isExpanded by rememberSaveable { mutableStateOf(false) }
    val iconRotation by animateFloatAsState(if (isExpanded) 180f else 0f)

    val mapViewModel: MapViewModel = metroViewModel()
    val mapState = mapViewModel.state.collectAsStateWithLifecycle().value

    val mapContent = (mapState as? ErrorLoadingState.Content)?.data
    val mapData = mapContent?.mapData
    val room = mapData?.rooms?.get(roomName)

    val canDisplayRoom = room != null

    Column(modifier = modifier.padding(vertical = 16.dp)) {
        if (!canDisplayRoom) {
            Text(
                text = roomName,
                style = KotlinConfTheme.typography.h3,
            )
        } else {
            val stateDesc = stringResource(
                if (isExpanded) Res.string.session_room_state_description_expanded
                else Res.string.session_room_state_description_collapsed
            )
            Action(
                label = roomName,
                icon = Res.drawable.down_24,
                size = ActionSize.Large,
                enabled = true,
                onClick = { isExpanded = !isExpanded },
                iconRotation = iconRotation,
                modifier = Modifier
                    .fillMaxWidth()
                    .semantics {
                        stateDescription = stateDesc
                    }
            )
            AnimatedVisibility(
                visible = isExpanded,
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically(),
                modifier = Modifier.fillMaxWidth(),
            ) {
                StaticMap(
                    mapData = mapData,
                    room = room,
                    svgsByPath = mapContent.svgsByPath,
                    modifier = Modifier
                        .padding(top = 8.dp)
                        .clip(KotlinConfTheme.shapes.roundedCornerMd)
                        .clickable {
                            onNavigateToMap(roomName)
                        }
                        .fillMaxWidth()
                        .heightIn(max = 400.dp),
                )
            }
        }
    }
}
