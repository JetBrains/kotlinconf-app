package org.jetbrains.kotlinconf.backend.routes

import io.ktor.http.HttpStatusCode
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.jetbrains.kotlinconf.backend.repositories.KotlinConfRepository
import org.jetbrains.kotlinconf.backend.utils.ConferenceConfig
import org.koin.ktor.ext.inject
import kotlin.time.Clock

/*
POST http://localhost:8080/{year}/sign
1238476512873162837
 */
fun Route.userRoutes() {
    val repository by inject<KotlinConfRepository>()
    val config: ConferenceConfig by inject()

    post("sign") {
        val year = getYearFromPath(config)

        if (!isLiveRequest(config)) {
            return@post call.respond(ARCHIVED_YEAR_FORBIDDEN)
        }

        val userUUID = call.receive<String>()
        val timestamp = Clock.System.now().toLocalDateTime(TimeZone.UTC)
        repository.createUser(userUUID, timestamp)
        val signed = repository.signPolicy(userUUID, year, timestamp)
        val code = if (signed) HttpStatusCode.Created else HttpStatusCode.OK
        call.respond(code)
    }

    get("sign") {
        val year = getYearFromPath(config)
        val principal = call.validatePrincipal(repository)
            ?: return@get call.respond(HttpStatusCode.Unauthorized)
        val signed = repository.isPolicySigned(principal.token, year)
        val code = if (signed) HttpStatusCode.OK else HttpStatusCode.NotFound
        call.respond(code)
    }
}
