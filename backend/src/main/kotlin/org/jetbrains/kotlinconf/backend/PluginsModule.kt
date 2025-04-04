package org.jetbrains.kotlinconf.backend

import io.ktor.http.HttpHeaders
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.application.log
import io.ktor.server.plugins.autohead.AutoHeadResponse
import io.ktor.server.plugins.calllogging.CallLogging
import io.ktor.server.plugins.compression.Compression
import io.ktor.server.plugins.conditionalheaders.ConditionalHeaders
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import io.ktor.server.plugins.cors.routing.CORS
import io.ktor.server.plugins.defaultheaders.DefaultHeaders
import io.ktor.server.plugins.forwardedheaders.XForwardedHeaders
import io.ktor.server.plugins.statuspages.StatusPages
import io.ktor.server.response.respond
import io.ktor.util.logging.error
import org.jetbrains.kotlinconf.backend.plugins.BearerChecker
import org.jetbrains.kotlinconf.backend.utils.BadRequest
import org.jetbrains.kotlinconf.backend.utils.NotFound
import org.jetbrains.kotlinconf.backend.utils.SecretInvalidError
import org.jetbrains.kotlinconf.backend.utils.ServiceUnavailable
import org.jetbrains.kotlinconf.backend.utils.Unauthorized


fun Application.pluginsSetup() {
    val config = environment.config
    val serviceConfig = config.config("service")
    val mode = serviceConfig.property("environment").getString()
    val production = mode == "production"

    log.info("Environment: $mode")

    if (!production) {
        install(CallLogging)
    }

    install(DefaultHeaders)
    install(ConditionalHeaders)
    install(Compression)
    install(AutoHeadResponse)
    install(XForwardedHeaders)

    install(StatusPages) {
        exception<ServiceUnavailable> { call, _ ->
            call.respond(HttpStatusCode.ServiceUnavailable)
        }
        exception<BadRequest> { call, _ ->
            call.respond(HttpStatusCode.BadRequest)
        }
        exception<Unauthorized> { call, _ ->
            call.respond(HttpStatusCode.Unauthorized)
        }
        exception<NotFound> { call, _ ->
            call.respond(HttpStatusCode.NotFound)
        }
        exception<SecretInvalidError> { call, _ ->
            call.respond(HttpStatusCode.Forbidden)
        }
        exception<Throwable> { call, cause ->
            this@pluginsSetup.environment.log.error(cause)
            call.respond(HttpStatusCode.InternalServerError)
        }
    }

    install(CORS) {
        allowHeader(HttpHeaders.Authorization)
        allowHeader(HttpHeaders.CacheControl)
        allowCredentials = true
        allowNonSimpleContentTypes = true
        listOf(HttpMethod.Put, HttpMethod.Get, HttpMethod.Post, HttpMethod.Delete, HttpMethod.Options).forEach {
            allowMethod(it)
        }
        anyHost()
    }

    install(ContentNegotiation) {
        json()
    }

    install(BearerChecker)
}