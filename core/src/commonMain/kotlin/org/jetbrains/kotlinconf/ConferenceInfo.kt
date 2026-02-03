package org.jetbrains.kotlinconf

import kotlinx.serialization.Serializable

@Serializable
class ConferenceInfo(
    val partners: List<PartnerGroup> = emptyList(),
    val days: List<DayInfo> = emptyList(),
    val aboutBlocks: List<AboutBlockInfo> = emptyList(),
    val tags: TagInfo = TagInfo(),
)

@Serializable
class PartnerGroup(
    val level: String,
    val partners: List<PartnerInfo>,
)

@Serializable
class PartnerInfo(
    val id: PartnerId,
    val name: String,
    val description: String,
    val logoUrlLight: String,
    val logoUrlDark: String,
    val url: String,
)

@Serializable
class DayInfo(
    val date: String,
    val line1: String,
    val line2: String,
)

@Serializable
class AboutBlockInfo(
    val sessionId: String?,
    val month: String,
    val day: String,
    val title1: String,
    val title2: String,
    val description: String?,
)

@Serializable
class TagInfo(
    val categories: List<String> = emptyList(),
    val levels: List<String> = emptyList(),
    val formats: List<String> = emptyList(),
)
