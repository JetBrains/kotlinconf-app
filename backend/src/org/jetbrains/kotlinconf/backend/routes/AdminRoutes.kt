package org.jetbrains.kotlinconf.backend.routes

import io.ktor.http.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.jetbrains.kotlinconf.backend.services.SessionizeService
import org.jetbrains.kotlinconf.backend.utils.ConferenceConfig
import org.koin.ktor.ext.inject

/*
POST http://localhost:8080/sessionizeSync
*/
fun Route.adminRoutes() {
    val config: ConferenceConfig by inject()
    val sesionize: SessionizeService by inject()

    post("sessionizeSync") {
        call.checkAdminKey(config.adminSecret)

        sesionize.synchronizeWithSessionize(config.sessionizeUrl)
        call.respond(HttpStatusCode.OK)
    }
}