package org.jetbrains.kotlinconf.backend.services

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.headers
import io.ktor.utils.io.core.Closeable
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import org.jetbrains.kotlinconf.SessionId
import org.jetbrains.kotlinconf.backend.model.GitHubItem
import org.jetbrains.kotlinconf.backend.utils.ConferenceConfig
import org.slf4j.LoggerFactory

class VideoUrlService(private val client: HttpClient, config: ConferenceConfig) : Closeable {
    private val log = LoggerFactory.getLogger("VideoUrlService")
    private val repo: String = config.dataRepo
    private val branch: String = config.dataBranch
    private val folder: String = config.videosFolder
    private val updateInterval = config.sessionizeInterval

    private val videoUrls = MutableSharedFlow<Map<SessionId, String>>(replay = 1)

    val syncJob = GlobalScope.launch(Dispatchers.IO) {
        while (true) {
            runCatching { updateVideoUrls() }.onFailure {
                log.error("Failed to update video URLs", it)
            }

            delay(updateInterval * 1000)
        }
    }

    suspend fun getVideoUrls(): Map<SessionId, String> = videoUrls.first()

    suspend fun updateVideoUrls() {
        videoUrls.emit(downloadVideoUrls())
    }

    private suspend fun downloadVideoUrls(): Map<SessionId, String> {
        val url = "https://api.github.com/repos/$repo/contents/$folder?ref=$branch"

        val contents: List<GitHubItem> = try {
            client.get(url) {
                headers {
                    append("Accept", "application/vnd.github.v3+json")
                }
            }.body()
        } catch (cause: Throwable) {
            log.warn("Error fetching repository contents: ${cause.message}")
            return emptyMap()
        }

        val urlsFile = contents
            .find { it.type == "file" && it.name == "urls.csv" }
            ?.download_url ?: run {
                log.warn("urls.csv file not found in $folder")
                return emptyMap()
            }

        return try {
            val content = client.get(urlsFile).body<String>()
            parseVideoUrls(content)
        } catch (cause: Throwable) {
            log.warn("Error downloading $urlsFile: ${cause.message}")
            emptyMap()
        }
    }

    internal fun parseVideoUrls(csvContent: String): Map<SessionId, String> {
        val result = mutableMapOf<SessionId, String>()

        csvContent.lineSequence()
            .filter { it.isNotBlank() }
            .forEach { line ->
                val parts = line.split(";", limit = 2)
                if (parts.size == 2) {
                    val sessionId = parts[0].trim()
                    val videoUrl = parts[1].trim()
                    if (sessionId.isNotEmpty() && videoUrl.isNotEmpty()) {
                        result[SessionId(sessionId)] = videoUrl
                    }
                }
            }

        return result
    }

    override fun close() {
        syncJob.cancel()
    }
}
