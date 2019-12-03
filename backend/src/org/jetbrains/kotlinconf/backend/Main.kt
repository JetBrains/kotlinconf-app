package org.jetbrains.kotlinconf.backend

import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.features.*
import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.serialization.*
import io.ktor.util.*

internal fun Application.main() {
    val config = environment.config
    val serviceConfig = config.config("service")
    val mode = serviceConfig.property("environment").getString()
    log.info("Environment: $mode")
    val sessionizeConfig = config.config("sessionize")
    val sessionizeUrl = sessionizeConfig.property("url").getString()
    val oldSessionizeUrl = sessionizeConfig.property("oldUrl").getString()
    val sessionizeInterval = sessionizeConfig.property("interval").getString().toLong()
    val adminSecret = serviceConfig.property("secret").getString()
    val production = mode == "production"

    if (!production) {
        install(CallLogging)
    }

    install(DefaultHeaders)
    install(ConditionalHeaders)
    install(Compression)
    install(PartialContent)
    install(AutoHeadResponse)
    install(XForwardedHeaderSupport)
    install(StatusPages) {
        exception<ServiceUnavailable> { _ ->
            call.respond(HttpStatusCode.ServiceUnavailable)
        }
        exception<BadRequest> { _ ->
            call.respond(HttpStatusCode.BadRequest)
        }
        exception<Unauthorized> { _ ->
            call.respond(HttpStatusCode.Unauthorized)
        }
        exception<NotFound> { _ ->
            call.respond(HttpStatusCode.NotFound)
        }
        exception<SecretInvalidError> { _ ->
            call.respond(HttpStatusCode.Forbidden)
        }
        exception<Throwable> { cause ->
            environment.log.error(cause)
            call.respond(HttpStatusCode.InternalServerError)
        }
    }

    install(ContentNegotiation) {
        serialization()
    }

    install(CORS) {
        anyHost()
        header(HttpHeaders.Authorization)
        allowCredentials = true
        listOf(HttpMethod.Put, HttpMethod.Delete, HttpMethod.Options).forEach { method(it) }
    }

    val database = Database(this)
    install(Routing) {
        authenticate()
        static {
            default("static/index.html")
            files("static")
        }

        api(database, sessionizeUrl, oldSessionizeUrl, adminSecret)
    }

    launchSyncJob(sessionizeUrl, oldSessionizeUrl, sessionizeInterval)
}

private fun Route.authenticate() {
    val bearer = "Bearer "
    intercept(ApplicationCallPipeline.Features) {
        val authorization = call.request.header(HttpHeaders.Authorization) ?: return@intercept
        if (!authorization.startsWith(bearer)) return@intercept
        val token = authorization.removePrefix(bearer).trim()
        call.authentication.principal(KotlinConfPrincipal(token))
    }
}

internal class KotlinConfPrincipal(val token: String) : Principal
