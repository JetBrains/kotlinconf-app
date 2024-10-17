package org.jetbrains.kotlinconf.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.material.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import kotlinconfapp.shared.generated.resources.Res
import kotlinconfapp.shared.generated.resources.close
import kotlinconfapp.shared.generated.resources.smilehappy
import kotlinconfapp.shared.generated.resources.smilehappy_active
import kotlinconfapp.shared.generated.resources.smileneutral
import kotlinconfapp.shared.generated.resources.smileneutral_active
import kotlinconfapp.shared.generated.resources.smilesad
import kotlinconfapp.shared.generated.resources.smilesad_active
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.kotlinconf.Score
import org.jetbrains.kotlinconf.ui.HDivider
import org.jetbrains.kotlinconf.ui.painter
import org.jetbrains.kotlinconf.ui.theme.grey50
import org.jetbrains.kotlinconf.ui.theme.grey5Black
import org.jetbrains.kotlinconf.ui.theme.greyGrey5
import org.jetbrains.kotlinconf.ui.theme.greyGrey50
import org.jetbrains.kotlinconf.ui.theme.greyGrey80
import org.jetbrains.kotlinconf.ui.theme.greyWhite
import org.jetbrains.kotlinconf.ui.theme.whiteGrey

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
                "How was the talk?", style = MaterialTheme.typography.body2.copy(
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

@OptIn(ExperimentalResourceApi::class)
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
                        style = MaterialTheme.typography.body2.copy(
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
                    style = MaterialTheme.typography.body2.copy(
                        color = if (feedback.isEmpty()) grey50 else MaterialTheme.colors.greyGrey5
                    )
                )
            }
        }
        Row {
            Spacer(modifier = Modifier.weight(1f))
            IconButton(onClick = { onClose() }, Modifier.padding(4.dp)) {
                Icon(
                    painter = Res.drawable.close.painter(),
                    contentDescription = "Close",
                    tint = MaterialTheme.colors.greyGrey5
                )
            }
        }
    }
}

@OptIn(ExperimentalResourceApi::class)
@Composable
fun VoteBlock(vote: Score?, onVote: (Score?) -> Unit) {
    Row {
        VoteButton(
            vote = Score.GOOD,
            active = vote == Score.GOOD,
            icon = Res.drawable.smilehappy,
            activeIcon = Res.drawable.smilehappy_active,
            onVote = onVote
        )
        VoteButton(
            vote = Score.OK,
            active = vote == Score.OK,
            icon = Res.drawable.smileneutral,
            activeIcon = Res.drawable.smileneutral_active,
            onVote = onVote
        )
        VoteButton(
            vote = Score.BAD,
            active = vote == Score.BAD,
            icon = Res.drawable.smilesad,
            activeIcon = Res.drawable.smilesad_active,
            onVote = onVote
        )
    }
}

@OptIn(ExperimentalResourceApi::class)
@Composable
fun VoteButton(
    vote: Score,
    active: Boolean,
    icon: DrawableResource,
    activeIcon: DrawableResource,
    onVote: (Score?) -> Unit
) {
    IconButton(onClick = { onVote(if (active) null else vote) }) {
        Icon(
            painter = (if (active) activeIcon else icon).painter(),
            contentDescription = vote.toString(),
            tint = MaterialTheme.colors.greyWhite
        )
    }
}
