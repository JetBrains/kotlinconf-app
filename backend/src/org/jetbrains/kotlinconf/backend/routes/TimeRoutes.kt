package org.jetbrains.kotlinconf.backend.routes

import io.ktor.http.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.util.date.*
import org.jetbrains.kotlinconf.backend.services.TimeService
import org.jetbrains.kotlinconf.backend.utils.ConferenceConfig
import org.koin.ktor.ext.inject

/**
 * Admin endpoints
 * GET http://localhost:8080/time
 *
 * POST http://localhost:8080/time/1589568000000
 */
fun Route.timeRoutes() {
    val config: ConferenceConfig by inject()
    val timeService: TimeService by inject()

    get("time") {
        call.respond(timeService.now())
    }
    post("time/{timestamp}") {
        call.checkAdminKey(config.adminSecret)

        val timestamp = call.parameters["timestamp"] ?: error("No time")
        val time = if (timestamp == "null") {
            null
        } else {
            GMTDate(timestamp.toLong())
        }

        timeService.updateTime(time)
        call.respond(HttpStatusCode.OK)
    }
}
