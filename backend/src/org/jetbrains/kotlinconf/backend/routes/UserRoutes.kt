package org.jetbrains.kotlinconf.backend.routes

import io.ktor.http.HttpStatusCode
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.post
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.jetbrains.kotlinconf.backend.repositories.KotlinConfRepository
import org.koin.ktor.ext.inject
import kotlin.getValue

/*
POST http://localhost:8080/sign
1238476512873162837
 */
fun Route.userRoutes() {
    val repository by inject<KotlinConfRepository>()

    post("sign") {
        val userUUID = call.receive<String>()
        val timestamp = Clock.System.now().toLocalDateTime(TimeZone.UTC)
        val created = repository.createUser(userUUID, timestamp)
        val code = if (created) HttpStatusCode.Created else HttpStatusCode.Conflict
        call.respond(code)
    }
}
