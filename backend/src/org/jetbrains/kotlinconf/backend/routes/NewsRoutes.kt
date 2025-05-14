package org.jetbrains.kotlinconf.backend.routes

import io.ktor.http.HttpStatusCode
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.jetbrains.kotlinconf.NewsListResponse
import org.jetbrains.kotlinconf.backend.services.NewsService
import org.jetbrains.kotlinconf.backend.utils.ConferenceConfig
import org.koin.ktor.ext.inject

fun Route.newsRoutes() {
    val news by inject<NewsService>()
    val config: ConferenceConfig by inject()

    route("news") {
        get {
            val news = news.getNews()
            call.respond(NewsListResponse(news))
        }
        get("refresh") {
            call.checkAdminKey(config.adminSecret)
            news.updateNews()
            call.respond(HttpStatusCode.OK)
        }
    }
}
