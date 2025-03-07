package org.jetbrains.kotlinconf

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.logging.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.utils.io.core.*
import kotlinx.datetime.Instant
import org.jetbrains.kotlinconf.utils.Logger
import io.ktor.client.plugins.logging.Logger as KtorLogger

/**
 * Adapter to handle backend API and manage auth information.
 */
class APIClient(
    private val apiUrl: String,
    private val appLogger: Logger
) : Closeable {

    var userId: String? = null

    private val client = HttpClient {
        install(ContentNegotiation) {
            json()
        }

        install(Logging) {
            level = LogLevel.HEADERS
            logger = object : KtorLogger {
                override fun log(message: String) {
                    appLogger.log("HttpClient") { message }
                }
            }
        }

        HttpResponseValidator {
            validateResponse {
                when (it.status) {
                    COMEBACK_LATER_STATUS -> throw TooEarlyVote()
                    TOO_LATE_STATUS -> throw TooLateVote()
                    HttpStatusCode.Conflict -> return@validateResponse
                    HttpStatusCode.Unauthorized -> throw Unauthorized()
                }
            }
        }

        install(HttpRequestRetry) {
            maxRetries = Int.MAX_VALUE
            delay {
                kotlinx.coroutines.delay(it)
            }
            constantDelay(10 * 1000L)
            retryOnException(retryOnTimeout = true)
        }

        install(DefaultRequest) {
            url.takeFrom(apiUrl)
        }
    }

    /**
     * @return status of request.
     */
    suspend fun sign(): Boolean {
        val userId = userId ?: return false

        val response = client.post {
            apiUrl("sign")
            setBody(userId)
        }

        return response.status.isSuccess()
    }

    /**
     * Get [ConferenceData] info
     */
    suspend fun downloadConferenceData(): Conference = client.get {
        url.path("conference")
    }.body()

    /**
     * Vote for session.
     */
    suspend fun vote(sessionId: SessionId, score: Score?): Boolean {
        if (userId == null) return false

        client.post {
            apiUrl("vote")
            json()
            setBody(VoteInfo(sessionId, score))
        }

        return true
    }

    /**
     * Send feedback
     */
    suspend fun sendFeedback(sessionId: SessionId, feedback: String): Boolean {
        if (userId == null) return false

        client.post {
            apiUrl("feedback")
            json()
            setBody(FeedbackInfo(sessionId, feedback))
        }

        return true
    }

    /**
     * List my votes.
     */
    suspend fun myVotes(): List<VoteInfo> {
        if (userId == null) return emptyList()

        return client.get {
            apiUrl("vote")
        }.body<Votes>().votes
    }

    suspend fun getServerTime(): Instant = client.get {
        apiUrl("time")
    }.bodyAsText().let { response -> Instant.fromEpochMilliseconds(response.toLong()) }

    suspend fun getNews(): List<NewsItem> = client.get("news").body<NewsListResponse>().items

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
