package org.jetbrains.kotlinconf.backend

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.jetbrains.kotlinconf.backend.routes.newsRoutes
import org.jetbrains.kotlinconf.backend.routes.imageProxyRoutes
import org.jetbrains.kotlinconf.backend.routes.adminRoutes
import org.jetbrains.kotlinconf.backend.routes.timeRoutes
import org.jetbrains.kotlinconf.backend.routes.userRoutes
import org.jetbrains.kotlinconf.backend.routes.votingRoutes
import org.jetbrains.kotlinconf.backend.routes.scheduleRoutes


fun Application.routesModule() {
    routing {
        userRoutes()
        scheduleRoutes()
        votingRoutes()
        newsRoutes()
        adminRoutes()
        timeRoutes()
        imageProxyRoutes()

        get("/healthz") {
            call.respond(HttpStatusCode.OK)
        }
    }
}


