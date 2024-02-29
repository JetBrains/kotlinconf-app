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
import kotlinconfapp.shared.generated.resources.light
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.kotlinconf.Speaker
import org.jetbrains.kotlinconf.ui.theme.bannerText
import org.jetbrains.kotlinconf.ui.theme.blackGrey5
import org.jetbrains.kotlinconf.ui.theme.blackWhite
import org.jetbrains.kotlinconf.ui.theme.grey50
import org.jetbrains.kotlinconf.ui.theme.grey5Black
import org.jetbrains.kotlinconf.ui.theme.greyGrey20
import org.jetbrains.kotlinconf.ui.theme.greyWhite
import org.jetbrains.kotlinconf.ui.theme.orange
import org.jetbrains.kotlinconf.ui.theme.whiteGrey
import org.jetbrains.kotlinconf.ui.components.AboutConfSubtitle
import org.jetbrains.kotlinconf.ui.components.AboutConfTopBanner
import org.jetbrains.kotlinconf.ui.components.AsyncImage
import org.jetbrains.kotlinconf.ui.components.NavigationBar

@OptIn(ExperimentalResourceApi::class)
@Composable
fun AboutConfScreen(
    keynoteSpeakers: List<Speaker>,
    secondDaySpeakers: List<Speaker>,
    back: () -> Unit
) {
    Column(
        Modifier
            .verticalScroll(rememberScrollState())
            .background(MaterialTheme.colors.grey5Black)
            .fillMaxWidth()
    ) {
        NavigationBar(
            title = "About conference",
            isLeftVisible = true,
            onLeftClick = back,
            isRightVisible = false
        )
        AboutConfTopBanner()
        AboutConfSchedule()
        HDivider()
        AboutConfDescription()
        HDivider()
        AboutConfKeynoteSection(keynoteSpeakers)
        AboutConfSecondKeynote(secondDaySpeakers)
        HDivider()
        LightningTalks()
        Party()
        ClosingPanel()
        FindMore()
    }
}

@Composable
fun AboutConfSchedule() {
    val text = """
        May 22 — Workshops
        May 23–24 — Conference
        Bella Center Copenhagen, Denmark
    """.trimIndent()
    Text(
        text,
        style = MaterialTheme.typography.body2.copy(color = MaterialTheme.colors.blackGrey5),
        modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 24.dp, bottom = 24.dp)
    )
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun AboutConfDescription() {
    Column(Modifier.background(MaterialTheme.colors.whiteGrey)) {
        val text =
            """KotlinConfKotlinConf is the official annual conference devoted to the Kotlin programming language. Organized by JetBrains, it is a place for the community to gather and discuss all things Kotlin."""
        Text(
            text,
            style = MaterialTheme.typography.body2.copy(color = MaterialTheme.colors.greyGrey20),
            modifier = Modifier.padding(16.dp, top = 24.dp)
        )

        FlowRow(
            modifier = Modifier
                .padding(top = 10.dp, start = 16.dp, end = 16.dp, bottom = 24.dp)
        ) {
            Text(
                "Social media hashtag: ",
                style = MaterialTheme.typography.body2.copy(color = MaterialTheme.colors.greyGrey20),
            )

            Text(
                "#KotlinConf",
                style = MaterialTheme.typography.body2.copy(
                    color = MaterialTheme.colors.blackWhite,
                ),
            )
        }
    }
}

@Composable
private fun AboutConfKeynoteSection(keynoteSpeakers: List<Speaker>) {
    AboutConfSubtitle("May 23, 9:00", "Opening Keynote")
    HDivider()

    if (keynoteSpeakers.size < 4) return

    Row(Modifier.background(MaterialTheme.colors.whiteGrey)) {
        Column(Modifier.fillMaxWidth(0.5f)) {
            val speaker = keynoteSpeakers[2]
            KeynoteSectionSpeakerCard(speaker.name, speaker.photoUrl, speaker.position)
        }
        Column(Modifier.fillMaxWidth()) {
            val speaker = keynoteSpeakers[3]
            KeynoteSectionSpeakerCard(speaker.name, speaker.photoUrl, speaker.position)
        }
    }
    HDivider()
    Row(Modifier.background(MaterialTheme.colors.whiteGrey)) {
        Column(Modifier.fillMaxWidth(0.5f)) {
            val speaker = keynoteSpeakers[0]
            KeynoteSectionSpeakerCard(speaker.name, speaker.photoUrl, speaker.position)
        }
        VDivider()
        Column(Modifier.fillMaxWidth()) {
            val speaker = keynoteSpeakers[1]
            KeynoteSectionSpeakerCard(speaker.name, speaker.photoUrl, speaker.position)
        }
    }
    HDivider()
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
            contentDescription = "Speaker photo",
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

@Composable
private fun AboutConfSecondKeynote(secondDaySpeakers: List<Speaker>) {
    AboutConfSubtitle("May 24, 9:00", "Second day Keynote")
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
            contentDescription = "Speaker photo",
            contentScale = ContentScale.FillWidth
        )


        Column(
            Modifier.fillMaxWidth()
        ) {
            Text(
                speaker.name,
                style = MaterialTheme.typography.h2.copy(
                    color = MaterialTheme.colors.greyWhite,
                    fontWeight = FontWeight.Bold
                ),
                modifier = Modifier.padding(start = 16.dp, top = 16.dp, end = 8.dp)
            )

            Text(
                speaker.position,
                style = MaterialTheme.typography.body2.copy(color = grey50),
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
                "28 Lightning talks!".uppercase(),
                style = MaterialTheme.typography.body2.copy(
                    color = MaterialTheme.colors.greyWhite,
                    fontWeight = FontWeight.Bold
                )
            )
        }

        Text(
            "Don't miss our new Lightning Talk track! Enjoy double the inspiration with two 15-minute talks in each time slot.",
            style = MaterialTheme.typography.body2.copy(color = MaterialTheme.colors.greyGrey20),
            modifier = Modifier.padding(start = 16.dp, end = 8.dp)
        )

        HDivider(Modifier.padding(top = 24.dp))
    }
}

@Composable
private fun Party() {
    AboutConfSubtitle("May 23, 9:00", "Party")
    HDivider()
    Column(
        Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colors.whiteGrey)
    ) {
        Text(
            "Have fun and mingle with the community at the biggest Kotlin party of the year!",
            style = MaterialTheme.typography.body2.copy(color = MaterialTheme.colors.greyGrey20),
            modifier = Modifier.padding(16.dp)
        )
    }
    HDivider()
}

@Composable
private fun ClosingPanel() {
    AboutConfSubtitle("May 24, 17:15", "Closing Panel")
    HDivider()
    Column(
        Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colors.whiteGrey)
    ) {
        Text(
            "Come to Hall 1 and seize the opportunity to ask the KotlinConf speakers your questions in person.",
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
            "by \n" + "JetBrains",
            style = MaterialTheme.typography.bannerText.copy(color = MaterialTheme.colors.greyWhite),
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(
                    start = 24.dp, top = 70.dp
                )
        )
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun FindMore() {
    val uriHandler = LocalUriHandler.current
    Column(
        Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colors.whiteGrey)
    ) {
        FlowRow(modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 24.dp, bottom = 24.dp)) {
            Text(
                buildAnnotatedString {
                    append("You can find more information about the conference on the official website:")
                },
                style = MaterialTheme.typography.body2.copy(color = grey50),
                modifier = Modifier
                    .clickable {
                        uriHandler.openUri("https://kotlinconf.com")
                    }
            )

            Text(
                buildAnnotatedString {
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

        Text("For visitors:",
            style = MaterialTheme.typography.body2.copy(color = grey50),
            modifier = Modifier
                .padding(start = 16.dp, top = 24.dp, end = 16.dp, bottom = 16.dp)
        )
        Text(
            buildAnnotatedString {
                withStyle(SpanStyle(textDecoration = TextDecoration.Underline)) {
                    append("Privacy Policy")
                }
            },
            style = MaterialTheme.typography.body2.copy(color = MaterialTheme.colors.greyWhite),
            modifier = Modifier
                .padding(start = 16.dp, end = 16.dp)
                .clickable {
                    uriHandler.openUri("https://kotlinconf.com/kotlinconf-2023-privacy-policy-for-visitors.pdf")
                }
        )
        Text(
            buildAnnotatedString {
                withStyle(SpanStyle(textDecoration = TextDecoration.Underline)) {
                    append("General Terms and Conditions")
                }
            },
            style = MaterialTheme.typography.body2.copy(color = MaterialTheme.colors.greyWhite),
            modifier = Modifier
                .padding(start = 16.dp, top = 24.dp, end = 16.dp, bottom = 40.dp)
                .clickable {
                    uriHandler.openUri("https://kotlinconf.com/kotlinconf-2023-general-terms-and-conditions-for-visitors.pdf")
                }
        )
    }
}
