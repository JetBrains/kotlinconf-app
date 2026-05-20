// ABOUTME: Ktor JS client for the admin JSON endpoints, authenticated with the admin bearer token.
// ABOUTME: Calls are origin-absolute (e.g. /2026/vote/all) so they hit the API, not the /admin shell.

package org.jetbrains.kotlinconf.admin

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.js.Js
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.http.HttpHeaders
import io.ktor.serialization.kotlinx.json.json
import kotlinx.browser.window
import kotlinx.serialization.json.Json
import org.jetbrains.kotlinconf.Conference

/**
 * Fetches admin data from the backend that serves this SPA (the page origin).
 * Paths are origin-absolute so they hit the API rather than the /admin shell.
 */
class AdminApi(private val token: String) {
    private val origin: String = window.location.origin

    private val client = HttpClient(Js) {
        install(ContentNegotiation) {
            json(Json { ignoreUnknownKeys = true })
        }
    }

    suspend fun conference(year: Int): Conference = get("$origin/$year/conference")

    suspend fun votes(year: Int): List<AdminVoteRow> = get("$origin/$year/vote/all")

    suspend fun feedback(year: Int): List<AdminFeedbackRow> = get("$origin/$year/feedback/summary")

    private suspend inline fun <reified T> get(url: String): T =
        client.get(url) { header(HttpHeaders.Authorization, "Bearer $token") }.body()
}
