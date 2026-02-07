@file:OptIn(ExperimentalTime::class)

package org.jetbrains.kotlinconf.network

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpHeaders
import io.ktor.http.encodedPath
import kotlinx.coroutines.CancellationException
import org.jetbrains.kotlinconf.AppConfig
import org.jetbrains.kotlinconf.utils.Logger
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

/**
 * Adapter to handle app-level (year-independent) backend API calls.
 */
class ApplicationApi(
    private val client: HttpClient,
    private val appLogger: Logger,
) {
    companion object Companion {
        private const val LOG_TAG = "APIClient"
    }

    suspend fun getServerTime(): Instant? {
        return safeApiCall {
            client.get { apiUrl("time") }.bodyAsText()
                .let { response -> Instant.fromEpochMilliseconds(response.toLong()) }
        }
    }

    suspend fun getConfig(): AppConfig? = safeApiCall { client.get("config").body() }

    private suspend fun <T> safeApiCall(
        call: suspend () -> T,
    ): T? {
        return try {
            call()
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            appLogger.log(LOG_TAG) { "API call failed: ${e.message}" }
            null
        }
    }

    private fun HttpRequestBuilder.apiUrl(path: String) {
        header(HttpHeaders.CacheControl, "no-cache")

        url {
            encodedPath = path
        }
    }
}
