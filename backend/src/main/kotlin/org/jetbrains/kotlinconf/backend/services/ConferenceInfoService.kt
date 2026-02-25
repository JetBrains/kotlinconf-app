package org.jetbrains.kotlinconf.backend.services

import kotlinx.serialization.json.Json
import org.jetbrains.kotlinconf.ConferenceInfo
import org.jetbrains.kotlinconf.PartnerInfo
import org.jetbrains.kotlinconf.backend.utils.ConferenceConfig
import org.slf4j.LoggerFactory

class ConferenceInfoService(
    private val config: ConferenceConfig
) {
    private val log = LoggerFactory.getLogger("ConferenceInfoService")
    private val json = Json { ignoreUnknownKeys = true }

    private val infoCache = mutableMapOf<Int, ConferenceInfo>()

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

    private fun ConferenceInfo.withFullImageUrls(year: Int): ConferenceInfo {
        val baseUrl = "${config.baseUrl}/$year"
        return copy(
            partners = partners.map { group ->
                group.copy(
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

    internal fun validateConferenceInfo() {
        for (year in config.supportedYears) {
            val info = loadConferenceInfo(year)
            if (info == null) {
                log.warn("No conference info found for year $year, skipping validation")
                continue
            }

            val issueCount = validatePartnerData(info, year) +
                    validateMapAssets(info, year) +
                    validateMapData(info, year)

            if (issueCount == 0) {
                log.info("All conference data for year $year is valid")
            } else {
                log.warn("$issueCount conference data issue(s) for year $year")
            }
        }
    }

    private fun validatePartnerData(
        info: ConferenceInfo,
        year: Int,
    ): Int {
        var issueCount = 0
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
                for ((variant, logoPath) in listOf(
                    "light" to partner.logoUrlLight,
                    "dark" to partner.logoUrlDark
                )) {
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
        return issueCount
    }

    private fun validateMapAssets(
        info: ConferenceInfo,
        year: Int,
    ): Int {
        var issueCount = 0
        for (floor in info.mapData.floors) {
            for ((variant, svgPath) in listOf(
                "light" to floor.svgPathLight,
                "dark" to floor.svgPathDark
            )) {
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
        return issueCount
    }

    private fun validateMapData(
        info: ConferenceInfo,
        year: Int,
    ): Int {
        var issueCount = 0
        val floorCount = info.mapData.floors.size
        val mapData = info.mapData
        val floorRange = 0..<floorCount
        val offsetRange = 0f..1f
        if (mapData.defaultFloorIndex !in floorRange) {
            log.warn("$year | defaultFloorIndex ${mapData.defaultFloorIndex} is out of range [0, $floorCount)")
            issueCount++
        }
        if (mapData.defaultOffsetX !in offsetRange) {
            log.warn("$year | defaultOffsetX ${mapData.defaultOffsetX} is outside [0, 1]")
            issueCount++
        }
        if (mapData.defaultOffsetY !in offsetRange) {
            log.warn("$year | defaultOffsetY ${mapData.defaultOffsetY} is outside [0, 1]")
            issueCount++
        }
        for ((roomName, room) in mapData.rooms) {
            if (room.floorIndex !in floorRange) {
                log.warn("$year | Room '$roomName' | floorIndex ${room.floorIndex} is out of range [0, $floorCount)")
                issueCount++
            }
            if (room.offsetX !in offsetRange) {
                log.warn("$year | Room '$roomName' | offsetX ${room.offsetX} is outside [0, 1]")
                issueCount++
            }
            if (room.offsetY !in offsetRange) {
                log.warn("$year | Room '$roomName' | offsetY ${room.offsetY} is outside [0, 1]")
                issueCount++
            }
        }
        return issueCount
    }
}
