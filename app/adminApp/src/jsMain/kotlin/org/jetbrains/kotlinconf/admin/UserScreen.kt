// ABOUTME: Per-user view: everything one anonymous user voted and the feedback they left.
// ABOUTME: Reached by clicking a pseudo-name; shows the friendly name plus the raw UUID key.
package org.jetbrains.kotlinconf.admin

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import org.jetbrains.compose.web.attributes.*
import org.jetbrains.compose.web.dom.*
import org.jetbrains.kotlinconf.Score

@Composable
fun UserScreen(data: AggregatedData, userId: String, onBack: () -> Unit) {
    val votes = remember(data, userId) { data.votesForUser(userId) }
    val feedback = remember(data, userId) { data.feedbackForUser(userId) }

    Div(attrs = { classes("crumbs") }) {
        Button(
            attrs = {
                classes("link")
                onClick { onBack() }
            },
        ) {
            Text("← Back to overview")
        }
    }

    Div(attrs = { classes("card") }) {
        Div(attrs = { classes("user-meta") }) {
            H1 { Text(pseudoName(userId)) }
            Span(attrs = { classes("pill") }) {
                Text("${votes.size} vote" + if (votes.size == 1) "" else "s")
            }
            Span(attrs = { classes("pill") }) {
                Text("${feedback.size} comment" + if (feedback.size == 1) "" else "s")
            }
        }
        Div(attrs = { classes("user-id") }) { Text(userId) }
    }

    Div(attrs = { classes("card") }) {
        H2(attrs = { classes("section-title") }) { Text("Votes") }
        if (votes.isEmpty()) {
            Div(attrs = { classes("empty-state") }) { Text("This user hasn't voted.") }
        } else {
            Table {
                Thead {
                    Tr {
                        Th { Text("Session") }
                        Th(attrs = { classes("right") }) { Text("Score") }
                    }
                }
                Tbody {
                    votes.forEach { vote ->
                        Tr {
                            Td(attrs = { classes("title") }) { Text(vote.title) }
                            Td(attrs = { classes("num") }) { Text(scoreLabel(vote.score)) }
                        }
                    }
                }
            }
        }
    }

    Div(attrs = { classes("card") }) {
        H2(attrs = { classes("section-title") }) { Text("Feedback") }
        if (feedback.isEmpty()) {
            Div(attrs = { classes("empty-state") }) { Text("This user hasn't left feedback.") }
        } else {
            feedback.forEach { comment ->
                Div(attrs = { classes("talk-block") }) {
                    Div(attrs = { classes("talk-title") }) { Text(comment.title) }
                    Div(
                        attrs = {
                            style {
                                property("white-space", "pre-wrap")
                                property("margin-top", "4px")
                                property("color", "#374151")
                            }
                        },
                    ) {
                        Text(comment.value)
                    }
                }
            }
        }
    }
}

private fun scoreLabel(score: Score?): String = if (score != null) "${score.emoji()} $score"
else "—"
