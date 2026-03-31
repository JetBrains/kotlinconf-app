package org.jetbrains.kotlinconf.backend.services

import kotlinx.serialization.json.Json
import org.jetbrains.kotlinconf.GoldenKodeeData
import org.jetbrains.kotlinconf.backend.utils.ConferenceConfig
import org.slf4j.LoggerFactory
import java.util.concurrent.ConcurrentHashMap

class GoldenKodeeService(
    private val config: ConferenceConfig
) {
    private val log = LoggerFactory.getLogger("GoldenKodeeService")
    private val json = Json { ignoreUnknownKeys = true }

    private val cache = ConcurrentHashMap<Int, GoldenKodeeData>()

    fun getGoldenKodeeData(year: Int): GoldenKodeeData? {
        if (year !in config.supportedYears) {
            return null
        }

        return cache.getOrPut(year) {
            loadGoldenKodeeData(year) ?: return null
        }
    }

    private fun loadGoldenKodeeData(year: Int): GoldenKodeeData? {
        val resourcePath = "/years/$year/golden-kodee.json"
        val inputStream = javaClass.getResourceAsStream(resourcePath)

        if (inputStream == null) {
            log.warn("Golden Kodee data not found for year $year at $resourcePath")
            return null
        }

        return try {
            val jsonString = inputStream.bufferedReader().use { it.readText() }
            json.decodeFromString<GoldenKodeeData>(jsonString)
        } catch (e: Exception) {
            log.error("Failed to parse Golden Kodee data for year $year: ${e.message}", e)
            null
        }
    }

    internal fun validateGoldenKodeeData(): Int {
        var totalIssues = 0
        for (year in config.supportedYears) {
            val data = loadGoldenKodeeData(year)
            if (data == null) {
                log.warn("No Golden Kodee data found for year $year, skipping validation")
                continue
            }

            var issueCount = 0
            for (category in data.categories) {
                if (category.title.isBlank()) {
                    log.warn("$year | Golden Kodee category ${category.id} has blank title")
                    issueCount++
                }
                if (category.nominees.isEmpty()) {
                    log.warn("$year | Golden Kodee category '${category.title}' has no nominees")
                    issueCount++
                }
                for (nominee in category.nominees) {
                    if (nominee.name.isBlank()) {
                        log.warn("$year | Golden Kodee nominee ${nominee.id} in '${category.title}' has blank name")
                        issueCount++
                    }
                    if (nominee.photoUrl.isBlank()) {
                        log.warn("$year | Golden Kodee nominee '${nominee.name}' in '${category.title}' has blank photoUrl")
                        issueCount++
                    }
                }
            }

            if (issueCount == 0) {
                log.info("All Golden Kodee data for year $year is valid")
            } else {
                log.warn("$issueCount Golden Kodee data issue(s) for year $year")
            }
            totalIssues += issueCount
        }
        return totalIssues
    }
}
