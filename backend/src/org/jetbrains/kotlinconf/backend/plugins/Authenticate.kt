package org.jetbrains.kotlinconf.backend.plugins

import io.ktor.http.HttpHeaders
import io.ktor.server.application.createRouteScopedPlugin
import io.ktor.server.auth.authentication
import io.ktor.server.request.header


val BearerChecker = createRouteScopedPlugin("BearerChecker") {
    val bearer = "Bearer "
    onCall { call ->
        val authorization = call.request.header(HttpHeaders.Authorization) ?: return@onCall
        if (!authorization.startsWith(bearer)) return@onCall

        val token = authorization.removePrefix(bearer).trim()
        call.authentication.principal(KotlinConfPrincipal(token))
    }
}

class KotlinConfPrincipal(val token: String)