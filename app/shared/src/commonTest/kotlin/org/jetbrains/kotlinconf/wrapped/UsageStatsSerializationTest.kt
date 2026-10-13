package org.jetbrains.kotlinconf.wrapped

import kotlinx.serialization.json.Json
import org.jetbrains.kotlinconf.SessionId
import org.jetbrains.kotlinconf.SpeakerId
import kotlin.test.Test
import kotlin.test.assertEquals

class UsageStatsSerializationTest {
    private val json = Json { ignoreUnknownKeys = true }

    @Test
    fun roundTripsThroughJson() {
        val stats = UsageStats(
            appLaunches = 3,
            sessionViews = mapOf(SessionId("s1") to 2, SessionId("s2") to 1),
            speakerViews = mapOf(SpeakerId("alice") to 4),
        )

        val encoded = json.encodeToString(UsageStats.serializer(), stats)
        val decoded = json.decodeFromString(UsageStats.serializer(), encoded)

        assertEquals(stats, decoded)
    }

    @Test
    fun decodesEmptyObjectToDefaults() {
        val decoded = json.decodeFromString(UsageStats.serializer(), "{}")

        assertEquals(UsageStats(), decoded)
    }
}
