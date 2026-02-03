package org.jetbrains.kotlinconf.backend.routes

import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import org.jetbrains.kotlinconf.backend.services.ArchivedDataService
import org.jetbrains.kotlinconf.backend.services.SessionizeService
import org.jetbrains.kotlinconf.backend.utils.ConferenceConfig
import org.jetbrains.kotlinconf.backend.utils.NotFound
import org.koin.ktor.ext.inject

/*
GET http://localhost:8080/conference
Accept: application/json
Authorization: Bearer 1238476512873162837

GET http://localhost:8080/{year}/conference
Accept: application/json
Authorization: Bearer 1238476512873162837
*/
fun Route.scheduleRoutes() {
    val sessionize: SessionizeService by inject()
    val archivedData: ArchivedDataService by inject()
    val config: ConferenceConfig by inject()

    get("conference") {
        val year = getYearFromPath(config)

        val conference = when (year) {
            config.currentYear -> sessionize.getConferenceData()
            else -> archivedData.getConferenceData(year) ?: throw NotFound()
        }

        call.respond(conference)
    }
}