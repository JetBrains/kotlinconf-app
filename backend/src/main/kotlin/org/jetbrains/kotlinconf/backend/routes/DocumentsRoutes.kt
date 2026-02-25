package org.jetbrains.kotlinconf.backend.routes

import io.ktor.http.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.jetbrains.kotlinconf.backend.services.AssetService
import org.jetbrains.kotlinconf.backend.services.AssetService.AssetType
import org.jetbrains.kotlinconf.backend.utils.BadRequest
import org.jetbrains.kotlinconf.backend.utils.ConferenceConfig
import org.jetbrains.kotlinconf.backend.utils.NotFound
import org.koin.ktor.ext.inject

/*
GET http://localhost:8080/{year}/documents
Accept: application/json

GET http://localhost:8080/{year}/documents/{name}
*/
fun Route.documentsRoutes() {
    val assetService: AssetService by inject()
    val config: ConferenceConfig by inject()

    get("documents/{name}") {
        val year = getYearFromPath(config)
        val name = call.parameters["name"] ?: throw BadRequest()
        val document = assetService.getAsset(year, AssetType.Document, name) ?: throw NotFound()
        call.respondText(document, Markdown)
    }
}

private val Markdown = ContentType(ContentType.Text.TYPE, "markdown")
