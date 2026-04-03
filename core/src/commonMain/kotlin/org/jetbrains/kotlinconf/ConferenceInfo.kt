package org.jetbrains.kotlinconf

import kotlinx.datetime.LocalDate
import kotlinx.serialization.Serializable

@Serializable
data class ConferenceInfo(
    val aboutHeader: String,
    val aboutDescription: String,
    val images: ConferenceImages? = null,
    val partners: List<PartnerGroup>,
    val days: List<DayInfo>,
    val aboutBlocks: List<AboutBlockInfo>,
    val tags: TagInfo,
    val mapData: MapData,
)

@Serializable
data class ConferenceImages(
    val kotlinConfLight: String,
    val kotlinConfDark: String,
)

@Serializable
data class MapData(
    val venueAddress: String?,
    val floors: List<FloorData>,
    val rooms: Map<String, RoomData>,
    val defaultFloorIndex: Int,
    val defaultOffsetX: Float,
    val defaultOffsetY: Float,
    val initialZoom: Float,
    val minZoom: Float,
    val maxZoom: Float,
)

@Serializable
data class FloorData(
    val name: String,
    val shortName: String = name,
    val svgPathLight: String,
    val svgPathDark: String,
)

@Serializable
data class RoomData(
    val floorIndex: Int,
    val offsetX: Float,
    val offsetY: Float,
)

@Serializable
data class PartnerGroup(
    val level: String,
    val partners: List<PartnerInfo>,
)

@Serializable
data class PartnerInfo(
    val id: PartnerId,
    val name: String,
    val description: String,
    val logoUrlLight: String,
    val logoUrlDark: String,
    val url: String,
)

@Serializable
data class DayInfo(
    val date: LocalDate,
    val line1: String,
    val line2: String,
    val combinedLine: String = "$line1 $line2",
)

@Serializable
data class AboutBlockInfo(
    val sessionId: SessionId?,
    val month: String,
    val day: String,
    val title1: String,
    val title2: String,
    val description: String?,
)

@Serializable
data class TagInfo(
    val categories: List<String>,
    val levels: List<String>,
    val formats: List<String>,
)
