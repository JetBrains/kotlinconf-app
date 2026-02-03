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
    private val logoCache = mutableMapOf<Pair<Int, String>, ByteArray>()

    fun getConferenceInfo(year: Int): ConferenceInfo? {
        if (year !in config.supportedYears) {
            return null
        }

        return infoCache.getOrPut(year) {
            loadConferenceInfo(year)?.withFullLogoUrls(year) ?: return null
        }
    }

    private fun loadConferenceInfo(year: Int): ConferenceInfo? {
        val resourcePath = "/archived/$year/conference-info.json"
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
     * Expands the logo filenames from the JSON data into full URLs.
     */
    private fun ConferenceInfo.withFullLogoUrls(year: Int): ConferenceInfo {
        val baseUrl = "${config.baseUrl}/$year"
        return ConferenceInfo(
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
            days = days,
            aboutBlocks = aboutBlocks,
            tags = tags,
        )
    }

    fun getPartnerLogo(year: Int, filename: String): ByteArray? {
        if (year !in config.supportedYears) {
            return null
        }

        return logoCache.getOrPut(year to filename) {
            loadPartnerLogo(year, filename) ?: return null
        }
    }

    private fun loadPartnerLogo(year: Int, filename: String): ByteArray? {
        val resourcePath = "/archived/$year/partner-logos/$filename"
        val inputStream = javaClass.getResourceAsStream(resourcePath)

        if (inputStream == null) {
            log.warn("Partner logo not found for year $year at $resourcePath")
            return null
        }

        return try {
            inputStream.readBytes()
        } catch (e: Exception) {
            log.error("Failed to read partner logo for year $year: ${e.message}", e)
            null
        }
    }
}
