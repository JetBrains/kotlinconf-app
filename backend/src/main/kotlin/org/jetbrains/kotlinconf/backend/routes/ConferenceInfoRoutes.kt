package org.jetbrains.kotlinconf.backend.routes

import io.ktor.http.ContentType
import io.ktor.server.response.respond
import io.ktor.server.response.respondBytes
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import org.jetbrains.kotlinconf.backend.services.ConferenceInfoService
import org.jetbrains.kotlinconf.backend.utils.ConferenceConfig
import org.jetbrains.kotlinconf.backend.utils.NotFound
import org.koin.ktor.ext.inject

/*
GET http://localhost:8080/conference-info
Accept: application/json

GET http://localhost:8080/{year}/conference-info
Accept: application/json

GET http://localhost:8080/{year}/partner-logos/{filename}
*/
fun Route.conferenceInfoRoutes() {
    val conferenceInfoService: ConferenceInfoService by inject()
    val config: ConferenceConfig by inject()

    get("conference-info") {
        val year = getYearFromPath(config)
        val conferenceInfo = conferenceInfoService.getConferenceInfo(year) ?: throw NotFound()
        call.respond(conferenceInfo)
    }

    get("partner-logos/{filename}") {
        val year = getYearFromPath(config)
        val filename = call.parameters["filename"] ?: throw NotFound()
        val logoBytes = conferenceInfoService.getPartnerLogo(year, filename) ?: throw NotFound()
        call.respondBytes(logoBytes, ContentType.Image.PNG)
    }
}
