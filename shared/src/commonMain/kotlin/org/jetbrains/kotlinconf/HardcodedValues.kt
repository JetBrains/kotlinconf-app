package org.jetbrains.kotlinconf

import kotlinconfapp.shared.generated.resources.Res
import kotlinconfapp.shared.generated.resources.partner_american_express
import kotlinconfapp.shared.generated.resources.partner_american_express_dark
import kotlinconfapp.shared.generated.resources.partner_gitar
import kotlinconfapp.shared.generated.resources.partner_gitar_dark
import kotlinconfapp.shared.generated.resources.partner_google
import kotlinconfapp.shared.generated.resources.partner_google_dark
import kotlinconfapp.shared.generated.resources.partner_gradle
import kotlinconfapp.shared.generated.resources.partner_gradle_dark
import kotlinconfapp.shared.generated.resources.partner_jvm_weekly
import kotlinconfapp.shared.generated.resources.partner_jvm_weekly_dark
import kotlinconfapp.shared.generated.resources.partner_kodeinkoders
import kotlinconfapp.shared.generated.resources.partner_kodeinkoders_dark
import kotlinconfapp.shared.generated.resources.partner_kotlin_foundation
import kotlinconfapp.shared.generated.resources.partner_kotlin_foundation_dark
import kotlinconfapp.shared.generated.resources.partner_revenuecat
import kotlinconfapp.shared.generated.resources.partner_revenuecat_dark
import kotlinconfapp.shared.generated.resources.partner_sentry
import kotlinconfapp.shared.generated.resources.partner_sentry_dark
import kotlinconfapp.shared.generated.resources.partner_typealias
import kotlinconfapp.shared.generated.resources.partner_typealias_dark
import kotlinconfapp.shared.generated.resources.partner_worldline
import kotlinconfapp.shared.generated.resources.partner_worldline_dark
import kotlinconfapp.shared.generated.resources.partners_bronze
import kotlinconfapp.shared.generated.resources.partners_codelab
import kotlinconfapp.shared.generated.resources.partners_digital
import kotlinconfapp.shared.generated.resources.partners_gold
import kotlinconfapp.shared.generated.resources.partners_media
import kotlinconfapp.shared.generated.resources.partners_platinum
import kotlinconfapp.shared.generated.resources.partners_silver
import kotlinconfapp.shared.generated.resources.partners_swag
import kotlinconfapp.shared.generated.resources.partners_video
import kotlinx.datetime.LocalDate
import org.jetbrains.compose.resources.StringResource

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
        val map = mapOf<LocalDate, DayValues>(
            LocalDate(2025, 5, 21) to DayValues("Workshop", "Day"),
            LocalDate(2025, 5, 22) to DayValues("Conference", "Day 1"),
            LocalDate(2025, 5, 23) to DayValues("Conference", "Day 2"),
        )
    }
}

internal val PARTNERS: Map<StringResource, List<Partner>> = mapOf(
    Res.string.partners_platinum to listOf(
        Partner(PartnerId("revenuecat"), "RevenueCat", "", Res.drawable.partner_revenuecat, Res.drawable.partner_revenuecat_dark),
    ),
    Res.string.partners_gold to listOf(
        Partner(PartnerId("google"), "Google", "", Res.drawable.partner_google, Res.drawable.partner_google_dark),
    ),
    Res.string.partners_silver to listOf(
        Partner(PartnerId("gradle"), "Gradle", "", Res.drawable.partner_gradle, Res.drawable.partner_gradle_dark),
    ),
    Res.string.partners_bronze to listOf(
        Partner(PartnerId("kotlinfoundation"), "Kotlin Foundation", "", Res.drawable.partner_kotlin_foundation, Res.drawable.partner_kotlin_foundation_dark),
        Partner(PartnerId("gitar"), "Gitar", "", Res.drawable.partner_gitar, Res.drawable.partner_gitar_dark),
        Partner(PartnerId("sentry"), "Sentry", "", Res.drawable.partner_sentry, Res.drawable.partner_sentry_dark),
    ),
    Res.string.partners_video to listOf(
        Partner(PartnerId("amex"), "American Express", "", Res.drawable.partner_american_express, Res.drawable.partner_american_express_dark),
    ),
    Res.string.partners_codelab to listOf(
        Partner(PartnerId("google"), "Google", "", Res.drawable.partner_google, Res.drawable.partner_google_dark),
    ),
    Res.string.partners_digital to listOf(
        Partner(PartnerId("kodein"), "Kodein Koders", "", Res.drawable.partner_kodeinkoders, Res.drawable.partner_kodeinkoders_dark),
        Partner(PartnerId("typealias"), "Typealias Studios", "", Res.drawable.partner_typealias, Res.drawable.partner_typealias_dark),
        Partner(PartnerId("worldline"), "World Line", "", Res.drawable.partner_worldline, Res.drawable.partner_worldline_dark),
    ),
    Res.string.partners_media to listOf(
        Partner(PartnerId("jvmweekly"), "JVM Weekly", "", Res.drawable.partner_jvm_weekly, Res.drawable.partner_jvm_weekly_dark),
    ),
    Res.string.partners_swag to listOf(
        Partner(PartnerId("revenuecat"), "RevenueCat", "", Res.drawable.partner_revenuecat, Res.drawable.partner_revenuecat_dark),
    ),
)
