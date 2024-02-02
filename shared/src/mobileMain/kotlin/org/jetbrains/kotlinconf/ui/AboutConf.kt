@file:OptIn(ExperimentalResourceApi::class)

package org.jetbrains.kotlinconf.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.kotlinconf.Speaker
import org.jetbrains.kotlinconf.theme.bannerText
import org.jetbrains.kotlinconf.theme.grey50
import org.jetbrains.kotlinconf.theme.grey5Black
import org.jetbrains.kotlinconf.theme.greyGrey20
import org.jetbrains.kotlinconf.theme.greyWhite
import org.jetbrains.kotlinconf.theme.orange
import org.jetbrains.kotlinconf.theme.violet
import org.jetbrains.kotlinconf.theme.whiteGrey
import org.jetbrains.kotlinconf.ui.components.AsyncImage

@OptIn(ExperimentalResourceApi::class)
@Composable
fun AboutConf(keynoteSpeakers: List<Speaker>, secondDaySpeakers: List<Speaker>, back: () -> Unit) {
    Column(
        Modifier
            .verticalScroll(rememberScrollState())
            .background(MaterialTheme.colors.grey5Black)
            .fillMaxWidth()
    ) {
        NavigationBar(
            title = "ABOUT", isLeftVisible = true, onLeftClick = back, isRightVisible = false
        )
        AboutConfTopBanner()
        HDivider()
        Description()
        HDivider()
        Keynote(keynoteSpeakers)
        SecondDayKeynote(secondDaySpeakers)
        HDivider()
        Labs()
        Party()
        ClosingPanel()
        FindMore()
    }
}

@Composable
private fun AboutConfTopBanner() {
    Image(
        painter = Res.drawable.about_conf_top_banner.painter(),
        contentDescription = null,
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 40.dp, bottom = 30.dp, start = 30.dp, end = 30.dp),
        contentScale = ContentScale.FillWidth
    )
}

@Composable
private fun Description() {
    Column(Modifier.background(MaterialTheme.colors.whiteGrey)) {
        Text(
            "KotlinConf is the official annual conference devoted to the Kotlin programming language. Organized by JetBrains, it is a place for the community to gather and discuss all things Kotlin.",
            style = MaterialTheme.typography.body2.copy(color = MaterialTheme.colors.greyGrey20),
            modifier = Modifier.padding(16.dp, top = 24.dp)
        )

        Text(
            "Social media hashtag:",
            style = MaterialTheme.typography.body2.copy(color = MaterialTheme.colors.greyGrey20),
            modifier = Modifier.padding(16.dp, top = 24.dp)
        )

        Text(
            "#KOTLINCONF",
            style = MaterialTheme.typography.body2.copy(
                color = MaterialTheme.colors.greyGrey20,
                fontWeight = FontWeight.Bold
            ),
            modifier = Modifier.padding(start = 16.dp, bottom = 48.dp)
        )
    }

}

@Composable
private fun Keynote(keynoteSpeakers: List<Speaker>) {
    TextTitle("APRIL 13 / 09:00", "Opening Keynote")
    HDivider()

    if (keynoteSpeakers.size < 4) return

    Row(Modifier.background(MaterialTheme.colors.whiteGrey)) {
        Column(Modifier.fillMaxWidth(0.5f)) {
            val speaker = keynoteSpeakers[2]
            SpeakerCard(speaker.name, speaker.photoUrl, speaker.position)
        }
        Column(Modifier.fillMaxWidth()) {
            val speaker = keynoteSpeakers[3]
            SpeakerCard(speaker.name, speaker.photoUrl, speaker.position)
        }
    }
    HDivider()
    Row(Modifier.background(MaterialTheme.colors.whiteGrey)) {
        Column(Modifier.fillMaxWidth(0.5f)) {
            val speaker = keynoteSpeakers[0]
            SpeakerCard(speaker.name, speaker.photoUrl, speaker.position)
        }
        VDivider()
        Column(Modifier.fillMaxWidth()) {
            val speaker = keynoteSpeakers[1]
            SpeakerCard(speaker.name, speaker.photoUrl, speaker.position)
        }
    }
    HDivider()
}

@Composable
private fun SpeakerCard(name: String, photoUrl: String, position: String) {
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
private fun TextTitle(tile: String, title: String) {
    Column(Modifier.background(MaterialTheme.colors.grey5Black)) {
        Text(
            tile.uppercase(),
            style = MaterialTheme.typography.body2.copy(color = MaterialTheme.colors.greyGrey20),
            modifier = Modifier.padding(16.dp, top = 24.dp)
        )

        Text(
            title.uppercase(),
            style = MaterialTheme.typography.h2.copy(
                color = MaterialTheme.colors.greyWhite,
                fontWeight = FontWeight.Bold
            ),
            modifier = Modifier.padding(start = 16.dp, bottom = 24.dp)
        )
    }
}

@Composable
private fun SecondDayKeynote(secondDaySpeakers: List<Speaker>) {
    TextTitle("APRIL 14 / 09:00", "Second day Keynote")
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
private fun Labs() {
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

        Row(Modifier.padding(16.dp)) {
            Icon(
                painter = Res.drawable.aws_lab.painter(),
                contentDescription = null,
                tint = violet,
                modifier = Modifier.padding(end = 8.dp)
            )

            Text(
                "AWS labs / Bir Nerd Ranch labs".uppercase(),
                style = MaterialTheme.typography.body2.copy(
                    color = MaterialTheme.colors.greyWhite,
                    fontWeight = FontWeight.Bold
                )
            )
        }

        Text(
            "Sink your teeth into Kotlin with Code Labs by Big Nerd Ranch for general topics and AWS Labs for AWS/Kotlin tech!",
            style = MaterialTheme.typography.body2.copy(color = MaterialTheme.colors.greyGrey20),
            modifier = Modifier.padding(start = 16.dp, end = 8.dp)
        )

        HDivider(Modifier.padding(top = 24.dp))
    }
}

@Composable
private fun Party() {
    TextTitle("APRIL 13 / 18:00", "KotlinConf’23 Party ")
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
    TextTitle("APRIL 14 / 17:15", "Closing Panel")
    HDivider()
    Column(
        Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colors.whiteGrey)
    ) {
        Text(
            "Come to Effectenbeurszaal and seize the opportunity to ask the KotlinConf speakers your questions in person.",
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

@Composable
private fun FindMore() {
    val uriHandler = LocalUriHandler.current
    Column(
        Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colors.whiteGrey)
    ) {
        Text(
            buildAnnotatedString {
                append("You can find more information about the conference on the official website:")
            },
            style = MaterialTheme.typography.body2.copy(color = grey50),
            modifier = Modifier
                .padding(start = 16.dp, top = 16.dp)
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
                .padding(start = 16.dp)
                .clickable {
                    uriHandler.openUri("https://kotlinconf.com")
                }
        )
        Text(
            buildAnnotatedString {
                withStyle(SpanStyle(textDecoration = TextDecoration.Underline)) {
                    append("Privacy Policy for Visitors")
                }
            },
            style = MaterialTheme.typography.body2.copy(color = MaterialTheme.colors.greyWhite),
            modifier = Modifier
                .padding(start = 16.dp, top = 24.dp)
                .clickable {
                    uriHandler.openUri("https://kotlinconf.com/kotlinconf-2023-privacy-policy-for-visitors.pdf")
                }
        )
        Text(
            buildAnnotatedString {
                withStyle(SpanStyle(textDecoration = TextDecoration.Underline)) {
                    append("General Terms and Conditions for Visitors")
                }
            },
            style = MaterialTheme.typography.body2.copy(color = MaterialTheme.colors.greyWhite),
            modifier = Modifier
                .padding(start = 16.dp, top = 8.dp, end = 16.dp, bottom = 24.dp)
                .clickable {
                    uriHandler.openUri("https://kotlinconf.com/kotlinconf-2023-general-terms-and-conditions-for-visitors.pdf")
                }
        )
    }
}
