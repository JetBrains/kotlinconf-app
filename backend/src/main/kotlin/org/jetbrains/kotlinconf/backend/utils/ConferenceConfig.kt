package org.jetbrains.kotlinconf.backend.utils

import io.ktor.server.config.ApplicationConfig

class ConferenceConfig(config: ApplicationConfig) {
    val imagesUrl: String = config.property("sessionize.imagesUrl").getString()
    val sessionizeUrl: String = config.property("sessionize.url").getString()
    val sessionizeInterval = config.property("sessionize.interval").getString().toLong()

    val adminSecret: String = config.property("service.secret").getString()

    val newsRepo: String = config.property("news.repo").getString()
    val newsBranch: String = config.property("news.branch").getString()
    val newsFolder: String = config.property("news.folder").getString()
}