// ABOUTME: Serves the compiled Compose HTML admin SPA as static assets under /admin.
// ABOUTME: The shell is public; the JSON data endpoints stay guarded by the admin secret.
package org.jetbrains.kotlinconf.backend.routes

import io.ktor.server.http.content.default
import io.ktor.server.http.content.staticResources
import io.ktor.server.routing.Route

fun Route.adminPanelRoutes() {
    staticResources("/admin", "admin", index = "index.html") {
        default("index.html")
    }
}
