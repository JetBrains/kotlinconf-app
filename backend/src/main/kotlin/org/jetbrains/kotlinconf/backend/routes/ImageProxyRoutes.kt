package org.jetbrains.kotlinconf.backend.routes

import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.jetbrains.kotlinconf.backend.services.SessionizeService
import org.jetbrains.kotlinconf.backend.utils.ConferenceConfig
import org.koin.ktor.ext.inject

/*
GET http://localhost:8080/sessionize/image/{imageId}
Authorization: Bearer 1238476512873162837

GET http://localhost:8080/{year}/sessionize/image/{imageId}
Authorization: Bearer 1238476512873162837
*/
fun Route.imageProxyRoutes() {
    val sessionize: SessionizeService by inject()
    val config: ConferenceConfig by inject()

    get("sessionize/image/{imageId}") {
        // Sanity check the year in the path, though we don't use it here in any way,
        // just hope that Sessionize keeps serving these images for previous years
        getYearFromPath(config)

        call.respond(sessionize.fetchImage(config.imagesUrl, call.parameters["imageId"] ?: error("No imageId")))
    }
}
