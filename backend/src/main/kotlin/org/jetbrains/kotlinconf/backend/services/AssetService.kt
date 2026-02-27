package org.jetbrains.kotlinconf.backend.services

import org.jetbrains.kotlinconf.backend.utils.ConferenceConfig
import org.slf4j.LoggerFactory
import java.util.concurrent.ConcurrentHashMap

private val safeFilenamePattern = Regex("""^[A-Za-z0-9][A-Za-z0-9._-]*$""")
private val validExtensions = setOf("md", "svg")

class AssetService(
    private val config: ConferenceConfig
) {
    enum class AssetType(val folder: String) {
        Map("maps"),
        Partner("partner-logos"),
        Document("documents"),
    }

    private val log = LoggerFactory.getLogger("AssetService")

    private val assetCache = ConcurrentHashMap<Pair<Int, String>, String>()

    fun getAsset(year: Int, assetType: AssetType, filename: String): String? {
        if (year !in config.supportedYears) {
            return null
        }
        if (!safeFilenamePattern.matches(filename)) {
            log.warn("Invalid asset filename: $filename")
            return null
        }
        val extension = filename.substringAfterLast('.', missingDelimiterValue = "")
        if (extension !in validExtensions) {
            log.warn("Invalid asset extension: '$extension'")
            return null
        }

        return assetCache.getOrPut(year to filename) {
            loadAsset(year, assetType, filename) ?: return null
        }
    }

    private fun loadAsset(year: Int, assetType: AssetType, filename: String): String? {
        val resourcePath = "/years/$year/${assetType.folder}/$filename"
        val inputStream = javaClass.getResourceAsStream(resourcePath)

        if (inputStream == null) {
            log.warn("Asset not found at path $resourcePath")
            return null
        }

        return try {
            inputStream.bufferedReader().use { it.readText() }
        } catch (e: Exception) {
            log.error("Failed to read asset for year $year: ${e.message}", e)
            null
        }
    }

    internal fun validateDocuments() {
        val requiredDocuments = setOf(
            "app-privacy-notice.md",
            "app-terms.md",
            "code-of-conduct.md",
            "visitors-privacy-notice.md",
            "visitors-terms.md",
        )

        for (year in config.supportedYears) {
            for (name in requiredDocuments) {
                val content = loadAsset(year, AssetType.Document, name)
                when {
                    content == null -> log.error("Missing document '$name' for year $year")
                    content.isBlank() -> log.error("Empty document '$name' for year $year")
                    else -> log.info("Document '$name' for year $year is valid (${content.length} chars)")
                }
            }
        }
    }
}
