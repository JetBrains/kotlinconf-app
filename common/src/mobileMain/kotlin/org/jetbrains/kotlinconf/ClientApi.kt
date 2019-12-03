package org.jetbrains.kotlinconf

import io.ktor.client.*
import io.ktor.client.features.*
import io.ktor.client.features.json.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.util.date.*
import kotlin.native.concurrent.*

/**
 * Adapter to handle backend API and manage auth information.
 */
@ThreadLocal
internal object ClientApi {
    val endpoint = "https://api.kotlinconf.com"

    private val client = HttpClient {
        install(JsonFeature)

        HttpResponseValidator {
            validateResponse {
                when (it.status) {
                    COMEBACK_LATER_STATUS -> throw TooEarlyVote()
                    TOO_LATE_STATUS -> throw TooLateVote()
                    HttpStatusCode.Unauthorized -> throw Unauthorized()
                }

                if (!it.status.isSuccess()) {
                    when (it.call.request.url.encodedPath) {
                        "/favorites" -> throw CannotFavorite()
                        else -> error("Bad status: ${it.status}")
                    }
                }
            }
        }
    }

    /**
     * @return status of request.
     */
    suspend fun sign(userId: String): Boolean {
        return client.post<HttpResponse> {
            apiUrl("users")
            body = userId
        }.status.isSuccess()
    }

    /**
     * Get [ConferenceData] info.
     * Load favorites and votes info if [userId] provided.
     */
    suspend fun getAll(userId: String?): ConferenceData = client.get {
        apiUrl("all2019", userId)
    }

    /**
     * Update favorite information.
     */
    suspend fun postFavorite(userId: String, sessionId: String): Unit = client.post {
        apiUrl("favorites", userId)
        body = sessionId
    }

    /**
     * Remove item from favorites list.
     */
    suspend fun deleteFavorite(userId: String, sessionId: String): Unit = client.delete {
        apiUrl("favorites", userId)
        body = sessionId
    }

    /**
     * Vote for session.
     */
    suspend fun postVote(userId: String, vote: VoteData): Unit = client.post {
        apiUrl("votes", userId)
        json()
        body = vote
    }

    /**
     * Remove vote.
     */
    suspend fun deleteVote(userId: String, sessionId: String): Unit = client.delete {
        apiUrl("votes", userId)
        json()
        body = VoteData(sessionId)
    }

    /**
     * Get news feed.
     */
    suspend fun getFeed(): FeedData = client.get {
        apiUrl("feed")
    }

    /**
     * Get server time.
     */
    suspend fun getServerTime(): GMTDate = client.get<String> {
        apiUrl("time")
    }.let { response -> GMTDate(response.toLong()) }

    private fun HttpRequestBuilder.json() {
        contentType(ContentType.Application.Json)
    }

    private fun HttpRequestBuilder.apiUrl(path: String, userId: String? = null) {
        if (userId != null) {
            header(HttpHeaders.Authorization, "Bearer $userId")
        }
        header(HttpHeaders.CacheControl, "no-cache")
        url {
            takeFrom(endpoint)
            encodedPath = path
        }
    }

}
