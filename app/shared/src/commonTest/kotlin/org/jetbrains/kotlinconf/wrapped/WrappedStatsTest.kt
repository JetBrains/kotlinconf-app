package org.jetbrains.kotlinconf.wrapped

import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import org.jetbrains.kotlinconf.Score
import org.jetbrains.kotlinconf.Session
import org.jetbrains.kotlinconf.SessionCardView
import org.jetbrains.kotlinconf.SessionId
import org.jetbrains.kotlinconf.Speaker
import org.jetbrains.kotlinconf.SpeakerId
import org.jetbrains.kotlinconf.TagInfo
import org.jetbrains.kotlinconf.VoteInfo
import org.jetbrains.kotlinconf.asSessionCard
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.minutes

class WrappedStatsTest {

    private val alice = Speaker(SpeakerId("alice"), "Alice", "Dev", "", "https://example.com/a.png")
    private val bob = Speaker(SpeakerId("bob"), "Bob", "Dev", "", "https://example.com/b.png")
    private val carol = Speaker(SpeakerId("carol"), "Carol", "Dev", "", "https://example.com/c.png")
    private val speakers = listOf(alice, bob, carol)

    private val tags = TagInfo(
        categories = listOf("Multiplatform", "Compose", "Server"),
        levels = listOf("Beginner", "Advanced"),
        formats = listOf("Talk", "Workshop"),
    )

    private val day1 = LocalDate(2026, 5, 21)
    private val day2 = LocalDate(2026, 5, 22)
    private val beforeConference = LocalDateTime(2026, 5, 20, 8, 0)
    private val afterConference = LocalDateTime(2026, 5, 23, 8, 0)

    private fun session(
        id: String,
        day: LocalDate,
        startHour: Int,
        durationMinutes: Int = 45,
        speakerIds: List<SpeakerId> = listOf(alice.id),
        location: String = "Room A",
        tags: List<String>? = listOf("Multiplatform", "Beginner"),
    ) = Session(
        id = SessionId(id),
        title = "Session $id",
        description = "",
        speakerIds = speakerIds,
        location = location,
        startsAt = LocalDateTime(day.year, day.month, day.day, startHour, 0),
        endsAt = LocalDateTime(day.year, day.month, day.day, startHour + durationMinutes / 60, durationMinutes % 60),
        tags = tags,
    )

    private val serviceEvent = session("lunch", day1, 12, speakerIds = emptyList(), tags = emptyList(), location = "Hall")

    private val sessions = listOf(
        session("s1", day1, 9, tags = listOf("Multiplatform", "Beginner")),
        session("s2", day1, 10, speakerIds = listOf(alice.id, bob.id), tags = listOf("Compose", "Advanced")),
        session("s3", day1, 11, durationMinutes = 10, speakerIds = listOf(carol.id), tags = listOf("Compose"), location = "Room B"),
        session("s4", day2, 9, speakerIds = listOf(bob.id), tags = listOf("Server"), location = "Room B"),
        session("s5", day2, 10, durationMinutes = 10, speakerIds = listOf(carol.id), tags = listOf("Multiplatform")),
        serviceEvent,
    )

    private fun cards(
        favorites: Set<String> = emptySet(),
        votes: Map<String, Score> = emptyMap(),
        now: LocalDateTime = afterConference,
    ): List<SessionCardView> {
        val speakersById = speakers.associateBy { it.id }
        val favoriteIds = favorites.map { SessionId(it) }.toSet()
        return sessions.map { session ->
            val vote = votes[session.id.id]?.let { VoteInfo(session.id, it) }
            session.asSessionCard(speakersById, now, favoriteIds, vote)
        }
    }

    private fun compute(
        favorites: Set<String> = emptySet(),
        votes: Map<String, Score> = emptyMap(),
        usage: UsageStats = UsageStats(),
        now: LocalDateTime = afterConference,
        tagInfo: TagInfo? = tags,
    ) = computeWrappedStats(
        year = 2026,
        sessions = cards(favorites, votes, now),
        speakers = speakers,
        tagInfo = tagInfo,
        usage = usage,
        now = now,
    )

    @Test
    fun eventStatsIgnoreServiceEvents() {
        val event = compute().event

        assertEquals(5, event.talkCount)
        assertEquals(3, event.speakerCount)
        assertEquals(2, event.dayCount)
        assertEquals(2, event.roomCount)
        assertEquals(2, event.lightningTalkCount)
        assertEquals(45.minutes * 3 + 10.minutes * 2, event.totalContentDuration)
    }

    @Test
    fun eventTopTopicsOnlyCountCategoryTags() {
        val topics = compute().event.topTopics

        // Multiplatform and Compose tie at 2 and are sorted alphabetically, Server has 1.
        // Level tags like "Beginner" and "Advanced" are not topics.
        assertEquals(
            listOf(TagCount("Compose", 2), TagCount("Multiplatform", 2), TagCount("Server", 1)),
            topics,
        )
    }

    @Test
    fun allTagsCountAsTopicsWithoutTagInfo() {
        val topics = compute(tagInfo = null).event.topTopics

        // Level tags now count too; "Advanced" wins the alphabetical tie-break among the single-use tags
        assertEquals(
            listOf(TagCount("Compose", 2), TagCount("Multiplatform", 2), TagCount("Advanced", 1)),
            topics,
        )
    }

    @Test
    fun bookmarkStats() {
        val bookmarks = compute(favorites = setOf("s1", "s2", "s3", "lunch")).bookmarks

        assertEquals(3, bookmarks.count)
        assertEquals(45.minutes * 2 + 10.minutes, bookmarks.totalDuration)
        assertEquals(3f / 5f, bookmarks.shareOfTalks)
        assertEquals(1, bookmarks.lightningTalkCount)
        assertEquals(TagCount("Compose", 2), bookmarks.topTopic)
        assertEquals(DayCount(day1, 3), bookmarks.busiestDay)
        assertEquals(LocalDateTime(2026, 5, 21, 9, 0), bookmarks.earliestStart)
    }

    @Test
    fun topSpeakersAreSortedByCountThenName() {
        val bookmarks = compute(favorites = setOf("s1", "s2", "s3", "s4")).bookmarks

        // Alice: s1, s2 = 2; Bob: s2, s4 = 2; Carol: s3 = 1
        assertEquals(
            listOf(SpeakerCount(alice, 2), SpeakerCount(bob, 2), SpeakerCount(carol, 1)),
            bookmarks.topSpeakers,
        )
    }

    @Test
    fun emptyBookmarks() {
        val bookmarks = compute().bookmarks

        assertEquals(0, bookmarks.count)
        assertEquals(0f, bookmarks.shareOfTalks)
        assertNull(bookmarks.topTopic)
        assertNull(bookmarks.busiestDay)
        assertNull(bookmarks.earliestStart)
        assertTrue(bookmarks.topSpeakers.isEmpty())
    }

    @Test
    fun voteStats() {
        val votes = compute(
            votes = mapOf("s1" to Score.GOOD, "s2" to Score.GOOD, "s3" to Score.OK, "s4" to Score.BAD),
        ).votes

        assertEquals(2, votes.good)
        assertEquals(1, votes.ok)
        assertEquals(1, votes.bad)
        assertEquals(4, votes.total)
        assertEquals(0.5f, votes.positivity)
    }

    @Test
    fun noVotesHaveNoPositivity() {
        val votes = compute().votes

        assertEquals(0, votes.total)
        assertNull(votes.positivity)
    }

    @Test
    fun usageSummaryIgnoresUnknownSessions() {
        val usage = UsageStats(
            appLaunches = 7,
            sessionViews = mapOf(
                SessionId("s1") to 2,
                SessionId("s2") to 5,
                SessionId("removed") to 100,
            ),
            speakerViews = mapOf(alice.id to 3, bob.id to 1),
        )

        val summary = compute(usage = usage).usage

        assertEquals(7, summary.appLaunches)
        assertEquals(7, summary.totalSessionViews)
        assertEquals(2, summary.uniqueSessionsViewed)
        assertEquals(4, summary.totalSpeakerViews)
        assertEquals(SessionId("s2"), summary.mostViewedSession?.id)
        assertEquals(5, summary.mostViewedSessionCount)
    }

    @Test
    fun conferenceOverDependsOnNow() {
        assertFalse(compute(now = beforeConference).isConferenceOver)
        assertTrue(compute(now = afterConference).isConferenceOver)
    }

    @Test
    fun isEmptyWithoutActivity() {
        assertTrue(compute().isEmpty)
        assertFalse(compute(favorites = setOf("s1")).isEmpty)
        assertFalse(compute(votes = mapOf("s1" to Score.OK)).isEmpty)
        assertFalse(compute(usage = UsageStats(sessionViews = mapOf(SessionId("s1") to 1))).isEmpty)
    }

    @Test
    fun personalityFreeSpiritWithoutActivity() {
        assertEquals(AttendeePersonality.FreeSpirit, compute().personality)
        // A handful of views alone isn't enough to count as activity
        assertEquals(
            AttendeePersonality.FreeSpirit,
            compute(usage = UsageStats(sessionViews = mapOf(SessionId("s1") to 3))).personality,
        )
    }

    @Test
    fun personalityPlannerWhenBookmarkingDominates() {
        assertEquals(
            AttendeePersonality.Planner,
            compute(favorites = setOf("s1", "s2", "s4"), votes = mapOf("s1" to Score.GOOD)).personality,
        )
    }

    @Test
    fun personalityReviewerWhenRatingDominates() {
        assertEquals(
            AttendeePersonality.Reviewer,
            compute(
                favorites = setOf("s1"),
                votes = mapOf("s1" to Score.GOOD, "s2" to Score.OK, "s4" to Score.BAD),
            ).personality,
        )
    }

    @Test
    fun personalityExplorerWhenBrowsingDominates() {
        val usage = UsageStats(
            sessionViews = listOf("s1", "s2", "s3", "s4", "s5").associate { SessionId(it) to 1 },
        )
        assertEquals(
            AttendeePersonality.Explorer,
            compute(favorites = setOf("s1"), usage = usage).personality,
        )
    }

    @Test
    fun personalityLightningFanWhenMostBookmarksAreLightningTalks() {
        assertEquals(
            AttendeePersonality.LightningFan,
            compute(favorites = setOf("s3", "s5", "s1")).personality,
        )
    }

    @Test
    fun usageStatsIncrements() {
        val stats = UsageStats()
            .withAppLaunch()
            .withAppLaunch()
            .withSessionView(SessionId("s1"))
            .withSessionView(SessionId("s1"))
            .withSessionView(SessionId("s2"))
            .withSpeakerView(alice.id)

        assertEquals(2, stats.appLaunches)
        assertEquals(mapOf(SessionId("s1") to 2, SessionId("s2") to 1), stats.sessionViews)
        assertEquals(3, stats.totalSessionViews)
        assertEquals(1, stats.totalSpeakerViews)
    }
}
