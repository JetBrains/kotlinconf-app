package org.jetbrains.kotlinconf.backend.routes

import io.ktor.server.application.*
import io.ktor.server.auth.*
import org.jetbrains.kotlinconf.backend.plugins.KotlinConfPrincipal
import org.jetbrains.kotlinconf.backend.repositories.KotlinConfRepository
import org.jetbrains.kotlinconf.backend.utils.Unauthorized

internal fun ApplicationCall.checkAdminKey(adminSecret: String) {
    val principal = principal<KotlinConfPrincipal>()
    if (principal?.token != adminSecret) {
        throw Unauthorized()
    }
}

internal suspend fun ApplicationCall.validatePrincipal(database: KotlinConfRepository): KotlinConfPrincipal? {
    val principal = principal<KotlinConfPrincipal>() ?: return null
    if (!database.validateUser(principal.token)) return null
    return principal
}
