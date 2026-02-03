package org.jetbrains.kotlinconf.backend

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.jetbrains.kotlinconf.backend.routes.adminRoutes
import org.jetbrains.kotlinconf.backend.routes.conferenceInfoRoutes
import org.jetbrains.kotlinconf.backend.routes.imageProxyRoutes
import org.jetbrains.kotlinconf.backend.routes.scheduleRoutes
import org.jetbrains.kotlinconf.backend.routes.timeRoutes
import org.jetbrains.kotlinconf.backend.routes.userRoutes
import org.jetbrains.kotlinconf.backend.routes.votingRoutes


fun Application.routesModule() {
    routing {
        // Year-agnostic routes (no year prefix needed)
        get("/healthz") {
            call.respond(HttpStatusCode.OK)
        }
        timeRoutes()
        adminRoutes()

        // Routes without the year prefix for backwards compatibility
        yearBasedRoutes()

        // Routes with year prefix: /{year}/...
        route("/{year}") {
            yearBasedRoutes()
        }
    }
}

/**
 * Registers all year-based routes.
 * Route handlers will check for the "year" path parameter:
 * - If present: validate and use that year
 * - If absent: use current year
 */
private fun Route.yearBasedRoutes() {
    userRoutes()
    scheduleRoutes()
    votingRoutes()
    imageProxyRoutes()
    conferenceInfoRoutes()
}
