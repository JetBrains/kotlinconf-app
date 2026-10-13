package org.jetbrains.kotlinconf.wrapped

import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import org.jetbrains.kotlinconf.Score
import org.jetbrains.kotlinconf.SessionCardView
import org.jetbrains.kotlinconf.Speaker
import org.jetbrains.kotlinconf.SpeakerId
import org.jetbrains.kotlinconf.TagInfo
import org.jetbrains.kotlinconf.isServiceEvent
import org.jetbrains.kotlinconf.minus
import kotlin.time.Duration

/**
 * Everything the Wrapped story needs, computed from on-device data only.
 */
data class WrappedStats(
    val year: Int,
    val isConferenceOver: Boolean,
    val event: EventStats,
    val bookmarks: BookmarkStats,
    val votes: VoteStats,
    val usage: UsageSummary,
    val personality: AttendeePersonality,
) {
    /** True when the attendee has done nothing we could summarize. */
    val isEmpty: Boolean
        get() = bookmarks.count == 0 && votes.total == 0 && usage.totalSessionViews == 0
}

/** Facts about the conference itself, independent of the attendee. */
data class EventStats(
    /** Talks, workshops, lightning talks – everything that has speakers or tags. */
    val talkCount: Int,
    val speakerCount: Int,
    val dayCount: Int,
    val roomCount: Int,
    val lightningTalkCount: Int,
    val totalContentDuration: Duration,
    /** Most frequently used topic tags across all talks, most common first. */
    val topTopics: List<TagCount>,
)

data class BookmarkStats(
    val count: Int,
    val totalDuration: Duration,
    /** Share of all talks that were bookmarked, 0..1. */
    val shareOfTalks: Float,
    val lightningTalkCount: Int,
    /** The most common topic tag among bookmarked talks. */
    val topTopic: TagCount?,
    /** The conference day with the most bookmarks. */
    val busiestDay: DayCount?,
    /** Speakers who appear most often in bookmarked talks, up to 3. */
    val topSpeakers: List<SpeakerCount>,
    /** The earliest bookmarked session start time of the whole conference. */
    val earliestStart: LocalDateTime?,
)

data class VoteStats(
    val good: Int,
    val ok: Int,
    val bad: Int,
) {
    val total: Int get() = good + ok + bad

    /** Share of positive ratings, 0..1, or null when nothing was rated. */
    val positivity: Float? get() = if (total == 0) null else good.toFloat() / total
}

data class UsageSummary(
    val appLaunches: Int,
    val totalSessionViews: Int,
    val uniqueSessionsViewed: Int,
    val totalSpeakerViews: Int,
    /** The session whose details were opened most often, if any. */
    val mostViewedSession: SessionCardView?,
    val mostViewedSessionCount: Int,
)

data class TagCount(val tag: String, val count: Int)
data class DayCount(val date: LocalDate, val count: Int)
data class SpeakerCount(val speaker: Speaker, val count: Int)

enum class AttendeePersonality {
    /** Bookmarked a large part of the schedule. */
    Planner,

    /** Rated most of what they saw. */
    Reviewer,

    /** Browsed lots of session details without committing to many bookmarks. */
    Explorer,

    /** Mostly bookmarked lightning talks. */
    LightningFan,

    /** Little or no activity recorded. */
    FreeSpirit,
}

/**
 * Computes the Wrapped summary.
 *
 * @param sessions all sessions of the conference, as shown in the schedule
 * @param speakers all speakers of the conference
 * @param tagInfo tag info of the conference, used to tell topic tags apart from level/format tags
 * @param usage on-device usage counters
 * @param now the current (possibly fake) time, used to tell if the conference is over
 */
fun computeWrappedStats(
    year: Int,
    sessions: List<SessionCardView>,
    speakers: List<Speaker>,
    tagInfo: TagInfo?,
    usage: UsageStats,
    now: LocalDateTime,
): WrappedStats {
    val talks = sessions.filterNot { it.isServiceEvent }
    val speakersById = speakers.associateBy { it.id }
    val topicTags: Set<String>? = tagInfo?.categories?.toSet()?.takeIf { it.isNotEmpty() }

    fun SessionCardView.topicTags(): Set<String> =
        if (topicTags == null) this.tags else this.tags.filter { it in topicTags }.toSet()

    fun List<SessionCardView>.countTopics(): List<TagCount> =
        flatMap { it.topicTags() }
            .groupingBy { it }
            .eachCount()
            .map { (tag, count) -> TagCount(tag, count) }
            .sortedWith(compareByDescending<TagCount> { it.count }.thenBy { it.tag })

    fun List<SessionCardView>.totalDuration(): Duration =
        fold(Duration.ZERO) { acc, session -> acc + (session.endsAt - session.startsAt) }

    val event = EventStats(
        talkCount = talks.size,
        speakerCount = speakers.size,
        dayCount = sessions.map { it.startsAt.date }.distinct().size,
        roomCount = talks.map { it.locationLine }.filter { it.isNotBlank() }.distinct().size,
        lightningTalkCount = talks.count { it.isLightning },
        totalContentDuration = talks.totalDuration(),
        topTopics = talks.countTopics().take(3),
    )

    val bookmarked = talks.filter { it.isFavorite }
    val topSpeakers = bookmarked
        .flatMap { it.speakerIds }
        .groupingBy { it }
        .eachCount()
        .mapNotNull { (id: SpeakerId, count) -> speakersById[id]?.let { SpeakerCount(it, count) } }
        .sortedWith(compareByDescending<SpeakerCount> { it.count }.thenBy { it.speaker.name })
        .take(3)
    val busiestDay = bookmarked
        .groupingBy { it.startsAt.date }
        .eachCount()
        .map { (date, count) -> DayCount(date, count) }
        .sortedWith(compareByDescending<DayCount> { it.count }.thenBy { it.date })
        .firstOrNull()

    val bookmarks = BookmarkStats(
        count = bookmarked.size,
        totalDuration = bookmarked.totalDuration(),
        shareOfTalks = if (talks.isEmpty()) 0f else bookmarked.size.toFloat() / talks.size,
        lightningTalkCount = bookmarked.count { it.isLightning },
        topTopic = bookmarked.countTopics().firstOrNull(),
        busiestDay = busiestDay,
        topSpeakers = topSpeakers,
        earliestStart = bookmarked.minOfOrNull { it.startsAt },
    )

    val votes = VoteStats(
        good = talks.count { it.vote == Score.GOOD },
        ok = talks.count { it.vote == Score.OK },
        bad = talks.count { it.vote == Score.BAD },
    )

    val sessionsById = sessions.associateBy { it.id }
    val sessionViews: List<Pair<SessionCardView, Int>> = usage.sessionViews
        .mapNotNull { (id, count) -> sessionsById[id]?.let { it to count } }
        .sortedWith(compareByDescending<Pair<SessionCardView, Int>> { it.second }.thenBy { it.first.title })
    val mostViewed = sessionViews.firstOrNull()
    val usageSummary = UsageSummary(
        appLaunches = usage.appLaunches,
        totalSessionViews = sessionViews.sumOf { it.second },
        uniqueSessionsViewed = sessionViews.size,
        totalSpeakerViews = usage.totalSpeakerViews,
        mostViewedSession = mostViewed?.first,
        mostViewedSessionCount = mostViewed?.second ?: 0,
    )

    val lastEnd = sessions.maxOfOrNull { it.endsAt }

    return WrappedStats(
        year = year,
        isConferenceOver = lastEnd != null && lastEnd <= now,
        event = event,
        bookmarks = bookmarks,
        votes = votes,
        usage = usageSummary,
        personality = pickPersonality(bookmarks, votes, usageSummary),
    )
}

internal fun pickPersonality(
    bookmarks: BookmarkStats,
    votes: VoteStats,
    usage: UsageSummary,
): AttendeePersonality {
    val hasActivity = bookmarks.count > 0 || votes.total > 0 || usage.uniqueSessionsViewed >= 5
    if (!hasActivity) return AttendeePersonality.FreeSpirit

    val mostlyLightning = bookmarks.count >= 3 &&
            bookmarks.lightningTalkCount * 2 >= bookmarks.count

    val scores = listOf(
        AttendeePersonality.Planner to bookmarks.count,
        AttendeePersonality.Reviewer to votes.total * 2,
        AttendeePersonality.Explorer to usage.uniqueSessionsViewed,
        AttendeePersonality.LightningFan to if (mostlyLightning) bookmarks.count + 1 else 0,
    )

    // On ties, the earlier entry wins, so bookmarking beats rating beats browsing
    return scores.maxBy { it.second }.first
}
