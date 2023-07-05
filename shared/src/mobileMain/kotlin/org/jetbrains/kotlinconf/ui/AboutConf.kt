package org.jetbrains.kotlinconf.android.ui

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.layout.*
import androidx.compose.ui.platform.*
import androidx.compose.ui.res.*
import androidx.compose.ui.text.*
import androidx.compose.ui.text.font.*
import androidx.compose.ui.text.style.*
import androidx.compose.ui.unit.*
import coil.compose.*
import com.jetbrains.kotlinconf.R
import org.jetbrains.kotlinconf.*
import org.jetbrains.kotlinconf.android.theme.*

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
        Logo()
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
private fun Logo() {
    Image(
        painter = painterResource(id = R.drawable.about),
        contentDescription = null,
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 48.dp, bottom = 24.dp),
        contentScale = ContentScale.FillWidth
    )
}

@Composable
private fun Description() {
    Column(Modifier.background(MaterialTheme.colors.whiteGrey)) {
        Text(
            "KotlinConf is the official annual conference devoted to the Kotlin programming language. Organized by JetBrains, it is a place for the community to gather and discuss all things Kotlin.",
            style = MaterialTheme.typography.t2.copy(color = MaterialTheme.colors.greyGrey20),
            modifier = Modifier.padding(16.dp, top = 24.dp)
        )

        Text(
            "Social media hashtag:",
            style = MaterialTheme.typography.t2.copy(color = MaterialTheme.colors.greyGrey20),
            modifier = Modifier.padding(16.dp, top = 24.dp)
        )

        Text(
            "#KOTLINCONF",
            style = MaterialTheme.typography.t2.copy(
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
            model = photoUrl,
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
            style = MaterialTheme.typography.t2.copy(color = grey50),
            modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 4.dp, bottom = 24.dp)
        )
    }
}

@Composable
private fun TextTitle(tile: String, title: String) {
    Column(Modifier.background(MaterialTheme.colors.grey5Black)) {
        Text(
            tile.uppercase(),
            style = MaterialTheme.typography.t2.copy(color = MaterialTheme.colors.greyGrey20),
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
            model = speaker.photoUrl,
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
                style = MaterialTheme.typography.t2.copy(color = grey50),
                modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 4.dp, bottom = 24.dp)
            )
        }
    }
}

@Composable
private fun Labs() {
    Column(Modifier.background(MaterialTheme.colors.whiteGrey)) {
        HDivider(Modifier.padding(top = 48.dp))
        Row(Modifier.padding(16.dp)) {
            Icon(
                painter = painterResource(id = R.drawable.light),
                contentDescription = null,
                tint = orange,
                modifier = Modifier.padding(end = 8.dp)
            )

            Text(
                "28 Lightning talks!".uppercase(),
                style = MaterialTheme.typography.t2.copy(
                    color = MaterialTheme.colors.greyWhite,
                    fontWeight = FontWeight.Bold
                )
            )
        }

        Text(
            "Don't miss our new Lightning Talk track! Enjoy double the inspiration with two 15-minute talks in each time slot.",
            style = MaterialTheme.typography.t2.copy(color = MaterialTheme.colors.greyGrey20),
            modifier = Modifier.padding(start = 16.dp, end = 8.dp)
        )

        HDivider(Modifier.padding(top = 24.dp))

        Row(Modifier.padding(16.dp)) {
            Icon(
                painter = painterResource(id = R.drawable.aws_labs),
                contentDescription = null,
                tint = violet,
                modifier = Modifier.padding(end = 8.dp)
            )

            Text(
                "AWS labs / Bir Nerd Ranch labs".uppercase(),
                style = MaterialTheme.typography.t2.copy(
                    color = MaterialTheme.colors.greyWhite,
                    fontWeight = FontWeight.Bold
                )
            )
        }

        Text(
            "Sink your teeth into Kotlin with Code Labs by Big Nerd Ranch for general topics and AWS Labs for AWS/Kotlin tech!",
            style = MaterialTheme.typography.t2.copy(color = MaterialTheme.colors.greyGrey20),
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
            style = MaterialTheme.typography.t2.copy(color = MaterialTheme.colors.greyGrey20),
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
            style = MaterialTheme.typography.t2.copy(color = MaterialTheme.colors.greyGrey20),
            modifier = Modifier.padding(16.dp)
        )

        Image(
            painter = painterResource(id = R.drawable.closing), contentDescription = null,
            modifier = Modifier
                .fillMaxWidth(),
            contentScale = ContentScale.Crop,
        )
    }

    HDivider()
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
            style = MaterialTheme.typography.t2.copy(color = grey50),
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
            style = MaterialTheme.typography.t2.copy(color = MaterialTheme.colors.greyWhite),
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
            style = MaterialTheme.typography.t2.copy(color = MaterialTheme.colors.greyWhite),
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
            style = MaterialTheme.typography.t2.copy(color = MaterialTheme.colors.greyWhite),
            modifier = Modifier
                .padding(start = 16.dp, top = 8.dp, end = 16.dp, bottom = 24.dp)
                .clickable {
                    uriHandler.openUri("https://kotlinconf.com/kotlinconf-2023-general-terms-and-conditions-for-visitors.pdf")
                }
        )
    }
}
