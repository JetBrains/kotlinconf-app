package org.jetbrains.kotlinconf.android.ui

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.focus.*
import androidx.compose.ui.input.key.*
import androidx.compose.ui.res.*
import androidx.compose.ui.text.input.*
import androidx.compose.ui.tooling.preview.*
import androidx.compose.ui.unit.*
import com.jetbrains.kotlinconf.R
import org.jetbrains.kotlinconf.*
import org.jetbrains.kotlinconf.android.theme.*

@Composable
fun VoteAndFeedback(
    vote: Score?,
    showFeedbackBlock: Boolean,
    onVote: (Score?) -> Unit,
    onSubmitFeedback: (String) -> Unit,
    onCloseFeedback: () -> Unit
) {
    Column(
        Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colors.whiteGrey)
    ) {
        Row(Modifier.padding(16.dp)) {
            Text(
                "How was the talk?", style = MaterialTheme.typography.t2.copy(
                    color = MaterialTheme.colors.greyGrey50
                ),
                modifier = Modifier.padding(top = 12.dp)
            )

            Spacer(modifier = Modifier.weight(1f))

            VoteBlock(vote, onVote)
        }
        HDivider()

        if (showFeedbackBlock) {
            FeedbackForm(onSend = {
                onSubmitFeedback(it)
            }, onClose = {
                onCloseFeedback()
            })
        }
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun FeedbackForm(onSend: (String) -> Unit, onClose: () -> Unit) {
    var feedback by remember { mutableStateOf("") }

    Box {
        Column(
            Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colors.grey5Black)
        ) {
            OutlinedTextField(
                value = feedback,
                onValueChange = {
                    feedback = it
                },
                placeholder = {
                    Text(
                        "Would you like to share a comment?",
                        style = MaterialTheme.typography.t2.copy(
                            color = MaterialTheme.colors.greyGrey80
                        )
                    )
                },
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                keyboardActions = KeyboardActions(
                    onDone = {
                        if (feedback.isNotEmpty()) onSend(feedback)
                    }
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .onKeyEvent {
                        if (it.nativeKeyEvent.keyCode != android.view.KeyEvent.KEYCODE_ENTER) return@onKeyEvent false
                        if (feedback.isNotEmpty()) onSend(feedback)
                        true
                    },
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    backgroundColor = MaterialTheme.colors.grey5Black,
                    focusedBorderColor = MaterialTheme.colors.grey5Black,
                    unfocusedBorderColor = MaterialTheme.colors.grey5Black,
                    disabledTextColor = grey50,
                    disabledLabelColor = MaterialTheme.colors.grey5Black
                ),
            )

            Button(
                onClick = { if (feedback.isNotEmpty()) onSend(feedback) },
                modifier = Modifier
                    .align(Alignment.CenterHorizontally),
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = MaterialTheme.colors.whiteGrey,
                    contentColor = MaterialTheme.colors.greyGrey5
                ),
                elevation = ButtonDefaults.elevation(0.dp, 0.dp, 0.dp, 0.dp, 0.dp)
            ) {
                Text(
                    "SEND MY COMMENT",
                    style = MaterialTheme.typography.t2.copy(
                        color = if (feedback.isEmpty()) grey50 else MaterialTheme.colors.greyGrey5
                    )
                )
            }
        }
        Row {
            Spacer(modifier = Modifier.weight(1f))
            IconButton(onClick = { onClose() }, Modifier.padding(4.dp)) {
                Icon(
                    painter = painterResource(id = R.drawable.close),
                    contentDescription = "Close",
                    tint = MaterialTheme.colors.greyGrey5
                )
            }
        }
    }
}


@Composable
fun VoteBlock(vote: Score?, onVote: (Score?) -> Unit) {
    Row {
        VoteButton(
            vote = Score.GOOD,
            active = vote == Score.GOOD,
            icon = R.drawable.smilehappy,
            activeIcon = R.drawable.smilehappy_active,
            onVote = onVote
        )
        VoteButton(
            vote = Score.OK,
            active = vote == Score.OK,
            icon = R.drawable.smileneutral,
            activeIcon = R.drawable.smileneutral_active,
            onVote = onVote
        )
        VoteButton(
            vote = Score.BAD,
            active = vote == Score.BAD,
            icon = R.drawable.smilesad,
            activeIcon = R.drawable.smilesad_active,
            onVote = onVote
        )
    }
}

@Composable
fun VoteButton(
    vote: Score,
    active: Boolean,
    icon: Int,
    activeIcon: Int,
    onVote: (Score?) -> Unit
) {
    IconButton(onClick = { onVote(if (active) null else vote) }) {
        Icon(
            painter = painterResource(if (active) activeIcon else icon),
            contentDescription = vote.toString(),
            tint = MaterialTheme.colors.greyWhite
        )
    }
}

@Composable
@Preview
fun FeedbackFormPreview() {
    FeedbackForm(onSend = {}, onClose = {})
}
