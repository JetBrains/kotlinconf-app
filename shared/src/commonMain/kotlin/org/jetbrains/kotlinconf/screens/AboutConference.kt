package org.jetbrains.kotlinconf.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import kotlinconfapp.shared.generated.resources.Res
import kotlinconfapp.shared.generated.resources.about_conference_description
import kotlinconfapp.shared.generated.resources.about_conference_general_terms_link
import kotlinconfapp.shared.generated.resources.about_conference_header
import kotlinconfapp.shared.generated.resources.about_conference_privacy_notice_link
import kotlinconfapp.shared.generated.resources.about_conference_title
import kotlinconfapp.shared.generated.resources.about_conference_website_link
import kotlinconfapp.shared.generated.resources.arrow_up_right_24
import kotlinconfapp.shared.generated.resources.kotlinconf_by_jetbrains
import kotlinconfapp.shared.generated.resources.kotlinconf_by_jetbrains_description
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.kotlinconf.ScreenWithTitle
import org.jetbrains.kotlinconf.ScrollToTopHandler
import org.jetbrains.kotlinconf.Speaker
import org.jetbrains.kotlinconf.SpeakerId
import org.jetbrains.kotlinconf.ui.components.DayHeader
import org.jetbrains.kotlinconf.ui.components.Divider
import org.jetbrains.kotlinconf.ui.components.PageMenuItem
import org.jetbrains.kotlinconf.ui.components.SpeakerCard
import org.jetbrains.kotlinconf.ui.components.Text
import org.jetbrains.kotlinconf.ui.theme.KotlinConfTheme
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun AboutConference(
    onPrivacyNotice: () -> Unit,
    onGeneralTerms: () -> Unit,
    onWebsiteLink: () -> Unit,
    onBack: () -> Unit,
    onSpeaker: (SpeakerId) -> Unit,
    viewModel: AboutConferenceViewModel = koinViewModel(),
) {
    val scrollState = rememberScrollState()
    ScrollToTopHandler(scrollState)
    ScreenWithTitle(
        title = stringResource(Res.string.about_conference_title),
        onBack = onBack,
        contentScrollState = scrollState,
    ) {
        Column(
            modifier = Modifier.padding(top = 24.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp),
        ) {
            Text(
                text = stringResource(Res.string.about_conference_header),
                style = KotlinConfTheme.typography.h1,
                modifier = Modifier.semantics { heading() }
            )
            Text(text = stringResource(Res.string.about_conference_description))
        }

        Spacer(modifier = Modifier.height(48.dp))

        Column(
            verticalArrangement = Arrangement.spacedBy(48.dp),
        ) {
            val events by viewModel.events.collectAsState()
            for (event in events) {
                EventCard(
                    month = event.month,
                    day = event.day,
                    line1 = event.title1,
                    line2 = event.title2,
                    description = event.description ?: "",
                    speakers = event.speakers ?: emptyList(),
                    location = event.sessionCard?.locationLine ?: "",
                    time = event.sessionCard?.shortTimeline ?: "",
                    onSpeaker = onSpeaker,
                )
            }
        }

        Image(
            painter = painterResource(Res.drawable.kotlinconf_by_jetbrains),
            contentDescription = stringResource(Res.string.kotlinconf_by_jetbrains_description),
            modifier = Modifier.align(Alignment.CenterHorizontally)
                .padding(vertical = 64.dp)
                .widthIn(max = 360.dp)
        )

        Column(
            modifier = Modifier.padding(bottom = 24.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            PageMenuItem(
                label = stringResource(Res.string.about_conference_privacy_notice_link),
                onClick = onPrivacyNotice,
            )
            PageMenuItem(
                label = stringResource(Res.string.about_conference_general_terms_link),
                onClick = onGeneralTerms,
            )
            PageMenuItem(
                label = stringResource(Res.string.about_conference_website_link),
                drawableEnd = Res.drawable.arrow_up_right_24,
                onClick = onWebsiteLink,
            )
        }
    }
}

@Composable
private fun EventCard(
    month: String,
    day: String,
    line1: String,
    line2: String,
    description: String,
    speakers: List<Speaker>,
    location: String,
    time: String,
    day2: String = "",
    onSpeaker: (SpeakerId) -> Unit,
    backgroundColor: Color = KotlinConfTheme.colors.mainBackground,
) {
    val roundedCornerShape = RoundedCornerShape(8.dp)
    Column(
        modifier = Modifier
            .background(backgroundColor)
            .border(
                width = 1.dp,
                color = KotlinConfTheme.colors.strokePale,
                shape = roundedCornerShape,
            ).clip(roundedCornerShape),
    ) {
        DayHeader(month, day, line1, line2, modifier = Modifier.fillMaxWidth(), day2 = day2)

        if (description.isNotEmpty()) {
            Text(description, modifier = Modifier.padding(horizontal = 12.dp, vertical = 16.dp))
        }

        if (speakers.isNotEmpty()) {
            Column(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                for (speaker in speakers) {
                    SpeakerCard(
                        name = speaker.name,
                        title = speaker.position,
                        photoUrl = speaker.photoUrl,
                        modifier = Modifier.fillMaxWidth(),
                        onClick = { onSpeaker(speaker.id) },
                    )
                }
            }
        }

        if (location.isNotEmpty() || time.isNotEmpty()) {
            Divider(1.dp, KotlinConfTheme.colors.strokePale)

            Row(
                modifier = Modifier.padding(16.dp).fillMaxSize(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(location, style = KotlinConfTheme.typography.text2)
                Text(time, style = KotlinConfTheme.typography.text2)
            }
        }
    }
}
