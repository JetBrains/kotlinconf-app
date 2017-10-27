package org.jetbrains.kotlinconf.components

import org.jetbrains.kotlinconf.data.Session
import org.jetbrains.kotlinconf.api.async
import org.jetbrains.kotlinconf.sessionsAPI
import react.*
import react.dom.div
import kotlin.browser.window


class SessionsComponent : RComponent<RProps, SessionsState>() {
    private var mounted = false

    override fun RBuilder.render(): ReactElement? = div(classes = "sessions") {
        loading(state.sessions ?: state.sessionsError) {
            val err = state.sessionsError
            if (err != null) {
                div {
                    +err
                }
            }
            else {
                for (session in state.sessions!!) {
                    div(classes = "session") {
                        div(classes = "session-title") {
                            routeLink("/session/${session.id}") {
                                +(session.title ?: "<untitled>")
                            }
                        }

                        dateRange(session.startsAt to session.endsAt)
                    }
                }
            }
        }
    }

    override fun componentDidMount() {
        mounted = true
        loadData()
    }

    override fun componentWillUnmount() {
        mounted = false
    }

    private fun loadData() {
        if (!mounted) return

        async {
            setState {
                try {
                    sessions = sessionsAPI.fetchSessions()
                    sessionsError = null
                } catch (e: Exception) {
                    sessionsError = "Error loading list of sessions:" + e.message
                }
            }
        }

        window.setTimeout(this::loadData, 10000)
    }
}

external interface SessionsState : RState {
    var sessions: List<Session>?
    var sessionsError: String?
}