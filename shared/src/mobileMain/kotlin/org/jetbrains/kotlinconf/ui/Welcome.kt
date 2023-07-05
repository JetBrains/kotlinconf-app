package org.jetbrains.kotlinconf.android.ui

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.res.*
import androidx.compose.ui.text.style.*
import androidx.compose.ui.tooling.preview.*
import androidx.compose.ui.unit.*
import com.jetbrains.kotlinconf.R
import org.jetbrains.kotlinconf.android.theme.*

@Composable
fun WelcomeScreen(
    onAcceptPrivacy: () -> Unit,
    onRejectPrivacy: () -> Unit,
    onAcceptNotifications: () -> Unit,
    onClose: () -> Unit
) {
    var step by remember { mutableStateOf(0) }
    var showFullPrivacyPolicy by remember { mutableStateOf(false) }

    if (showFullPrivacyPolicy) {
        FullPrivacyPolicy(onAccept = {
            onAcceptPrivacy()
        }, onClose = {
            onRejectPrivacy()
            showFullPrivacyPolicy = false
            step += 1
        })
    } else if (step == 0) {
        PrivacyPolicy(onAcceptPrivacy, showDetails = {
            showFullPrivacyPolicy = true
        }) {
            onRejectPrivacy()
            step += 1
        }
    } else {
        Notifications(onAcceptNotifications) {
            onClose()
        }
    }
}

@Composable
private fun FullPrivacyPolicy(
    onAccept: () -> Unit,
    onClose: () -> Unit
) {
    FormWithButtons(onAccept = {
        onAccept()
        onClose()
    }, onReject = onClose) {
        PrivacyPolicy()
    }
}

@Composable
private fun PrivacyPolicy(
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
        Column(Modifier.fillMaxWidth()) {
            Image(
                painter = painterResource(id = R.drawable.privacy),
                contentDescription = null,
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(top = 16.dp, bottom = 16.dp)
                    .height(136.dp)
                    .fillMaxWidth()
            )

            Text(
                "privacy policy".uppercase(),
                style = MaterialTheme.typography.h2.copy(color = MaterialTheme.colors.greyWhite),
                modifier = Modifier.padding(start = 16.dp, end = 16.dp, bottom = 24.dp)
            )

            Text(
                PRIVACY_NOTE,
                style = MaterialTheme.typography.t2.copy(color = MaterialTheme.colors.greyGrey20),
                modifier = Modifier.padding(start = 16.dp, end = 16.dp, bottom = 24.dp)
            )

            Text(
                "READ THE APP PRIVACY POLICY",
                style = MaterialTheme.typography.t2.copy(
                    color = MaterialTheme.colors.greyWhite,
                    textDecoration = TextDecoration.Underline
                ),
                modifier = Modifier
                    .padding(16.dp)
                    .clickable { showDetails() }
            )


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

@Composable
private fun FormWithButtons(
    onAccept: () -> Unit,
    onReject: () -> Unit,
    body: @Composable () -> Unit
) {
    Column(
        Modifier
            .background(MaterialTheme.colors.whiteGrey)
            .fillMaxSize(),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) {
            Column(Modifier.verticalScroll(rememberScrollState())) {
                body()
            }
        }
        Column {
            Buttons(onAccept, onReject)
        }
    }
}

@Composable
private fun Buttons(onAccept: () -> Unit, onReject: () -> Unit) {
    Column(Modifier.height(68.dp)) {
        HDivider()
        Row {
            Button(
                onClick = { onReject() },
                modifier = Modifier
                    .weight(1f)
                    .height(68.dp)
                    .shadow(0.dp),
                shape = RectangleShape,
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = MaterialTheme.colors.whiteGrey,
                )
            ) {
                Text(
                    "REJECT",
                    style = MaterialTheme.typography.t2.copy(color = MaterialTheme.colors.greyGrey5)
                )
            }
            Button(
                onClick = { onAccept() },
                modifier = Modifier
                    .weight(1f)
                    .height(68.dp)
                    .shadow(0.dp),
                shape = RectangleShape,
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = MaterialTheme.colors.greyWhite,
                )
            ) {
                Text(
                    "ACCEPT",
                    style = MaterialTheme.typography.t2.copy(color = MaterialTheme.colors.grey5Grey)
                )
            }
        }
    }
}


@Composable
private fun Notifications(onAcceptNotifications: () -> Unit, onClose: () -> Unit) {
    FormWithButtons(onAccept = {
        onAcceptNotifications()
        onClose()
    }, onReject = {
        onClose()
    }) {
        Column(Modifier.fillMaxWidth()) {
            Image(
                painter = painterResource(id = R.drawable.notifications),
                contentDescription = null,
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(top = 16.dp, bottom = 16.dp)
                    .height(136.dp)
                    .fillMaxWidth()
            )

            Text(
                "Do you want reminders?".uppercase(),
                style = MaterialTheme.typography.h2.copy(color = MaterialTheme.colors.greyWhite),
                modifier = Modifier.padding(start = 16.dp, end = 16.dp, bottom = 24.dp)
            )

            Text(
                REMINDERS_TEXT,
                style = MaterialTheme.typography.t2.copy(color = MaterialTheme.colors.greyGrey20),
                modifier = Modifier.padding(start = 16.dp, end = 16.dp, bottom = 24.dp)
            )

            Row(Modifier.padding(16.dp)) {
                Box(
                    modifier = Modifier
                        .clip(RectangleShape)
                        .size(12.dp)
                        .border(BorderStroke(1.dp, MaterialTheme.colors.greyGrey20), RectangleShape)
                        .background(MaterialTheme.colors.whiteGrey)
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
                        .background(MaterialTheme.colors.greyWhite)
                )
            }
        }
    }
}

@Composable
@Preview(showBackground = true)
private fun PrivacyPolicyPreview() {
    KotlinConfTheme {
        Notifications({}, {})
    }
}

@Composable
@Preview
private fun WelcomeScreenPreview() {
    KotlinConfTheme {
        WelcomeScreen({}, {}, {}, {})
    }
}

internal const val REMINDERS_TEXT =
    "Get reminders about talks that you don’t want to miss. You will receive a message 5 minutes before the session begins so you have time to get ready."

internal const val PRIVACY_NOTE =
    "The app provides the opportunity for you to provide feedback about talks. We collect such input anonymously. In case you decide to post any personal data in your feedback, we shall process your data in accordance with the Privacy Policy"