package org.jetbrains.kotlinconf.backend.routes

import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import org.jetbrains.kotlinconf.backend.services.GoldenKodeeService
import org.jetbrains.kotlinconf.backend.utils.ConferenceConfig
import org.jetbrains.kotlinconf.backend.utils.NotFound
import org.koin.ktor.ext.inject

/*
GET http://localhost:8080/golden-kodee
Accept: application/json

GET http://localhost:8080/{year}/golden-kodee
Accept: application/json
*/
fun Route.goldenKodeeRoutes() {
    val goldenKodeeService: GoldenKodeeService by inject()
    val config: ConferenceConfig by inject()

    get("golden-kodee") {
        val year = getYearFromPath(config)
        val data = goldenKodeeService.getGoldenKodeeData(year) ?: throw NotFound()
        call.respond(data)
    }
}
