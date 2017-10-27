package org.jetbrains.kotlinconf.components

import org.jetbrains.kotlinconf.SessionModel
import kotlinext.js.jsObject
import org.jetbrains.kotlinconf.api.Votes
import org.jetbrains.kotlinconf.api.VotesSubscription
import org.jetbrains.kotlinconf.api.async
import org.jetbrains.kotlinconf.sessionsAPI
import react.*
import react.dom.div
import react.dom.p
import react.dom.span
import kotlin.browser.window
import kotlin.js.Json
import kotlin.js.Math
import kotlin.js.json

class SessionComponent : RComponent<RouteResultProps<SessionProps>, SessionState>() {
    private var votesSubscription: VotesSubscription? = null
    private var mounted = false

    override fun RBuilder.render(): ReactElement? = div(classes = "session-view") {
        loading(state.session) { session ->
            div(classes = "session-app-title") { +"KotlinConf App" }

            div(classes = "session-badges") {
                for (badge in arrayOf("app-store", "google-play")) {
                    span(classes = "session-badge session-badge-$badge") {  }
                    +" "
                }
            }

            div(classes = "session-block") {
                renderSessionBlock(session)
            }

            val votes = state.votes
            if (votes != null) {
                renderVotes(votes)
            } else {
                div(classes = "session-votes-unavailable") {
                    +"Sorry, votes information unavailable"
                }
            }
        }
    }

    private fun RBuilder.renderSessionBlock(session: SessionModel) {
        div(classes = "session-info-block") {
            div(classes = "session-title") {
                +session.title
            }

            div(classes = "session-speakers") {
                span(classes = "session-speakers-label") { +"By " }
                for ((index, speaker) in session.speakers.withIndex()) {
                    if (index > 0) {
                        span(classes = "session-speaker-separator") { +", " }
                    }
                    span(classes = "session-speaker") { +(speaker.fullName ?: "<Unknown name>") }
                }
            }

            div(classes = "session-description") {
                div(classes = "session-subtitle") {
                    dateRange(session.startsAt to session.endsAt)
                    session.room?.let { div(classes = "session-room") { +it } }
                }

                for (part in session.description.split(lineSeparatorRegex)) {
                    p {
                        +part
                    }
                }
            }
        }

        div(classes = "session-smiles-label") { +"Tap to rate:" }

        div(classes = "session-smiles") {
            for (mood in arrayOf("happy", "neutral", "unhappy")) {
                +" "
                span(classes = "session-smile-small session-smile-small-$mood") {  }
            }
        }

        div(classes = "session-circle") { }
    }

    private fun RBuilder.renderVotes(votes: Votes) {
        val votesList = listOf(votes.good, votes.bad, votes.soso)
        val total = votesList.sum()

        fun percentage(value: Int) = if (total == 0) 0.0 else value.toDouble() / total.toDouble()
        var votePosition = 0.0
        fun RBuilder.votes(id: String, value: Int) {
            child("div", jsObject<StyledSpanProps> {}) {
                val percent = percentage(value)

                attrs {
                    classes = "session-votes-$id"
                    style = json(
                            "width" to percent.asRelativePosition(),
                            "left" to votePosition.asRelativePosition()
                    )
                }

                votePosition += percent

                div("session-votes-content") {
                    div("session-votes-cell") {
                        span(classes = "session-votes-cell-hack") { +"\u00A0" }
                        div("session-votes-container") {
                            span(classes = "session-votes-label") { +"\u00A0" }
                            span(classes = "session-votes-count-percentage") { +"${Math.round(percent * 100)}%" }
                            span(classes = "session-votes-count-absolute") { +"$value votes" }
                        }
                    }
                }
            }
        }

        div(classes = "session-votes-total") { +"Total: $total votes " }
        div(classes = "session-votes") {
            votes("good", votes.good)
            votes("soso", votes.soso)
            votes("bad", votes.bad)
        }
    }

    private fun Double.asRelativePosition() = (this * 100).toFixed(2) + "%"

    override fun componentDidMount() {
        mounted = true
        loadData()
        subscribeToVotes()
    }

    override fun componentWillUnmount() {
        mounted = false
        votesSubscription?.close()
    }

    private fun loadData() {
        if (!mounted) return

        async {
            updateState {
                session = sessionsAPI.fetchSession(props.match.params.id)
            }
        }

        window.setTimeout(this::loadData, 10000)
    }

    private fun subscribeToVotes() {
        votesSubscription = sessionsAPI.subscribeToVotes(props.match.params.id) {
            updateState {
                votes = it
            }
        }
    }
}

external interface SessionProps : RProps {
    var id: String
}

external interface SessionState : RState {
    var session: SessionModel?
    var votes: Votes?
}

private external interface StyledSpanProps : RProps {
    @JsName("className")
    var classes: String

    @JsName("style")
    var style: Json
}

private val lineSeparatorRegex = Regex("\\r|\\n|\\r|\\n")