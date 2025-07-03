package org.jetbrains.kotlinconf.backend.services

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.utils.io.core.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDateTime
import org.jetbrains.kotlinconf.NewsItem
import org.jetbrains.kotlinconf.backend.model.GitHubItem
import org.jetbrains.kotlinconf.backend.utils.ConferenceConfig
import org.slf4j.LoggerFactory

class NewsService(private val client: HttpClient, config: ConferenceConfig): Closeable {
    private val log = LoggerFactory.getLogger("NewsService")
    private val repo: String = config.newsRepo
    private val branch: String = config.newsBranch
    private val folder: String = config.newsFolder
    private val updateInterval = config.sessionizeInterval

    private val news = MutableSharedFlow<List<NewsItem>>(replay = 1)

    val syncJob = GlobalScope.launch(Dispatchers.IO) {
        while (true) {
            runCatching { updateNews() }.onFailure {
                log.error("Failed to update news", it)
            }

            kotlinx.coroutines.delay(updateInterval * 1000)
        }
    }

    suspend fun getNews(): List<NewsItem> = news.first()

    suspend fun updateNews() {
        news.emit(downloadNews())
    }

    private suspend fun downloadNews(): List<NewsItem> {
        val url = "https://api.github.com/repos/$repo/contents/$folder?ref=$branch"

        val contents: List<GitHubItem> = try {
            client.get(url) {
                headers {
                    append("Accept", "application/vnd.github.v3+json")
                }
            }.body()
        } catch (cause: Throwable) {
            log.warn("Error fetching repository contents: ${cause.message}")
            return emptyList()
        }

        val urls = contents
            .filter { it.type == "file" && it.name.endsWith(".md") }
            .mapNotNull { it.download_url }

        val result = mutableListOf<NewsItem>()
        for (url in urls) {
            try {
                val content = client.get(url).body<String>()
                val item = parseNewsItem(content)
                result.add(item)
            } catch (cause: Throwable) {
                log.warn("Error downloading $url: ${cause.message}")
            }
        }

        return result
    }

    /**
     * Parses a Markdown file and converts it to a NewsItem object.
     *
     * The Markdown file should have the following format:
     * ```
     *
     * ---
     * id: idstring
     * photoUrl: http://example.com/photo.jpg
     * title: title of the page
     * publicationDate: "2024-01-20T00:00:00"
     * ---
     * <content in markdown format>
     * ```
     * @return NewsItem object parsed from the markdown file
     * @throws IllegalArgumentException if the file format is invalid or required fields are missing
     */
    internal fun parseNewsItem(markdownContent: String): NewsItem {
        val metadataStart = markdownContent.indexOf("---")
        val metadataEnd = markdownContent.indexOf("---", startIndex = metadataStart + 3)
        val metadataSection = markdownContent.substring(metadataStart + 3, metadataEnd)
        val bodyContent = markdownContent.substring(metadataEnd + 3)

        val metadataLines = metadataSection.split("\n")
        val metadata = mutableMapOf<String, String>()

        for (line in metadataLines) {
            if (line.isBlank()) continue
            val keyValue = line.split(":", limit = 2)
            if (keyValue.size == 2) {
                val key = keyValue[0].trim()
                val value = keyValue[1].trim()
                metadata[key] = value
            }
        }

        // Extract required fields
        val id = metadata["id"] ?: throw IllegalArgumentException("Missing required field: id")
        val photoUrl = metadata["photoUrl"]?.takeIf { it.isNotEmpty() }
        val title = metadata["title"] ?: throw IllegalArgumentException("Missing required field: title")

        // Parse publication date
        val publicationDateStr = metadata["publicationDate"]
            ?: throw IllegalArgumentException("Missing required field: publicationDate")

        val publicationDate = LocalDateTime.parse(publicationDateStr)

        return NewsItem(
            id = id,
            photoUrl = photoUrl,
            title = title,
            publicationDate = publicationDate,
            content = bodyContent
        )
    }


    override fun close() {
        syncJob.cancel()
    }
}