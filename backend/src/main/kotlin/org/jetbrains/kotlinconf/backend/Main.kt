package org.jetbrains.kotlinconf.backend

import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.application.ApplicationCallPipeline.ApplicationPhase.Plugins
import io.ktor.server.auth.*
import io.ktor.server.http.content.*
import io.ktor.server.plugins.autohead.*
import io.ktor.server.plugins.callloging.*
import io.ktor.server.plugins.compression.*
import io.ktor.server.plugins.conditionalheaders.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.plugins.cors.routing.*
import io.ktor.server.plugins.defaultheaders.*
import io.ktor.server.plugins.forwardedheaders.*
import io.ktor.server.plugins.partialcontent.*
import io.ktor.server.plugins.statuspages.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.util.logging.*

fun main(args: Array<String>): Unit =
    io.ktor.server.netty.EngineMain.main(args)

fun Application.conferenceBackend() {
    val config = environment.config
    val serviceConfig = config.config("service")
    val mode = serviceConfig.property("environment").getString()
    log.info("Environment: $mode")
    val sessionizeConfig = config.config("sessionize")
    val sessionizeUrl = sessionizeConfig.property("url").getString()
    val sessionizeInterval = sessionizeConfig.property("interval").getString().toLong()
    val adminSecret = serviceConfig.property("secret").getString()
    val production = mode == "production"

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
            this@conferenceBackend.environment.log.error(cause)
            call.respond(HttpStatusCode.InternalServerError)
        }
    }

    install(ContentNegotiation) {
        json()
    }

    install(CORS) {
        anyHost()
        allowHeader(HttpHeaders.Authorization)
        allowCredentials = true
        listOf(HttpMethod.Put, HttpMethod.Delete, HttpMethod.Options).forEach { allowMethod(it) }
    }

    val database = Store(this)
    routing {
        authenticate()
        static {
            default("static/index.html")
            files("static")
        }

        api(database, sessionizeUrl, adminSecret)

        get("/healthz") {
            call.respond(HttpStatusCode.OK)
        }
    }

    launchSyncJob(sessionizeUrl, sessionizeInterval)
}

private fun Route.authenticate() {
    val bearer = "Bearer "
    intercept(Plugins) {
        val authorization = call.request.header(HttpHeaders.Authorization) ?: return@intercept
        if (!authorization.startsWith(bearer)) return@intercept
        val token = authorization.removePrefix(bearer).trim()
        call.authentication.principal(KotlinConfPrincipal(token))
    }
}

internal class KotlinConfPrincipal(val token: String) : Principal
