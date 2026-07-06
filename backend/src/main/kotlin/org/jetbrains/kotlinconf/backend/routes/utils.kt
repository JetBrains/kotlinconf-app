package org.jetbrains.kotlinconf.backend.routes

import io.ktor.http.HttpStatusCode
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.routing.*
import org.jetbrains.kotlinconf.backend.plugins.KotlinConfPrincipal
import org.jetbrains.kotlinconf.backend.repositories.KotlinConfRepository
import org.jetbrains.kotlinconf.backend.utils.ConferenceConfig
import org.jetbrains.kotlinconf.backend.utils.NotFound
import org.jetbrains.kotlinconf.backend.utils.Unauthorized

internal fun ApplicationCall.checkAdminKey(adminSecret: String) {
    val principal = principal<KotlinConfPrincipal>()
    if (principal?.token != adminSecret) {
        throw Unauthorized()
    }
}

internal suspend fun ApplicationCall.validatePrincipal(
    database: KotlinConfRepository,
): KotlinConfPrincipal? {
    val principal = principal<KotlinConfPrincipal>() ?: return null
    if (!database.validateUser(principal.token)) return null
    return principal
}

val ARCHIVED_YEAR_FORBIDDEN = HttpStatusCode(403, "Forbidden: Archived Year")

/**
 * Gets the year for this request from the "year" path parameter, then validates and returns it
 * (throws NotFound if the parameter is missing or invalid).
 */
internal fun RoutingContext.getYearFromPath(config: ConferenceConfig): Int {
    val yearParam = call.parameters["year"] ?: throw NotFound()

    val year = yearParam.toIntOrNull() ?: throw NotFound()

    if (year !in config.supportedYears) throw NotFound()

    return year
}

/**
 * Checks if the request contains an explicit year parameter that matches the current year.
 */
internal fun RoutingContext.isLiveRequest(config: ConferenceConfig): Boolean {
    val yearParam = call.parameters["year"] ?: return false
    val year = yearParam.toIntOrNull() ?: return false
    return year == config.currentYear
}
