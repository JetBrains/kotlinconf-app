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
import androidx.compose.runtime.key
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import kotlinconfapp.shared.generated.resources.Res
import kotlinconfapp.shared.generated.resources.about_conference_closingpanel_title_line1
import kotlinconfapp.shared.generated.resources.about_conference_closingpanel_title_line2
import kotlinconfapp.shared.generated.resources.about_conference_codelabs_title_line1
import kotlinconfapp.shared.generated.resources.about_conference_codelabs_title_line2
import kotlinconfapp.shared.generated.resources.about_conference_day1_title_line1
import kotlinconfapp.shared.generated.resources.about_conference_day1_title_line2
import kotlinconfapp.shared.generated.resources.about_conference_day2_title_line1
import kotlinconfapp.shared.generated.resources.about_conference_day2_title_line2
import kotlinconfapp.shared.generated.resources.about_conference_description
import kotlinconfapp.shared.generated.resources.about_conference_general_terms_link
import kotlinconfapp.shared.generated.resources.about_conference_hashtag
import kotlinconfapp.shared.generated.resources.about_conference_header
import kotlinconfapp.shared.generated.resources.about_conference_party_description
import kotlinconfapp.shared.generated.resources.about_conference_party_line1
import kotlinconfapp.shared.generated.resources.about_conference_party_line2
import kotlinconfapp.shared.generated.resources.about_conference_privacy_policy_link
import kotlinconfapp.shared.generated.resources.about_conference_timeline
import kotlinconfapp.shared.generated.resources.about_conference_title
import kotlinconfapp.shared.generated.resources.about_conference_website_link
import kotlinconfapp.shared.generated.resources.arrow_up_right_24
import kotlinconfapp.shared.generated.resources.kotlinconf_by_jetbrains
import kotlinconfapp.shared.generated.resources.kotlinconf_by_jetbrains_dark
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
import org.jetbrains.kotlinconf.ui.components.StyledText
import org.jetbrains.kotlinconf.ui.theme.KotlinConfTheme

@Composable
fun AboutConference(
    onPrivacyPolicy: () -> Unit,
    onGeneralTerms: () -> Unit,
    onWebsiteLink: () -> Unit,
    onBack: () -> Unit,
    onSpeaker: (SpeakerId) -> Unit,
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
            StyledText(text = stringResource(Res.string.about_conference_header), style = KotlinConfTheme.typography.h1)
            StyledText(text = stringResource(Res.string.about_conference_timeline))
            StyledText(text = stringResource(Res.string.about_conference_description))
            StyledText(text = stringResource(Res.string.about_conference_hashtag))
        }

        Spacer(modifier = Modifier.height(48.dp))

        Column(
            verticalArrangement = Arrangement.spacedBy(48.dp),
        ) {
            // TODO retrieve data from the service
            Event(
                month = "MAY",
                day = "22",
                line1 = stringResource(Res.string.about_conference_day1_title_line1),
                line2 = stringResource(Res.string.about_conference_day1_title_line2),
                description = "",
                speakers = listOf(
                    Speaker(SpeakerId("0"), "Hadi Hariri", "", "Never believed in elevator pitches", ""),
                    Speaker(SpeakerId("1"), "Hadi Hariri", "", "Never believed in elevator pitches", ""),
                ),
                location = "Hall A",
                time = "10:00 – 11:00",
                onSpeaker = onSpeaker,
            )

            Event(
                month = "MAY",
                day = "23",
                line1 = stringResource(Res.string.about_conference_day2_title_line1),
                line2 = stringResource(Res.string.about_conference_day2_title_line2),
                description = "",
                speakers = listOf(
                    Speaker(SpeakerId("2"), "Hadi Hariri", "", "Never believed in elevator pitches", ""),
                ),
                location = "Hall A",
                time = "10:00 – 11:00",
                onSpeaker = onSpeaker,
            )

            Event(
                month = "MAY",
                day = "22",
                day2 = "23",
                line1 = stringResource(Res.string.about_conference_codelabs_title_line1),
                line2 = stringResource(Res.string.about_conference_codelabs_title_line2),
                description = "TBA",
                speakers = emptyList(),
                location = "",
                time = "",
                backgroundColor = KotlinConfTheme.colors.tileBackground,
                onSpeaker = onSpeaker,
            )

            Event(
                month = "MAY",
                day = "22",
                line1 = stringResource(Res.string.about_conference_party_line1),
                line2 = stringResource(Res.string.about_conference_party_line2),
                description = stringResource(Res.string.about_conference_party_description),
                speakers = emptyList(),
                location = "Hall A",
                time = "18:00 – 22:30",
                backgroundColor = KotlinConfTheme.colors.tileBackground,
                onSpeaker = onSpeaker,
            )

            Event(
                month = "MAY",
                day = "23",
                line1 = stringResource(Res.string.about_conference_closingpanel_title_line1),
                line2 = stringResource(Res.string.about_conference_closingpanel_title_line2),
                description = "",
                speakers = listOf(
                    Speaker(SpeakerId("3"), "Hadi Hariri", "", "Never believed in elevator pitches", ""),
                ),
                location = "Hall A",
                time = "17:00 – 18:00",
                onSpeaker = onSpeaker,
            )
        }

        Image(
            painter = painterResource(
                if (KotlinConfTheme.colors.isDark) Res.drawable.kotlinconf_by_jetbrains_dark
                else Res.drawable.kotlinconf_by_jetbrains
            ),
            contentDescription = stringResource(Res.string.kotlinconf_by_jetbrains_description),
            modifier = Modifier.align(Alignment.CenterHorizontally)
                .padding(vertical = 64.dp)
                .widthIn(max = 360.dp)
        )

        Column(
            modifier = Modifier.padding(bottom = 24.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            PageMenuItem(stringResource(Res.string.about_conference_privacy_policy_link), onClick = onPrivacyPolicy)
            PageMenuItem(stringResource(Res.string.about_conference_general_terms_link), onClick = onGeneralTerms)
            PageMenuItem(stringResource(Res.string.about_conference_website_link), drawableResource = Res.drawable.arrow_up_right_24, onClick = onWebsiteLink)
        }
    }
}

@Composable
private fun Event(
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
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        DayHeader(month, day, line1, line2, modifier = Modifier.fillMaxWidth(), day2 = day2)

        if (description.isNotEmpty()) {
            StyledText(description, modifier = Modifier.padding(horizontal = 12.dp))
        }

        for (speaker in speakers) {
            key(speaker.id) {
                SpeakerCard(
                    name = speaker.name,
                    title = speaker.description,
                    photoUrl = speaker.photoUrl,
                    modifier = Modifier.padding(horizontal = 12.dp).fillMaxWidth(),
                    onClick = { onSpeaker(speaker.id) },
                )
            }
        }

        if (location.isNotEmpty() || time.isNotEmpty()) {
            Divider(1.dp, KotlinConfTheme.colors.strokePale)

            Row(Modifier.padding(horizontal = 12.dp).padding(bottom = 12.dp).fillMaxSize(), Arrangement.SpaceBetween) {
                StyledText(location, style = KotlinConfTheme.typography.text2)
                StyledText(time, style = KotlinConfTheme.typography.text2)
            }
        }
    }
}
