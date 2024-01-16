package org.jetbrains.kotlinconf.ui.components

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.input.key.*
import androidx.compose.ui.text.input.*
import androidx.compose.ui.unit.*
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.kotlinconf.*
import org.jetbrains.kotlinconf.theme.*
import org.jetbrains.kotlinconf.ui.HDivider

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

@OptIn(ExperimentalComposeUiApi::class, ExperimentalResourceApi::class)
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
                        if (it.key != Key.Enter) return@onKeyEvent false
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
                    painter = Drawables.CLOSE_ICON,
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
            icon = Drawables.SMILE_HAPPY,
            activeIcon = Drawables.SMILE_HAPPY_ACTIVE,
            onVote = onVote
        )
        VoteButton(
            vote = Score.OK,
            active = vote == Score.OK,
            icon = Drawables.SMILE_NEUTRAL,
            activeIcon = Drawables.SMILE_NEUTRAL_ACTIVE,
            onVote = onVote
        )
        VoteButton(
            vote = Score.BAD,
            active = vote == Score.BAD,
            icon = Drawables.SMILE_SAD,
            activeIcon = Drawables.SMILE_SAD_ACTIVE,
            onVote = onVote
        )
    }
}

@Composable
fun VoteButton(
    vote: Score,
    active: Boolean,
    icon: Painter,
    activeIcon: Painter,
    onVote: (Score?) -> Unit
) {
    IconButton(onClick = { onVote(if (active) null else vote) }) {
        Icon(
            painter = if (active) activeIcon else icon,
            contentDescription = vote.toString(),
            tint = MaterialTheme.colors.greyWhite
        )
    }
}
