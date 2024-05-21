package org.jetbrains.kotlinconf.backend

import io.ktor.http.HttpHeaders
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.application.Application
import io.ktor.server.application.createRouteScopedPlugin
import io.ktor.server.application.install
import io.ktor.server.application.log
import io.ktor.server.auth.Principal
import io.ktor.server.auth.authentication
import io.ktor.server.plugins.autohead.AutoHeadResponse
import io.ktor.server.plugins.callloging.CallLogging
import io.ktor.server.plugins.compression.Compression
import io.ktor.server.plugins.conditionalheaders.ConditionalHeaders
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import io.ktor.server.plugins.cors.routing.CORS
import io.ktor.server.plugins.defaultheaders.DefaultHeaders
import io.ktor.server.plugins.forwardedheaders.XForwardedHeaders
import io.ktor.server.plugins.statuspages.StatusPages
import io.ktor.server.request.header
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import io.ktor.server.routing.routing
import io.ktor.util.logging.error

fun main(args: Array<String>): Unit =
    io.ktor.server.netty.EngineMain.main(args)

fun Application.conferenceBackend() {
    val config = environment.config
    val serviceConfig = config.config("service")
    val mode = serviceConfig.property("environment").getString()
    log.info("Environment: $mode")
    val sessionizeConfig = config.config("sessionize")
    val imagesUrl = sessionizeConfig.property("imagesUrl").getString()
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

    install(CORS){
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

    val database = Store(this)
    routing {
        authenticate()
        api(database, sessionizeUrl, imagesUrl, adminSecret)

        get("/healthz") {
            call.respond(HttpStatusCode.OK)
        }
    }

    launchSyncJob(sessionizeUrl, sessionizeInterval)
}

private fun Route.authenticate() {
    install(Authenticate)
}

private val Authenticate = createRouteScopedPlugin("Authenticate") {
    val bearer = "Bearer "
    onCall { call ->
        val authorization = call.request.header(HttpHeaders.Authorization) ?: return@onCall
        if (!authorization.startsWith(bearer)) return@onCall

        val token = authorization.removePrefix(bearer).trim()
        call.authentication.principal(KotlinConfPrincipal(token))
    }
}

internal class KotlinConfPrincipal(val token: String) : Principal
