package org.jetbrains.kotlinconf.backend.routes

import io.ktor.http.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.jetbrains.kotlinconf.NewsListResponse
import org.jetbrains.kotlinconf.NewsRequest
import org.jetbrains.kotlinconf.backend.repositories.KotlinConfRepository
import org.jetbrains.kotlinconf.backend.utils.ConferenceConfig
import org.koin.ktor.ext.inject

fun Route.newsRoutes() {
    val repository by inject<KotlinConfRepository>()
    val config: ConferenceConfig by inject()

    route("news") {
        get {
            val news = repository.getAllNews()
            call.respond(NewsListResponse(news))
        }
        get("refresh") {
            call.checkAdminKey(config.adminSecret)
            val request = call.receive<NewsRequest>()
            val news = repository.createNews(request)
            call.respond(HttpStatusCode.Created, mapOf("id" to news.id))
        }
    }
}
