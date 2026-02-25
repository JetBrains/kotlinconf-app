package org.jetbrains.kotlinconf.backend.services

import kotlinx.serialization.json.Json
import org.jetbrains.kotlinconf.ConferenceInfo
import org.jetbrains.kotlinconf.PartnerGroup
import org.jetbrains.kotlinconf.PartnerInfo
import org.jetbrains.kotlinconf.backend.utils.ConferenceConfig
import org.slf4j.LoggerFactory

class ConferenceInfoService(
    private val config: ConferenceConfig
) {
    private val log = LoggerFactory.getLogger("ConferenceInfoService")
    private val json = Json { ignoreUnknownKeys = true }

    private val infoCache = mutableMapOf<Int, ConferenceInfo>()
    private val logoCache = mutableMapOf<Pair<Int, String>, String>()
    private val mapSvgCache = mutableMapOf<Pair<Int, String>, String>()

    private val safeFilenamePattern = Regex("""^[A-Za-z0-9][A-Za-z0-9._-]*$""")

    fun getConferenceInfo(year: Int): ConferenceInfo? {
        if (year !in config.supportedYears) {
            return null
        }

        return infoCache.getOrPut(year) {
            loadConferenceInfo(year)?.withFullImageUrls(year) ?: return null
        }
    }

    private fun loadConferenceInfo(year: Int): ConferenceInfo? {
        val resourcePath = "/years/$year/conference-info.json"
        val inputStream = javaClass.getResourceAsStream(resourcePath)

        if (inputStream == null) {
            log.warn("Conference info not found for year $year at $resourcePath")
            return null
        }

        return try {
            val jsonString = inputStream.bufferedReader().use { it.readText() }
            json.decodeFromString<ConferenceInfo>(jsonString)
        } catch (e: Exception) {
            log.error("Failed to parse conference info for year $year: ${e.message}", e)
            null
        }
    }

    /**
     * Expands relative filenames from the JSON data into full URLs.
     */
    private fun ConferenceInfo.withFullImageUrls(year: Int): ConferenceInfo {
        val baseUrl = "${config.baseUrl}/$year"
        return copy(
            partners = partners.map { group ->
                PartnerGroup(
                    level = group.level,
                    partners = group.partners.map { partner ->
                        PartnerInfo(
                            id = partner.id,
                            name = partner.name,
                            description = partner.description,
                            logoUrlLight = "$baseUrl/${partner.logoUrlLight}",
                            logoUrlDark = "$baseUrl/${partner.logoUrlDark}",
                            url = partner.url,
                        )
                    }
                )
            },
        )
    }

    internal fun validateConferenceData() {
        for (year in config.supportedYears) {
            val info = loadConferenceInfo(year)
            if (info == null) {
                log.warn("No conference info found for year $year, skipping validation")
                continue
            }

            var issueCount = 0

            // Validate partner data
            for (group in info.partners) {
                for (partner in group.partners) {
                    if (partner.description.isBlank()) {
                        log.warn("$year | ${partner.name} | Missing description")
                        issueCount++
                    }
                    if (partner.url.isBlank()) {
                        log.warn("$year | ${partner.name} | Missing link")
                        issueCount++
                    }
                    for ((variant, logoPath) in listOf("light" to partner.logoUrlLight, "dark" to partner.logoUrlDark)) {
                        if (logoPath.isBlank()) {
                            log.warn("$year | ${partner.name} | Missing $variant logo path")
                            issueCount++
                            continue
                        }
                        val resourcePath = "/years/$year/$logoPath"
                        if (javaClass.getResourceAsStream(resourcePath) == null) {
                            log.warn("$year | ${partner.name} | Missing $variant logo file: $logoPath")
                            issueCount++
                        }
                    }
                }
            }

            // Validate map SVG files
            for (floor in info.mapData.floors) {
                for ((variant, svgPath) in listOf("light" to floor.svgPathLight, "dark" to floor.svgPathDark)) {
                    if (svgPath.isBlank()) {
                        log.warn("$year | ${floor.name} | Missing $variant map SVG path")
                        issueCount++
                        continue
                    }
                    val resourcePath = "/years/$year/$svgPath"
                    if (javaClass.getResourceAsStream(resourcePath) == null) {
                        log.warn("$year | ${floor.name} | Missing $variant map SVG file: $svgPath")
                        issueCount++
                    }
                }
            }

            if (issueCount == 0) {
                log.info("All conference data for year $year is valid")
            } else {
                log.warn("$issueCount conference data issue(s) for year $year")
            }
        }
    }

    fun getPartnerLogo(year: Int, filename: String): String? {
        if (year !in config.supportedYears) {
            return null
        }
        if (!safeFilenamePattern.matches(filename) || !filename.endsWith(".svg")) {
            log.warn("Invalid partner logo filename: $filename")
            return null
        }

        return logoCache.getOrPut(year to filename) {
            loadPartnerLogo(year, filename) ?: return null
        }
    }

    private fun loadPartnerLogo(year: Int, filename: String): String? {
        val resourcePath = "/years/$year/partner-logos/$filename"
        val inputStream = javaClass.getResourceAsStream(resourcePath)

        if (inputStream == null) {
            log.warn("Partner logo not found for year $year at $resourcePath")
            return null
        }

        return try {
            inputStream.bufferedReader().use { it.readText() }
        } catch (e: Exception) {
            log.error("Failed to read partner logo for year $year: ${e.message}", e)
            null
        }
    }

    fun getMapSvg(year: Int, filename: String): String? {
        if (year !in config.supportedYears) {
            return null
        }
        if (!safeFilenamePattern.matches(filename) || !filename.endsWith(".svg")) {
            log.warn("Invalid map SVG filename: $filename")
            return null
        }

        return mapSvgCache.getOrPut(year to filename) {
            loadMapSvg(year, filename) ?: return null
        }
    }

    private fun loadMapSvg(year: Int, filename: String): String? {
        val resourcePath = "/years/$year/maps/$filename"
        val inputStream = javaClass.getResourceAsStream(resourcePath)

        if (inputStream == null) {
            log.warn("Map SVG not found for year $year at $resourcePath")
            return null
        }

        return try {
            inputStream.bufferedReader().use { it.readText() }
        } catch (e: Exception) {
            log.error("Failed to read map SVG for year $year: ${e.message}", e)
            null
        }
    }
}
