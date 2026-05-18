package org.jetbrains.kotlinconf.network

import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.SingleIn
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
import org.jetbrains.kotlinconf.utils.tagged
import kotlin.time.Instant

/**
 * Adapter to handle app-level (year-independent) backend API calls.
 */
@Inject
@SingleIn(AppScope::class)
class ApplicationApi(
    private val client: HttpClient,
    logger: Logger,
) {
    companion object {
        private const val LOG_TAG = "ApplicationApi"
    }

    private val taggedLogger = logger.tagged(LOG_TAG)

    suspend fun getServerTime(): Instant? = safeApiCall {
        client.get { apiUrl("time") }.bodyAsText()
            .let { response -> Instant.fromEpochMilliseconds(response.toLong()) }
    }

    suspend fun getConfig(): AppConfig? = safeApiCall {
        client.get { apiUrl("config") }.body()
    }

    private suspend fun <T> safeApiCall(
        call: suspend () -> T,
    ): T? {
        return try {
            call()
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            taggedLogger.log { "API call failed: ${e.message}" }
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
