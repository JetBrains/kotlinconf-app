package org.jetbrains.kotlinconf

import kotlinx.serialization.Serializable
import kotlin.jvm.JvmInline

@Serializable
@JvmInline
value class AwardCategoryId(val id: String)

@Serializable
@JvmInline
value class NomineeId(val id: String)

@Serializable
data class Nominee(
    val id: NomineeId,
    val name: String,
    val photoUrl: String,
    val position: String = "",
    val bio: String = "",
    val projectName: String = "",
    val projectDescription: String = "",
    val winner: Boolean = false,
)

@Serializable
data class AwardCategory(
    val id: AwardCategoryId,
    val title: String,
    val description: String,
    val nominees: List<Nominee>,
)

@Serializable
data class GoldenKodeeData(
    val categories: List<AwardCategory>,
)
