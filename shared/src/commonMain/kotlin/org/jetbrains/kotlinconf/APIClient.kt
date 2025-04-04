package org.jetbrains.kotlinconf

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.DefaultRequest
import io.ktor.client.plugins.HttpRequestRetry
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.contentType
import io.ktor.http.encodedPath
import io.ktor.http.isSuccess
import io.ktor.http.takeFrom
import io.ktor.serialization.kotlinx.json.json
import io.ktor.utils.io.core.Closeable
import kotlinx.coroutines.CancellationException
import kotlinx.datetime.Instant
import org.jetbrains.kotlinconf.utils.Logger
import io.ktor.client.plugins.logging.Logger as KtorLogger

/**
 * Adapter to handle backend API and manage auth information.
 */
class APIClient(
    private val apiUrl: String,
    private val appLogger: Logger,
) : Closeable {

    companion object {
        private const val LOG_TAG = "APIClient"
    }

    var userId: String? = null

    private val client = HttpClient {
        install(ContentNegotiation) {
            json()
        }

        install(Logging) {
            level = LogLevel.HEADERS
            logger = object : KtorLogger {
                override fun log(message: String) {
                    appLogger.log(LOG_TAG) { message }
                }
            }
        }

        expectSuccess = true
        install(HttpTimeout) {
            requestTimeoutMillis = 5000
        }

        install(HttpRequestRetry) {
            retryOnServerErrors(maxRetries = 3)
            exponentialDelay()
        }

        install(DefaultRequest) {
            url.takeFrom(apiUrl)
        }
    }

    /**
     * @return status of request.
     */
    suspend fun sign(userId :String): Boolean {
        return safeApiCall {
            client.post {
                apiUrl("sign")
                setBody(userId)
            }.status.isSuccess()
        } ?: false
    }

    /**
     * Get [ConferenceData] info
     */
    suspend fun downloadConferenceData(): Conference? {
        return safeApiCall {
            client.get("conference").body()
        }
    }

    /**
     * Vote for session.
     */
    suspend fun vote(sessionId: SessionId, score: Score?): Boolean {
        if (userId == null) return false

        return safeApiCall {
            client.post {
                apiUrl("vote")
                json()
                setBody(VoteInfo(sessionId, score))
            }.status.isSuccess()
        } ?: false
    }

    /**
     * Send feedback
     */
    suspend fun sendFeedback(sessionId: SessionId, feedback: String): Boolean {
        if (userId == null) return false

        return safeApiCall {
            client.post {
                apiUrl("feedback")
                json()
                setBody(FeedbackInfo(sessionId, feedback))
            }.status.isSuccess()
        } ?: false
    }

    /**
     * List my votes.
     */
    suspend fun myVotes(): List<VoteInfo> {
        if (userId == null) return emptyList()

        return safeApiCall {
            client.get { apiUrl("vote") }.body<Votes>().votes
        } ?: emptyList()
    }

    suspend fun getServerTime(): Instant? {
        return safeApiCall {
            client.get { apiUrl("time") }.bodyAsText()
                .let { response -> Instant.fromEpochMilliseconds(response.toLong()) }
        }
    }

    suspend fun getNews(): List<NewsItem>? {
        return safeApiCall { client.get("news").body<NewsListResponse>().items }
    }

    /**
     * Runs the [call], returning its result or `null` if exceptions occurred.
     */
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

    private fun HttpRequestBuilder.json() {
        contentType(ContentType.Application.Json)
    }

    private fun HttpRequestBuilder.apiUrl(path: String) {
        if (userId != null) {
            header(HttpHeaders.Authorization, "Bearer $userId")
        }

        header(HttpHeaders.CacheControl, "no-cache")

        url {
            encodedPath = path
        }
    }

    override fun close() {
        client.close()
    }
}
