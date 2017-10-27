package org.jetbrains.kotlinconf

import org.jetbrains.kotlinconf.api.SessionsAPI
import kotlin.browser.window

val sessionsAPI = SessionsAPI(
        baseUrl = "",
        baseWsUrl = getWsServer()
)

private fun getWsServer(): String {
    val location = window.location
    val wsProtocol = if (location.protocol == "https:") "wss" else "ws"
    return "$wsProtocol://${location.host}"
}