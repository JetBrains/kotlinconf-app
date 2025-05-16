package org.jetbrains.kotlinconf.backend.routes

import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.jetbrains.kotlinconf.backend.services.SessionizeService
import org.jetbrains.kotlinconf.backend.utils.ConferenceConfig
import org.koin.ktor.ext.inject

/*
GET http://localhost:8080/sessionize/image/{imageId}
Authorization: Bearer 1238476512873162837
*/
fun Route.imageProxyRoutes() {
    val sessionize: SessionizeService by inject()
    val config: ConferenceConfig by inject()

    get("sessionize/image/{imageId}") {
        call.respond(sessionize.fetchImage(config.imagesUrl, call.parameters["imageId"] ?: error("No imageId")))
    }
}
