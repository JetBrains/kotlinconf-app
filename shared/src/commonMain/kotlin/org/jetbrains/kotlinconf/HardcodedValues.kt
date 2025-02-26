package org.jetbrains.kotlinconf

import kotlinx.datetime.LocalDate

object TagValues {
    val categories = listOf(
        "Server-side",
        "Multiplatform",
        "Android",
        "Extensibility/Tooling",
        "Languages and best practices",
        "Other",
    )
    val levels = listOf(
        "Introductory and overview",
        "Intermediate",
        "Advanced",
    )
    val formats = listOf(
        "Workshop",
        "Regular session",
        "Lightning talk",
    )
}

data class DayValues(
    val line1: String,
    val line2: String,
) {
    companion object {
        // TODO update for 2025 data
        val map = mapOf<LocalDate, DayValues>(
            LocalDate(2024, 5, 22) to DayValues("Workshop", "Day"),
            LocalDate(2024, 5, 23) to DayValues("Conference", "Day 1"),
            LocalDate(2024, 5, 24) to DayValues("Conference", "Day 2"),
        )
    }
}
