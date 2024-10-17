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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import kotlinconfapp.shared.generated.resources.Res
import kotlinconfapp.shared.generated.resources.privacy_note
import kotlinconfapp.shared.generated.resources.privacy_policy_bird
import kotlinconfapp.shared.generated.resources.privacy_policy_title
import kotlinconfapp.shared.generated.resources.read_privacy
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.kotlinconf.ui.painter
import org.jetbrains.kotlinconf.ui.theme.grey20Grey80
import org.jetbrains.kotlinconf.ui.theme.greyGrey20
import org.jetbrains.kotlinconf.ui.theme.greyWhite
import org.jetbrains.kotlinconf.ui.theme.whiteGrey

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
        Column(Modifier.fillMaxHeight().verticalScroll(rememberScrollState())) {
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
                stringResource(Res.string.privacy_policy_title),
                style = MaterialTheme.typography.h2.copy(color = MaterialTheme.colors.greyWhite),
                modifier = Modifier.padding(start = 16.dp, end = 16.dp, bottom = 24.dp)
            )

            Text(
                stringResource(Res.string.privacy_note),
                style = MaterialTheme.typography.body2.copy(color = MaterialTheme.colors.greyGrey20),
                modifier = Modifier.padding(start = 16.dp, end = 16.dp, bottom = 24.dp)
            )

            Text(
                stringResource(Res.string.read_privacy),
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
            Spacer(Modifier.height(80.dp))
        }
    }
}
