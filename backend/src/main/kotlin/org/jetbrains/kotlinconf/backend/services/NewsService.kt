package org.jetbrains.kotlinconf.backend.services

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import java.io.File

class NewsService {
    private val client = HttpClient(CIO) {
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
                prettyPrint = true
            })
        }
    }

    @Serializable
    data class GitHubItem(
        val name: String,
        val path: String,
        val type: String,
        val download_url: String? = null
    )

    /**
     * Downloads all files from a specific folder in a public GitHub repository branch
     *
     * @param org The organization or username that owns the repository
     * @param repo The repository name
     * @param branch The branch name (e.g., "main", "master")
     * @param folderPath The path to the folder inside the repository
     * @param outputDir The local directory where files should be saved
     * @param fileFilter Optional predicate to filter which files to download
     */
    suspend fun downloadFilesFromGitHub(
        org: String,
        repo: String,
        branch: String,
        folderPath: String,
        outputDir: String,
        fileFilter: (GitHubItem) -> Boolean = { true }
    ) {
        // Create output directory if it doesn't exist
        withContext(Dispatchers.IO) {
            File(outputDir).mkdirs()
        }

        // Normalize folder path to remove leading/trailing slashes
        val normalizedPath = folderPath.trim('/')

        // Get contents of the specified folder in the specified branch
        val url = "https://api.github.com/repos/$org/$repo/contents/$normalizedPath?ref=$branch"

        val contents: List<GitHubItem> = try {
            client.get(url) {
                headers {
                    append("Accept", "application/vnd.github.v3+json")
                }
            }.body()
        } catch (e: Exception) {
            println("Error fetching repository contents: ${e.message}")
            emptyList()
        }

        // Print the files found
        println("Files found in $org/$repo/$branch/$normalizedPath:")
        contents.forEach {
            println("- ${it.name} (${it.type})")
        }

        // Download each file that passes the filter
        var downloadCount = 0
        contents.forEach { item ->
            when (item.type) {
                "dir" -> {
                    // If it's a directory, we could recursively download, but we'll skip for now
                    println("Skipping directory: ${item.path}")
                }
                "file" -> {
                    // Check if we should download this file
                    if (fileFilter(item)) {
                        item.download_url?.let { url ->
                            try {
                                val content = client.get(url).body<String>()
                                withContext(Dispatchers.IO) {
                                    File("$outputDir/${item.name}").writeText(content)
                                }
                                println("Downloaded: ${item.path}")
                                downloadCount++
                            } catch (e: Exception) {
                                println("Error downloading ${item.path}: ${e.message}")
                            }
                        }
                    }
                }
            }
        }

        println("Downloaded $downloadCount files to $outputDir")
    }

    suspend fun stop() {
        client.close()
    }
}