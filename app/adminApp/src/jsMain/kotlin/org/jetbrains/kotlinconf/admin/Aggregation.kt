// ABOUTME: Pure functions that join conference data with votes and feedback for the admin views.
// ABOUTME: DOM-free so the dashboard math (counts, averages, per-user grouping) stays readable.

package org.jetbrains.kotlinconf.admin

import org.jetbrains.kotlinconf.Conference
import org.jetbrains.kotlinconf.Score
import org.jetbrains.kotlinconf.Session
import org.jetbrains.kotlinconf.Speaker

data class FeedbackItem(val userId: String, val value: String)

data class VoteCounts(val good: Int = 0, val ok: Int = 0, val bad: Int = 0) {
    val total: Int get() = good + ok + bad
    val avg: Double get() = if (total == 0) 0.0 else (good - bad).toDouble() / total
}

/** One row per (session, speaker), matching the original dashboard layout. */
data class SessionRow(
    val sessionId: String,
    val title: String,
    val videoUrl: String?,
    val speakerId: String,
    val speaker: String,
    val counts: VoteCounts,
    val feedback: List<FeedbackItem>,
)

/** A session's feedback collapsed across its speakers (for the "Feedback by talk" section). */
data class FeedbackGroup(
    val sessionId: String,
    val title: String,
    val videoUrl: String?,
    val speakers: List<String>,
    val counts: VoteCounts,
    val feedback: List<FeedbackItem>,
)

data class UserVote(val sessionId: String, val title: String, val score: Score?)
data class UserFeedback(val sessionId: String, val title: String, val value: String)

class AggregatedData(
    val rows: List<SessionRow>,
    /** "userId|sessionId" -> the score that user gave that session (powers the feedback badges). */
    val scoreByUserSession: Map<String, Score>,
    val sessionsById: Map<String, Session>,
    val speakersById: Map<String, Speaker>,
    val votes: List<AdminVoteRow>,
    val feedback: List<AdminFeedbackRow>,
)

fun userSessionKey(userId: String, sessionId: String) = "$userId|$sessionId"

fun aggregate(
    conference: Conference,
    votes: List<AdminVoteRow>,
    feedback: List<AdminFeedbackRow>,
): AggregatedData {
    val speakersById = conference.speakers.associateBy { it.id.id }
    val sessionsById = conference.sessions.associateBy { it.id.id }

    val countsBySession = HashMap<String, MutableVoteCounts>()
    val scoreByUserSession = HashMap<String, Score>()
    for (v in votes) {
        val sid = v.sessionId.id
        val counts = countsBySession.getOrPut(sid) { MutableVoteCounts() }
        when (v.score) {
            Score.GOOD -> counts.good++
            Score.OK -> counts.ok++
            Score.BAD -> counts.bad++
            null -> {} // cleared / unvoted — ignored in tallies
        }
        if (v.score != null) scoreByUserSession[userSessionKey(v.userId, sid)] = v.score
    }

    val feedbackBySession = HashMap<String, MutableList<FeedbackItem>>()
    for (f in feedback) {
        feedbackBySession.getOrPut(f.sessionId.id) { mutableListOf() }
            .add(FeedbackItem(f.userId, f.value))
    }

    val rows = ArrayList<SessionRow>()
    for (s in conference.sessions) {
        val sid = s.id.id
        val counts = countsBySession[sid]?.toImmutable() ?: VoteCounts()
        val fb = feedbackBySession[sid].orEmpty()
        val speakerIds: List<String?> =
            if (s.speakerIds.isNotEmpty()) s.speakerIds.map { it.id } else listOf(null)
        for (speakerId in speakerIds) {
            val speaker = speakerId?.let { speakersById[it] }
            rows.add(
                SessionRow(
                    sessionId = sid,
                    title = s.title,
                    videoUrl = s.videoUrl,
                    speakerId = speaker?.id?.id ?: "",
                    speaker = speaker?.name ?: "—",
                    counts = counts,
                    feedback = fb,
                )
            )
        }
    }
    return AggregatedData(rows, scoreByUserSession, sessionsById, speakersById, votes, feedback)
}

/** Sessions that have feedback, with their speakers merged (one block per session). */
fun AggregatedData.feedbackGroups(): List<FeedbackGroup> {
    val bySession = LinkedHashMap<String, FeedbackGroup>()
    for (row in rows) {
        if (row.feedback.isEmpty()) continue
        val existing = bySession[row.sessionId]
        if (existing == null) {
            bySession[row.sessionId] = FeedbackGroup(
                sessionId = row.sessionId,
                title = row.title,
                videoUrl = row.videoUrl,
                speakers = if (row.speaker != "—") listOf(row.speaker) else emptyList(),
                counts = row.counts,
                feedback = row.feedback,
            )
        } else if (row.speaker != "—" && row.speaker !in existing.speakers) {
            bySession[row.sessionId] = existing.copy(speakers = existing.speakers + row.speaker)
        }
    }
    return bySession.values.toList()
}

fun AggregatedData.votesForUser(userId: String): List<UserVote> =
    votes.asSequence()
        .filter { it.userId == userId }
        .map { UserVote(it.sessionId.id, titleOf(it.sessionId.id), it.score) }
        .sortedBy { it.title.lowercase() }
        .toList()

fun AggregatedData.feedbackForUser(userId: String): List<UserFeedback> =
    feedback.asSequence()
        .filter { it.userId == userId }
        .map { UserFeedback(it.sessionId.id, titleOf(it.sessionId.id), it.value) }
        .sortedBy { it.title.lowercase() }
        .toList()

private fun AggregatedData.titleOf(sessionId: String): String =
    sessionsById[sessionId]?.title ?: sessionId

private class MutableVoteCounts(var good: Int = 0, var ok: Int = 0, var bad: Int = 0) {
    fun toImmutable() = VoteCounts(good, ok, bad)
}
