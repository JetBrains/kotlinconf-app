package org.jetbrains.kotlinconf.backend.routes

import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import org.jetbrains.kotlinconf.AppConfig
import org.jetbrains.kotlinconf.backend.utils.ConferenceConfig
import org.koin.ktor.ext.inject

/*
GET http://localhost:8080/config
Accept: application/json
*/
fun Route.configRoutes() {
    val config: ConferenceConfig by inject()

    get("config") {
        val message = AppConfig(
            currentYear = config.currentYear,
            supportedYears = config.supportedYears,
        )
        call.respond(message)
    }
}
