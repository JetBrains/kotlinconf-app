package org.jetbrains.kotlinconf.backend.services

import kotlinx.serialization.json.Json
import org.jetbrains.kotlinconf.Conference
import org.jetbrains.kotlinconf.backend.utils.ConferenceConfig
import org.slf4j.LoggerFactory
import java.util.concurrent.ConcurrentHashMap

class ArchivedDataService(
    private val config: ConferenceConfig,
) {
    private val log = LoggerFactory.getLogger("ArchivedDataService")

    private val cache = ConcurrentHashMap<Int, ByteArray>()

    internal fun validateArchives() {
        val json = Json { ignoreUnknownKeys = true }
        for (year in config.supportedYears) {
            if (year == config.currentYear) {
                continue
            }

            val data = getConferenceData(year)
            if (data == null) {
                log.warn("No archived data found for year $year")
                continue
            }
            try {
                json.decodeFromString<Conference>(data.decodeToString())
                log.info("Archived data for year $year is valid")
            } catch (e: Exception) {
                log.error("Archived data for year $year failed to decode: ${e.message}", e)
            }
        }
    }

    fun getConferenceData(year: Int): ByteArray? {
        if (year !in config.supportedYears) {
            return null
        }

        return cache.getOrPut(year) {
            loadConferenceData(year) ?: return null
        }
    }

    private fun loadConferenceData(year: Int): ByteArray? {
        val resourcePath = "/years/$year/conference.json"
        val inputStream = javaClass.getResourceAsStream(resourcePath)

        if (inputStream == null) {
            log.warn("Archived data not found for year $year at $resourcePath")
            return null
        }

        return try {
            inputStream.use { it.readBytes() }
        } catch (e: Exception) {
            log.error("Failed to read archived data for year $year: ${e.message}", e)
            null
        }
    }
}
