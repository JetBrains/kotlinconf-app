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
import kotlinx.serialization.json.Json
import org.jetbrains.kotlinconf.Conference

/**
 * Fetches admin data from [baseUrl] (the backend the SPA is served from, by default the page origin).
 * Paths are origin-absolute under [baseUrl] so they hit the API rather than the /admin shell.
 */
class AdminApi(private val baseUrl: String, private val token: String) {
    private val client = HttpClient(Js) {
        install(ContentNegotiation) {
            json(Json { ignoreUnknownKeys = true })
        }
    }

    suspend fun conference(year: Int): Conference = get("$baseUrl/$year/conference")

    suspend fun votes(year: Int): List<AdminVoteRow> = get("$baseUrl/$year/vote/all")

    suspend fun feedback(year: Int): List<AdminFeedbackRow> = get("$baseUrl/$year/feedback/summary")

    private suspend inline fun <reified T> get(url: String): T =
        client.get(url) { header(HttpHeaders.Authorization, "Bearer $token") }.body()
}
