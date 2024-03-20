package org.jetbrains.kotlinconf.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import kotlinconfapp.shared.generated.resources.Res
import kotlinconfapp.shared.generated.resources.about_conf_bottom_banner
import kotlinconfapp.shared.generated.resources.about_conf_description
import kotlinconfapp.shared.generated.resources.about_conf_footer
import kotlinconfapp.shared.generated.resources.about_conf_schedule
import kotlinconfapp.shared.generated.resources.about_conference
import kotlinconfapp.shared.generated.resources.by_jetbrains
import kotlinconfapp.shared.generated.resources.closing_description
import kotlinconfapp.shared.generated.resources.closing_time
import kotlinconfapp.shared.generated.resources.closing_title
import kotlinconfapp.shared.generated.resources.for_visitors
import kotlinconfapp.shared.generated.resources.general_terms
import kotlinconfapp.shared.generated.resources.hashtag
import kotlinconfapp.shared.generated.resources.keynote_start_time
import kotlinconfapp.shared.generated.resources.keynote_title
import kotlinconfapp.shared.generated.resources.light
import kotlinconfapp.shared.generated.resources.lightning_talks_description
import kotlinconfapp.shared.generated.resources.lightning_talks_title
import kotlinconfapp.shared.generated.resources.party_description
import kotlinconfapp.shared.generated.resources.party_time
import kotlinconfapp.shared.generated.resources.party_title
import kotlinconfapp.shared.generated.resources.privacy_policy
import kotlinconfapp.shared.generated.resources.second_day_keynote_speaker
import kotlinconfapp.shared.generated.resources.second_day_keynote_time
import kotlinconfapp.shared.generated.resources.second_day_keynote_title
import kotlinconfapp.shared.generated.resources.social_media_hashtag_text
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.kotlinconf.ConferenceService
import org.jetbrains.kotlinconf.Speaker
import org.jetbrains.kotlinconf.ui.components.AboutConfSubtitle
import org.jetbrains.kotlinconf.ui.components.AboutConfTopBanner
import org.jetbrains.kotlinconf.ui.components.AsyncImage
import org.jetbrains.kotlinconf.ui.components.NavigationBar
import org.jetbrains.kotlinconf.ui.theme.bannerText
import org.jetbrains.kotlinconf.ui.theme.blackGrey5
import org.jetbrains.kotlinconf.ui.theme.blackWhite
import org.jetbrains.kotlinconf.ui.theme.grey50
import org.jetbrains.kotlinconf.ui.theme.grey5Black
import org.jetbrains.kotlinconf.ui.theme.greyGrey20
import org.jetbrains.kotlinconf.ui.theme.greyWhite
import org.jetbrains.kotlinconf.ui.theme.orange
import org.jetbrains.kotlinconf.ui.theme.whiteGrey

@OptIn(ExperimentalResourceApi::class)
@Composable
fun AboutConfScreen(
    service: ConferenceService,
    showVisitorsPrivacyPolicy: () -> Unit,
    showVisitorsTerms: () -> Unit,
    back: () -> Unit
) {
    val sessionCards by service.sessionCards.collectAsState()
    val speakers by service.speakers.collectAsState()
    val keynoteSpeakers = sessionCards
        .firstOrNull { it.title == stringResource(Res.string.keynote_title) }
        ?.speakerIds
        ?.map { service.speakerById(it) }
        ?: emptyList()

    val secondDaySpeaker = speakers.all.filter {
        it.name == stringResource(Res.string.second_day_keynote_speaker)
    }

    val time by service.time.collectAsState()
    val timeString =
        "${time.month.name} ${time.dayOfMonth} ${time.hours}:${time.minutes}:${time.seconds}"
    Column(
        Modifier
            .background(MaterialTheme.colors.grey5Black)
            .fillMaxWidth()
    ) {
        NavigationBar(
            title = stringResource(Res.string.about_conference),
            isLeftVisible = true,
            onLeftClick = back,
            isRightVisible = false
        )
        Column(
            Modifier.fillMaxWidth()
                .verticalScroll(rememberScrollState())
        ) {
            AboutConfTopBanner()
            AboutConfSchedule()
            HDivider()
            AboutConfDescription()
            HDivider()
            AboutConfKeynoteSection(keynoteSpeakers)
            AboutConfSecondKeynote(secondDaySpeaker)
            HDivider()
            LightningTalks()
            Party()
            ClosingPanel()
            AboutConferenceFooter(timeString, showVisitorsPrivacyPolicy, showVisitorsTerms)
        }
    }
}

@OptIn(ExperimentalResourceApi::class)
@Composable
fun AboutConfSchedule() {
    Text(
        stringResource(Res.string.about_conf_schedule),
        style = MaterialTheme.typography.body2.copy(color = MaterialTheme.colors.blackGrey5),
        modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 24.dp, bottom = 24.dp)
    )
}

@OptIn(ExperimentalLayoutApi::class, ExperimentalResourceApi::class)
@Composable
private fun AboutConfDescription() {
    Column(Modifier.background(MaterialTheme.colors.whiteGrey)) {
        Text(
            stringResource(Res.string.about_conf_description),
            style = MaterialTheme.typography.body2.copy(color = MaterialTheme.colors.greyGrey20),
            modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 24.dp)
        )

        FlowRow(
            modifier = Modifier
                .padding(top = 10.dp, start = 16.dp, end = 16.dp, bottom = 24.dp)
        ) {
            Text(
                stringResource(Res.string.social_media_hashtag_text),
                style = MaterialTheme.typography.body2.copy(color = MaterialTheme.colors.greyGrey20),
            )

            Text(
                stringResource(Res.string.hashtag),
                style = MaterialTheme.typography.body2.copy(
                    color = MaterialTheme.colors.blackWhite,
                ),
            )
        }
    }
}

@OptIn(ExperimentalResourceApi::class)
@Composable
private fun AboutConfKeynoteSection(keynoteSpeakers: List<Speaker>) {
    AboutConfSubtitle(
        stringResource(Res.string.keynote_start_time),
        stringResource(Res.string.keynote_title)
    )
    HDivider()
    val rows = keynoteSpeakers.size / 2

    repeat(rows) {
        Row(Modifier.background(MaterialTheme.colors.whiteGrey)) {
            Column(Modifier.fillMaxWidth(0.5f)) {
                val speaker = keynoteSpeakers[it * 2]
                KeynoteSectionSpeakerCard(speaker.name, speaker.photoUrl, speaker.position)
            }
            Column(Modifier.fillMaxWidth()) {
                val speaker = keynoteSpeakers[it * 2 + 1]
                KeynoteSectionSpeakerCard(speaker.name, speaker.photoUrl, speaker.position)
            }
        }
        HDivider()
    }
}

@Composable
private fun KeynoteSectionSpeakerCard(name: String, photoUrl: String, position: String) {
    Column(
        Modifier.background(MaterialTheme.colors.whiteGrey)
    ) {
        AsyncImage(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 0.dp, end = 0.dp),
            imageUrl = photoUrl,
            contentDescription = "",
            contentScale = ContentScale.FillWidth,
        )

        Text(
            name,
            style = MaterialTheme.typography.h4.copy(
                color = MaterialTheme.colors.greyWhite,
                fontWeight = FontWeight.Bold
            ),
            modifier = Modifier.padding(start = 16.dp, top = 16.dp, end = 8.dp)
        )

        Text(
            position,
            style = MaterialTheme.typography.body2.copy(color = grey50),
            modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 4.dp, bottom = 24.dp)
        )
    }
}

@OptIn(ExperimentalResourceApi::class)
@Composable
private fun AboutConfSecondKeynote(secondDaySpeakers: List<Speaker>) {
    AboutConfSubtitle(
        stringResource(Res.string.second_day_keynote_time),
        stringResource(Res.string.second_day_keynote_title)
    )
    HDivider()

    if (secondDaySpeakers.isEmpty()) return
    val speaker = secondDaySpeakers.first()
    Row(
        Modifier.background(MaterialTheme.colors.whiteGrey)
    ) {
        AsyncImage(
            modifier = Modifier
                .fillMaxWidth(0.5f)
                .padding(start = 0.dp, end = 0.dp),
            imageUrl = speaker.photoUrl,
            contentDescription = "",
            contentScale = ContentScale.FillWidth
        )


        Column(
            Modifier.fillMaxWidth()
        ) {
            Text(
                speaker.name,
                style = MaterialTheme.typography.h4.copy(
                    color = MaterialTheme.colors.greyWhite,
                    fontWeight = FontWeight.Bold
                ),
                modifier = Modifier.padding(start = 16.dp, top = 16.dp, end = 8.dp)
            )

            Text(
                speaker.position,
                style = MaterialTheme.typography.body2.copy(color = MaterialTheme.colors.greyWhite),
                modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 4.dp, bottom = 24.dp)
            )
        }
    }
}

@OptIn(ExperimentalResourceApi::class)
@Composable
private fun LightningTalks() {
    Column(Modifier.background(MaterialTheme.colors.whiteGrey)) {
        HDivider(Modifier.padding(top = 48.dp))
        Row(Modifier.padding(16.dp)) {
            Icon(
                painter = Res.drawable.light.painter(),
                contentDescription = null,
                tint = orange,
                modifier = Modifier.padding(end = 8.dp)
            )

            Text(
                stringResource(Res.string.lightning_talks_title),
                style = MaterialTheme.typography.body2.copy(
                    color = MaterialTheme.colors.greyWhite,
                    fontWeight = FontWeight.Bold
                )
            )
        }

        Text(
            stringResource(Res.string.lightning_talks_description),
            style = MaterialTheme.typography.body2.copy(color = MaterialTheme.colors.greyGrey20),
            modifier = Modifier.padding(start = 16.dp, end = 8.dp)
        )

        HDivider(Modifier.padding(top = 24.dp))
    }
}

@OptIn(ExperimentalResourceApi::class)
@Composable
private fun Party() {
    AboutConfSubtitle(
        stringResource(Res.string.party_time),
        stringResource(Res.string.party_title)
    )
    HDivider()
    Column(
        Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colors.whiteGrey)
    ) {
        Text(
            stringResource(Res.string.party_description),
            style = MaterialTheme.typography.body2.copy(color = MaterialTheme.colors.greyGrey20),
            modifier = Modifier.padding(16.dp)
        )
    }
    HDivider()
}

@OptIn(ExperimentalResourceApi::class)
@Composable
private fun ClosingPanel() {
    AboutConfSubtitle(
        stringResource(Res.string.closing_time),
        stringResource(Res.string.closing_title)
    )
    HDivider()
    Column(
        Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colors.whiteGrey)
    ) {
        Text(
            stringResource(Res.string.closing_description),
            style = MaterialTheme.typography.body2.copy(color = MaterialTheme.colors.greyGrey20),
            modifier = Modifier.padding(16.dp)
        )

        BottomBanner()
    }

    HDivider()
}

@OptIn(ExperimentalResourceApi::class)
@Composable
private fun BottomBanner() {
    Box(Modifier.fillMaxWidth()) {
        Image(
            painter = Res.drawable.about_conf_bottom_banner.painter(),
            contentDescription = null,
            contentScale = ContentScale.None,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(start = 42.dp, end = 25.dp, bottom = 53.dp)
                .height(112.dp),
        )
        Text(
            stringResource(Res.string.by_jetbrains),
            style = MaterialTheme.typography.bannerText.copy(color = MaterialTheme.colors.greyWhite),
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(
                    start = 24.dp, top = 70.dp
                )
        )
    }
}

@OptIn(ExperimentalLayoutApi::class, ExperimentalResourceApi::class)
@Composable
private fun AboutConferenceFooter(
    time: String,
    showVisitorsPrivacyPolicy: () -> Unit,
    showVisitorsTerms: () -> Unit
) {
    val uriHandler = LocalUriHandler.current
    Column(
        Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colors.whiteGrey)
    ) {
        FlowRow(
            modifier = Modifier.padding(
                start = 16.dp,
                end = 16.dp,
                top = 24.dp,
                bottom = 24.dp
            )
        ) {
            Text(
                buildAnnotatedString {
                    append(stringResource(Res.string.about_conf_footer))
                    withStyle(SpanStyle(textDecoration = TextDecoration.Underline)) {
                        append("kotlinconf.com")
                    }
                },
                style = MaterialTheme.typography.body2.copy(color = MaterialTheme.colors.greyWhite),
                modifier = Modifier
                    .clickable {
                        uriHandler.openUri("https://kotlinconf.com")
                    }
            )
        }

        HDivider()

        Text(
            stringResource(Res.string.for_visitors),
            style = MaterialTheme.typography.body2.copy(color = grey50),
            modifier = Modifier
                .padding(start = 16.dp, top = 24.dp, end = 16.dp, bottom = 16.dp)
        )
        Text(
            buildAnnotatedString {
                withStyle(SpanStyle(textDecoration = TextDecoration.Underline)) {
                    append(stringResource(Res.string.privacy_policy))
                }
            },
            style = MaterialTheme.typography.body2.copy(color = MaterialTheme.colors.greyWhite),
            modifier = Modifier
                .padding(start = 16.dp, end = 16.dp)
                .clickable {
                    showVisitorsPrivacyPolicy()
                }
        )
        Text(
            buildAnnotatedString {
                withStyle(SpanStyle(textDecoration = TextDecoration.Underline)) {
                    append(stringResource(Res.string.general_terms))
                }
            },
            style = MaterialTheme.typography.body2.copy(color = MaterialTheme.colors.greyWhite),
            modifier = Modifier
                .padding(start = 16.dp, top = 24.dp, end = 16.dp, bottom = 40.dp)
                .clickable {
                    showVisitorsTerms()
                }
        )
        Text(
            "Server time: $time",
            style = MaterialTheme.typography.body2.copy(color = grey50),
            modifier = Modifier.padding(16.dp)
        )
    }
}
