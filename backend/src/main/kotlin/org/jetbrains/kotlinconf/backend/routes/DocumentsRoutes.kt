package org.jetbrains.kotlinconf.backend.routes

import io.ktor.http.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.jetbrains.kotlinconf.backend.services.DocumentsService
import org.jetbrains.kotlinconf.backend.utils.ConferenceConfig
import org.jetbrains.kotlinconf.backend.utils.NotFound
import org.koin.ktor.ext.inject

/*
GET http://localhost:8080/{year}/documents
Accept: application/json

GET http://localhost:8080/{year}/documents/{name}
*/
fun Route.documentsRoutes() {
    val documentsService: DocumentsService by inject()
    val config: ConferenceConfig by inject()

    get("documents") {
        val year = getYearFromPath(config)
        val documents = documentsService.getAllDocuments(year) ?: throw NotFound()
        call.respond(documents)
    }

    get("documents/{name}") {
        val year = getYearFromPath(config)
        val name = call.parameters["name"] ?: return@get call.respond(HttpStatusCode.BadRequest)
        val document = documentsService.getDocument(year, name)
        if (document != null) {
            call.respondText(document, ContentType.Text.Markdown)
        } else {
            call.respond(HttpStatusCode.NotFound)
        }
    }
}

val ContentType.Text.Markdown
    get() = ContentType(TYPE, "markdown")
