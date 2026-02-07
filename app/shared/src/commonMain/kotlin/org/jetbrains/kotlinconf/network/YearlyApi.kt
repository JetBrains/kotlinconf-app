package org.jetbrains.kotlinconf.network

import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.SingleIn
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.contentType
import io.ktor.http.encodedPath
import io.ktor.http.isSuccess
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import org.jetbrains.kotlinconf.Conference
import org.jetbrains.kotlinconf.ConferenceInfo
import org.jetbrains.kotlinconf.FeedbackInfo
import org.jetbrains.kotlinconf.Score
import org.jetbrains.kotlinconf.SessionId
import org.jetbrains.kotlinconf.VoteInfo
import org.jetbrains.kotlinconf.Votes
import org.jetbrains.kotlinconf.di.YearScope
import org.jetbrains.kotlinconf.storage.YearlyStorage
import org.jetbrains.kotlinconf.utils.Logger
import org.jetbrains.kotlinconf.utils.tagged

@Inject
@SingleIn(YearScope::class)
class YearlyApi(
    private val year: Int,
    private val client: HttpClient,
    yearStorage: YearlyStorage,
    appScope: CoroutineScope,
    logger: Logger,
) {
    companion object Companion {
        private const val LOG_TAG = "YearlyAPIClient"
    }

    private val taggedLogger = logger.tagged(LOG_TAG)

    val userId = yearStorage.getUserId()
        .stateIn(appScope, SharingStarted.Eagerly, null)

    suspend fun sign(userId: String): Boolean {
        return safeApiCall {
            client.post {
                yearApiUrl("sign")
                setBody(userId)
            }.status.isSuccess()
        } ?: false
    }

    suspend fun downloadConferenceData(): Conference? {
        return safeApiCall {
            client.get {
                yearApiUrl("conference")
            }.body()
        }
    }

    suspend fun downloadConferenceInfo(): ConferenceInfo? {
        return safeApiCall {
            client.get {
                yearApiUrl("conference-info")
            }.body()
        }
    }

    suspend fun vote(sessionId: SessionId, score: Score?): Boolean {
        if (userId.value == null) return false

        return safeApiCall {
            client.post {
                yearApiUrl("vote")
                json()
                setBody(VoteInfo(sessionId, score))
            }.status.isSuccess()
        } ?: false
    }

    suspend fun sendFeedback(sessionId: SessionId, feedback: String): Boolean {
        if (userId.value == null) return false

        return safeApiCall {
            client.post {
                yearApiUrl("feedback")
                json()
                setBody(FeedbackInfo(sessionId, feedback))
            }.status.isSuccess()
        } ?: false
    }

    suspend fun myVotes(): List<VoteInfo> {
        if (userId.value == null) return emptyList()

        return safeApiCall {
            client.get { yearApiUrl("vote") }.body<Votes>().votes
        } ?: emptyList()
    }

    private suspend fun <T> safeApiCall(
        call: suspend () -> T,
    ): T? {
        return try {
            client
            call()
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            taggedLogger.log { "API call failed: ${e.message}" }
            null
        }
    }

    private fun HttpRequestBuilder.json() {
        contentType(ContentType.Application.Json)
    }

    private fun HttpRequestBuilder.yearApiUrl(path: String) {
        userId.value?.let {
            header(HttpHeaders.Authorization, "Bearer $it")
        }

        header(HttpHeaders.CacheControl, "no-cache")

        url {
            encodedPath = "$year/$path"
        }
    }
}
