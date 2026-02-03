package org.jetbrains.kotlinconf.backend.services

import kotlinx.serialization.json.Json
import org.jetbrains.kotlinconf.Conference
import org.jetbrains.kotlinconf.backend.utils.ConferenceConfig
import org.slf4j.LoggerFactory

class ArchivedDataService(
    private val config: ConferenceConfig,
) {
    private val log = LoggerFactory.getLogger("ArchivedDataService")
    private val json = Json { ignoreUnknownKeys = true }

    private val cache = mutableMapOf<Int, Conference>()

    fun getConferenceData(year: Int): Conference? {
        if (year == config.currentYear) {
            // This should be served from live data, even if we already
            // have archived data for the current year as well
            return null
        }

        if (year !in config.supportedYears) {
            return null
        }

        return cache.getOrPut(year) {
            loadConferenceData(year) ?: return null
        }
    }

    private fun loadConferenceData(year: Int): Conference? {
        val resourcePath = "/archived/$year/conference.json"
        val inputStream = javaClass.getResourceAsStream(resourcePath)

        if (inputStream == null) {
            log.warn("Archived data not found for year $year at $resourcePath")
            return null
        }

        return try {
            val jsonString = inputStream.bufferedReader().use { it.readText() }
            json.decodeFromString<Conference>(jsonString)
        } catch (e: Exception) {
            log.error("Failed to parse archived data for year $year: ${e.message}", e)
            null
        }
    }
}
