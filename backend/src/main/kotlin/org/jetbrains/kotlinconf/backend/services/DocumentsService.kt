package org.jetbrains.kotlinconf.backend.services

import org.jetbrains.kotlinconf.backend.utils.ConferenceConfig
import org.slf4j.LoggerFactory

class DocumentsService(
    private val config: ConferenceConfig,
) {
    private val log = LoggerFactory.getLogger("DocumentsService")

    private val allowedDocuments = setOf(
        "code-of-conduct",
        "visitors-privacy-notice",
        "visitors-terms",
    )

    private val cache = mutableMapOf<Pair<Int, String>, String>()

    internal fun validateDocuments() {
        for (year in config.supportedYears) {
            for (name in allowedDocuments) {
                val content = loadDocument(year, name)
                if (content == null) {
                    log.error("Missing document '$name' for year $year")
                } else if (content.isBlank()) {
                    log.error("Empty document '$name' for year $year")
                } else {
                    log.info("Document '$name' for year $year is valid (${content.length} chars)")
                }
            }
        }
    }

    fun getDocument(year: Int, name: String): String? {
        if (year !in config.supportedYears) return null
        if (name !in allowedDocuments) return null

        return cache.getOrPut(year to name) {
            loadDocument(year, name) ?: return null
        }
    }

    fun getAllDocuments(year: Int): Map<String, String>? {
        if (year !in config.supportedYears) return null

        return allowedDocuments.associateWith { name ->
            getDocument(year, name) ?: return@associateWith null
        }.filterValues { it != null }.mapValues { it.value!! }
    }

    private fun loadDocument(year: Int, name: String): String? {
        val resourcePath = "/years/$year/documents/$name.md"
        val inputStream = javaClass.getResourceAsStream(resourcePath)

        if (inputStream == null) {
            log.warn("Document not found for year $year at $resourcePath")
            return null
        }

        return try {
            inputStream.bufferedReader().use { it.readText() }
        } catch (e: Exception) {
            log.error("Failed to read document $name for year $year: ${e.message}", e)
            null
        }
    }
}
