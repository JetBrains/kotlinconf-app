package org.jetbrains.kotlinconf

import kotlinconfapp.shared.generated.resources.Res
import kotlinconfapp.shared.generated.resources.partner_american_express
import kotlinconfapp.shared.generated.resources.partner_android_weekly
import kotlinconfapp.shared.generated.resources.partner_bloomberg
import kotlinconfapp.shared.generated.resources.partner_gitar
import kotlinconfapp.shared.generated.resources.partner_google
import kotlinconfapp.shared.generated.resources.partner_gradle
import kotlinconfapp.shared.generated.resources.partner_jvm_weekly
import kotlinconfapp.shared.generated.resources.partner_kodeinkoders
import kotlinconfapp.shared.generated.resources.partner_kotlin_foundation
import kotlinconfapp.shared.generated.resources.partner_kotlin_weekly
import kotlinconfapp.shared.generated.resources.partner_kotzilla
import kotlinconfapp.shared.generated.resources.partner_revenuecat
import kotlinconfapp.shared.generated.resources.partner_sentry
import kotlinconfapp.shared.generated.resources.partner_typealias
import kotlinconfapp.shared.generated.resources.partner_uber
import kotlinconfapp.shared.generated.resources.partner_worldline
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
        "Language and best practices",
        "Education",
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
        "Codelab",
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
        Partner(PartnerId("revenuecat"), "RevenueCat", "RevenueCat makes it easy to build analyze, and grow in-app subscriptions. Trusted by over 38k apps, developers can manage cross-platform payments, track customer data, and access powerful analytics to maximize their app's revenue.", Res.drawable.partner_revenuecat, "https://www.rev.cat"),
    ),
    Res.string.partners_gold to listOf(
        Partner(PartnerId("google"), "Google", "Google's mission is to organize the world's information and make it universally accessible and useful.", Res.drawable.partner_google, "https://about.google/"),
    ),
    Res.string.partners_silver to listOf(
        Partner(PartnerId("gradle"), "Gradle", "Develocity is the leading software solution for improving developer productivity & the developer experience. It does this by leveraging advanced build and test performance acceleration technologies.", Res.drawable.partner_gradle, "http://www.gradle.com/"),
        Partner(PartnerId("kotlinfoundation"), "Kotlin Foundation", "The Kotlin Foundation is a non-profit company whose mission is to promote and advance the Kotlin ecosystem.", Res.drawable.partner_kotlin_foundation, "https://kotlinfoundation.org/"),
        Partner(PartnerId("kotzilla"), "Kotzilla", "Creators of Koin, the popular Kotlin dependency injection framework, Kotzilla provides debugging tools for Kotlin devs to resolve complex app issues, empowering them to develop safer, faster apps.", Res.drawable.partner_kotzilla, "https://kotzilla.io/"),
        Partner(PartnerId("uber"), "Uber", "We reimagine the way the world moves for the better Movement is what we power.", Res.drawable.partner_uber, "https://www.uber.com/nl/en/"),
    ),
    Res.string.partners_bronze to listOf(
        Partner(PartnerId("bloomberg"), "Bloomberg", "Bloomberg’s 9,000+ engineers solve complex, real-world problems. Kotlin is popular among the firm’s Java and the JVM community, and increasingly, it is used to write entire application stacks.", Res.drawable.partner_bloomberg, "https://www.bloomberg.com/company/values/tech-at-bloomberg/"),
        Partner(PartnerId("gitar"), "Gitar", "Gitar simplifies code maintenance for developers with automated solutions for dead code cleanup, feature flag removal, and seamless app migrations. Boost efficiency and code quality effortlessly!", Res.drawable.partner_gitar, "www.gitar.ai"),
        Partner(PartnerId("sentry"), "Sentry", "For software teams, Sentry is essential for monitoring application code quality. From Error tracking to Performance monitoring, developers can see clearer, solve quicker, and learn continuously about their applications — from frontend to backend. Loved by nearly 4 million developers and 90,000 organizations worldwide, Sentry provides code-level observability to many of the world's best-known companies like Disney, Cloudflare, Eventbrite, Slack, Supercell, and Rockstar Games.", Res.drawable.partner_sentry, "https://sentry.io/welcome"),
    ),
    Res.string.partners_video to listOf(
        Partner(PartnerId("amex"), "American Express", "American Express is a globally integrated payments company, providing customers with access to products, insights and experiences that enrich lives and build business success. Find your place in Tech at Amex.", Res.drawable.partner_american_express, "americanexpress.com/techcareers"),
    ),
    Res.string.partners_codelab to listOf(
        Partner(PartnerId("google"), "Google", "Google's mission is to organize the world's information and make it universally accessible and useful.", Res.drawable.partner_google, "https://about.google/"),
    ),
    Res.string.partners_digital to listOf(
        Partner(PartnerId("kodein"), "Kodein Koders", "Level-up your Kotlin expertise with Kodein Koders: guiding developers to master Kotlin Multiplatform through hands-on workshops to tackle real-world Android, iOS, Desktop, and Web challenges.", Res.drawable.partner_kodeinkoders, "https://kodein.net"),
        Partner(PartnerId("typealias"), "Typealias Studios", "TypeAlias Studios makes it easy to level up your Kotlin and Android skills with illustrated articles, videos, livestreams, and courses—equipping developers at every level, from beginner to pro.", Res.drawable.partner_typealias, "https://typealias.com"),
        Partner(PartnerId("worldline"), "World Line", "Worldline is a global payments player, employing over 7000 tech engineers. Our technology powers the growth of millions of businesses around the world; from your local coffee shop, to global e-commerce players and international banks.", Res.drawable.partner_worldline, "https://worldline.com/"),
    ),
    Res.string.partners_media to listOf(
        Partner(PartnerId("androidweekly"), "Android Weekly", "Android Weekly is a free newsletter that helps you to stay cutting-edge with your Android Development.", Res.drawable.partner_android_weekly, "https://androidweekly.net"),
        Partner(PartnerId("jvmweekly"), "JVM Weekly", "From the latest updates in JVM languages like Java, Kotlin, and Scala to emerging technologies like GraalVM and Quarkus, this newsletter covers a wide range of topics that are of interest to developers and tech enthusiasts.", Res.drawable.partner_jvm_weekly, "https://www.jvm-weekly.com/"),
        Partner(PartnerId("kotlinweekly"), "Kotlin Weekly", "Kotlin Weekly is your weekly source of Kotlin news, libraries, and events going on the Kotlin world.", Res.drawable.partner_kotlin_weekly, "https://kotlinweekly.net/"),
    ),
    Res.string.partners_swag to listOf(
        Partner(PartnerId("revenuecat"), "RevenueCat", "RevenueCat makes it easy to build analyze, and grow in-app subscriptions. Trusted by over 38k apps, developers can manage cross-platform payments, track customer data, and access powerful analytics to maximize their app's revenue.", Res.drawable.partner_revenuecat, "https://www.rev.cat"),
    ),
)

data class AboutBlock(
    val sessionId: SessionId?,
    val month: String,
    val day: String,
    val title1: String,
    val title2: String,
    val description: String?,
)

internal val ABOUT_CONFERENCE_BLOCKS = listOf(
    AboutBlock(
        sessionId = SessionId("857088"),
        month = "MAY",
        day = "22",
        title1 = "Opening",
        title2 = "keynote",
        description = null,
    ),
    AboutBlock(
        sessionId = SessionId("797367"),
        month = "MAY",
        day = "23",
        title1 = "Second day",
        title2 = "keynote",
        description = null,
    ),
    AboutBlock(
        sessionId = null,
        month = "MAY",
        day = "22-23",
        title1 = "Code",
        title2 = "labs",
        description = "Participate in code labs by Google and get practical, hands-on experience with Kotlin Multiplatform! They'll guide you through setting up your environment, introducing shared logic to a mobile app, and migrating existing Android code to multiple platforms.",
    ),
    AboutBlock(
        sessionId = SessionId("62b7f3ff-2afa-4b2e-8aec-e69e536cdb6a"),
        month = "MAY",
        day = "22",
        title1 = "",
        title2 = "Party",
        description = "Have fun and mingle with the community at the biggest Kotlin party of the year!",
    ),
    AboutBlock(
        sessionId = SessionId("857092"),
        month = "MAY",
        day = "23",
        title1 = "Closing",
        title2 = "panel",
        description = "Come and seize the opportunity to ask the KotlinConf speakers your questions in person.",
    )
)
