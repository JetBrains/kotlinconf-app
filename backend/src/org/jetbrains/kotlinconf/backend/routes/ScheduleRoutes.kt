package org.jetbrains.kotlinconf.backend.routes

import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import org.jetbrains.kotlinconf.backend.services.SessionizeService

import org.koin.ktor.ext.inject

/*
GET http://localhost:8080/conference
Accept: application/json
Authorization: Bearer 1238476512873162837
*/
fun Route.scheduleRoutes() {
    val sessionize: SessionizeService by inject()
    get("conference") {
        call.respond(sessionize.getConferenceData())
    }
}