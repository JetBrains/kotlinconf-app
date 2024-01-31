package org.jetbrains.kotlinconf.ui.welcome

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import kotlinconfapp.shared.generated.resources.Res
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.kotlinconf.theme.grey20Grey80
import org.jetbrains.kotlinconf.theme.greyGrey20
import org.jetbrains.kotlinconf.theme.greyWhite
import org.jetbrains.kotlinconf.theme.whiteGrey
import org.jetbrains.kotlinconf.ui.painter

@OptIn(ExperimentalResourceApi::class)
@Composable
fun WelcomePrivacyPolicyScreen(
    onAcceptPrivacy: () -> Unit,
    showDetails: () -> Unit,
    onClose: () -> Unit
) {
    FormWithButtons(onAccept = {
        onAcceptPrivacy()
        onClose()
    }, onReject = {
        onClose()
    }) {
        Column(Modifier.fillMaxHeight()) {
            Image(
                painter = Res.drawable.privacy_policy_bird.painter(),
                contentDescription = null,
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(top = 42.dp, bottom = 50.dp)
                    .width(70.dp)
                    .height(64.dp)
                    .fillMaxWidth()
            )

            Text(
                "Privacy policy",
                style = MaterialTheme.typography.h2.copy(color = MaterialTheme.colors.greyWhite),
                modifier = Modifier.padding(start = 16.dp, end = 16.dp, bottom = 24.dp)
            )

            Text(
                PRIVACY_NOTE,
                style = MaterialTheme.typography.body2.copy(color = MaterialTheme.colors.greyGrey20),
                modifier = Modifier.padding(start = 16.dp, end = 16.dp, bottom = 24.dp)
            )

            Text(
                "Read the app privacy policy",
                style = MaterialTheme.typography.body2.copy(
                    color = MaterialTheme.colors.greyWhite,
                    textDecoration = TextDecoration.Underline
                ),
                modifier = Modifier
                    .padding(16.dp)
                    .clickable { showDetails() }
            )

            Spacer(Modifier.fillMaxSize())

            Row(Modifier.padding(16.dp)) {
                Box(
                    modifier = Modifier
                        .clip(RectangleShape)
                        .size(12.dp)
                        .border(BorderStroke(1.dp, MaterialTheme.colors.greyGrey20), RectangleShape)
                        .background(MaterialTheme.colors.greyWhite)
                )

                Spacer(Modifier.width(8.dp))

                Box(
                    modifier = Modifier
                        .clip(RectangleShape)
                        .size(12.dp)
                        .border(
                            BorderStroke(1.dp, MaterialTheme.colors.grey20Grey80),
                            RectangleShape
                        )
                        .background(MaterialTheme.colors.whiteGrey)
                )
            }
        }
    }
}

internal const val PRIVACY_NOTE = """
For you to maximize the benefits of this app, we need your consent to access and use your device ID so you can provide and we can collect and track your feedback about talks. 

We will process your data in accordance with the App Privacy Policy. You can adjust or withdraw your consent at any time by sending a request to privacy@jetbrains.com, but doing so may affect how this app functions.
"""