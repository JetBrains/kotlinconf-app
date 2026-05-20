// ABOUTME: The overview dashboard: per-session vote tallies, filters, sorting, CSV export, and feedback.
// ABOUTME: Each feedback comment links to the commenter's user page via their pseudo-name.

package org.jetbrains.kotlinconf.admin

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import kotlin.math.abs
import org.jetbrains.compose.web.attributes.*
import org.jetbrains.compose.web.dom.*
import org.jetbrains.kotlinconf.Score

private enum class SortKey { TITLE, SPEAKER, GOOD, OK, BAD, TOTAL, AVG, FEEDBACK }

private data class Column(val key: SortKey, val label: String, val right: Boolean)

private val COLUMNS = listOf(
    Column(SortKey.TITLE, "Title", right = false),
    Column(SortKey.SPEAKER, "Speaker", right = false),
    Column(SortKey.GOOD, "👍", right = true),
    Column(SortKey.OK, "😐", right = true),
    Column(SortKey.BAD, "👎", right = true),
    Column(SortKey.TOTAL, "Total", right = true),
    Column(SortKey.AVG, "Avg", right = true),
    Column(SortKey.FEEDBACK, "Feedback", right = true),
)

@Composable
fun OverviewScreen(data: AggregatedData, year: Int, onOpenUser: (String) -> Unit) {
    var query by remember { mutableStateOf("") }
    var minVotes by remember { mutableStateOf(0) }
    var onlyFeedback by remember { mutableStateOf(false) }
    var hideEmpty by remember { mutableStateOf(true) }
    var sortKey by remember { mutableStateOf(SortKey.AVG) }
    var sortDir by remember { mutableStateOf(-1) }
    var expanded by remember { mutableStateOf(emptySet<String>()) }

    val visible = remember(data, query, minVotes, onlyFeedback, hideEmpty, sortKey, sortDir) {
        data.rows
            .filter { row -> matchesFilters(row, query, minVotes, onlyFeedback, hideEmpty) }
            .sortedWith(comparatorFor(sortKey, sortDir))
    }

    Div(attrs = { classes("card") }) {
        Div(attrs = { classes("toolbar") }) {
            Div(attrs = { classes("filters") }) {
                Input(type = InputType.Text) {
                    attr("placeholder", "Search title or speaker…")
                    value(query)
                    onInput { query = it.value }
                }
                Label(attrs = { classes("checkbox-row") }) {
                    Text("Min votes:")
                    Input(type = InputType.Number) {
                        attr("min", "0")
                        value(minVotes.toString())
                        onInput { minVotes = it.value?.toInt()?.coerceAtLeast(0) ?: 0 }
                    }
                }
                Label(attrs = { classes("checkbox-row") }) {
                    Input(type = InputType.Checkbox) { checked(onlyFeedback); onInput { onlyFeedback = it.value } }
                    Text("With feedback only")
                }
                Label(attrs = { classes("checkbox-row") }) {
                    Input(type = InputType.Checkbox) { checked(hideEmpty); onInput { hideEmpty = it.value } }
                    Text("Hide 0-vote talks")
                }
            }
            Div(attrs = { style { property("display", "flex"); property("gap", "10px"); property("align-items", "center") } }) {
                Span(attrs = { classes("row-count") }) {
                    Text("${visible.size} row" + if (visible.size == 1) "" else "s")
                }
                Button(attrs = {
                    classes("secondary")
                    onClick { exportCsv(year, visible, data) }
                }) { Text("Download CSV") }
            }
        }

        Table {
            Thead {
                Tr {
                    COLUMNS.forEach { column ->
                        Th(attrs = {
                            if (column.right) classes("right")
                            onClick {
                                if (sortKey == column.key) {
                                    sortDir = -sortDir
                                } else {
                                    sortKey = column.key
                                    sortDir = if (column.right) -1 else 1
                                }
                            }
                        }) {
                            Text("${column.label} ")
                            Span(attrs = { classes("arrow") }) {
                                Text(if (sortKey == column.key) (if (sortDir == -1) "▾" else "▴") else "")
                            }
                        }
                    }
                }
            }
            Tbody {
                visible.forEach { row ->
                    val key = rowKey(row)
                    Tr(attrs = {
                        classes("session-row")
                        onClick { expanded = if (key in expanded) expanded - key else expanded + key }
                    }) {
                        Td(attrs = { classes("title") }) { TitleCell(row) }
                        Td(attrs = { classes("speakers") }) { Text(row.speaker) }
                        NumCell(row.counts.good.toString())
                        NumCell(row.counts.ok.toString())
                        NumCell(row.counts.bad.toString())
                        NumCell(row.counts.total.toString())
                        AvgCell(row.counts)
                        NumCell(if (row.feedback.isNotEmpty()) row.feedback.size.toString() else "")
                    }
                    if (key in expanded) {
                        Tr(attrs = { classes("feedback-row") }) {
                            Td(attrs = { attr("colspan", COLUMNS.size.toString()) }) {
                                ExpandedFeedback(row, data, onOpenUser)
                            }
                        }
                    }
                }
            }
        }
        if (visible.isEmpty()) {
            Div(attrs = { classes("empty-state") }) { Text("No talks match the current filters.") }
        }
    }

    FeedbackByTalk(data, query, onOpenUser)
}

@Composable
private fun TitleCell(row: SessionRow) {
    val url = row.videoUrl
    if (url != null) {
        A(href = url, attrs = {
            target(ATarget.Blank)
            attr("rel", "noopener")
            onClick { it.stopPropagation() }
        }) { Text(row.title) }
    } else {
        Text(row.title)
    }
}

@Composable
private fun NumCell(text: String) {
    Td(attrs = { classes("num") }) { Text(text) }
}

@Composable
private fun AvgCell(counts: VoteCounts) {
    Td(attrs = {
        classes("num")
        if (counts.total > 0) style { property("background", avgColor(counts.avg)) }
    }) {
        Text(if (counts.total > 0) counts.avg.toFixed(2) else "—")
    }
}

@Composable
private fun ExpandedFeedback(row: SessionRow, data: AggregatedData, onOpenUser: (String) -> Unit) {
    if (row.feedback.isEmpty()) {
        Span(attrs = { classes("empty") }) { Text("No feedback text for this talk.") }
        return
    }
    Div { Text("${row.feedback.size} feedback item" + (if (row.feedback.size == 1) "" else "s") + ":") }
    FeedbackList(row.sessionId, row.feedback, data, onOpenUser)
}

@Composable
private fun FeedbackByTalk(data: AggregatedData, query: String, onOpenUser: (String) -> Unit) {
    val groups = remember(data, query) {
        data.feedbackGroups()
            .filter { group ->
                query.isBlank() || (group.title + " " + group.speakers.joinToString(" "))
                    .lowercase().contains(query.trim().lowercase())
            }
            .sortedWith(
                compareByDescending<FeedbackGroup> { it.feedback.size }.thenByDescending { it.counts.avg }
            )
    }

    Div(attrs = { classes("card") }) {
        H2(attrs = { classes("section-title") }) { Text("Feedback by talk") }
        P(attrs = { classes("section-sub") }) {
            Text("All free-text comments, grouped by session. Respects the search filter above.")
        }
        if (groups.isEmpty()) {
            Div(attrs = { classes("empty-state") }) { Text("No feedback comments in the current view.") }
            return@Div
        }
        groups.forEach { group ->
            Div(attrs = { classes("talk-block") }) {
                Div(attrs = { classes("talk-head") }) {
                    Div {
                        Span(attrs = { classes("talk-title") }) {
                            val url = group.videoUrl
                            if (url != null) {
                                A(href = url, attrs = { target(ATarget.Blank); attr("rel", "noopener") }) { Text(group.title) }
                            } else {
                                Text(group.title)
                            }
                        }
                        if (group.speakers.isNotEmpty()) {
                            Span(attrs = { classes("talk-speaker") }) { Text("— " + group.speakers.joinToString(", ")) }
                        }
                    }
                    Div(attrs = { classes("talk-counts") }) {
                        Span { Text("👍 ${group.counts.good}") }
                        Span { Text("😐 ${group.counts.ok}") }
                        Span { Text("👎 ${group.counts.bad}") }
                        if (group.counts.total > 0) {
                            Span(attrs = {
                                style {
                                    property("background", avgColor(group.counts.avg))
                                    property("padding", "1px 6px")
                                    property("border-radius", "4px")
                                }
                            }) { Text("avg ${group.counts.avg.toFixed(2)}") }
                        }
                    }
                }
                FeedbackList(group.sessionId, group.feedback, data, onOpenUser)
            }
        }
    }
}

/** A list of comments; each shows the commenter's score badge and a link to their user page. */
@Composable
fun FeedbackList(sessionId: String, items: List<FeedbackItem>, data: AggregatedData, onOpenUser: (String) -> Unit) {
    Ul(attrs = { classes("feedback-list") }) {
        items.forEach { item ->
            Li {
                val score = data.scoreByUserSession[userSessionKey(item.userId, sessionId)]
                Span(attrs = { classes("score-badge"); attr("title", scoreTitle(score)) }) {
                    Text(score?.emoji() ?: "—")
                }
                Button(attrs = { classes("pseudo-link"); onClick { onOpenUser(item.userId) } }) {
                    Text(pseudoName(item.userId))
                }
                Text(" " + item.value)
            }
        }
    }
}

private fun matchesFilters(
    row: SessionRow,
    query: String,
    minVotes: Int,
    onlyFeedback: Boolean,
    hideEmpty: Boolean,
): Boolean {
    if (hideEmpty && row.counts.total == 0) return false
    if (row.counts.total < minVotes) return false
    if (onlyFeedback && row.feedback.isEmpty()) return false
    if (query.isNotBlank()) {
        val hay = (row.title + " " + row.speaker).lowercase()
        if (!hay.contains(query.trim().lowercase())) return false
    }
    return true
}

private fun comparatorFor(key: SortKey, dir: Int): Comparator<SessionRow> {
    val base: Comparator<SessionRow> = when (key) {
        SortKey.TITLE -> compareBy { it.title.lowercase() }
        SortKey.SPEAKER -> compareBy { it.speaker.lowercase() }
        SortKey.GOOD -> compareBy { it.counts.good }
        SortKey.OK -> compareBy { it.counts.ok }
        SortKey.BAD -> compareBy { it.counts.bad }
        SortKey.TOTAL -> compareBy { it.counts.total }
        SortKey.AVG -> compareBy { it.counts.avg }
        SortKey.FEEDBACK -> compareBy { it.feedback.size }
    }
    return if (dir < 0) base.reversed() else base
}

private fun exportCsv(year: Int, rows: List<SessionRow>, data: AggregatedData) {
    val header = listOf(
        "session_id", "title", "speaker_id", "speaker",
        "good", "ok", "bad", "total", "avg_score", "feedback",
    )
    val lines = mutableListOf(header)
    val whitespace = Regex("\\s+")
    rows.forEach { row ->
        val feedback = row.feedback.joinToString(" | ") { item ->
            val score = data.scoreByUserSession[userSessionKey(item.userId, row.sessionId)]
            (score?.emoji() ?: "—") + " " + item.value
        }.replace(whitespace, " ")
        lines.add(
            listOf(
                row.sessionId, row.title, row.speakerId, row.speaker,
                row.counts.good.toString(), row.counts.ok.toString(), row.counts.bad.toString(),
                row.counts.total.toString(),
                if (row.counts.total > 0) row.counts.avg.toFixed(3) else "",
                feedback,
            )
        )
    }
    downloadCsv("kotlinconf-$year-votes.csv", lines)
}

private fun rowKey(row: SessionRow): String = "${row.sessionId}|${row.speakerId}"

private fun scoreTitle(score: Score?): String =
    if (score != null) "Voter rated this talk $score" else "Commenter didn't vote (or unvoted)"

/** Red → soft → green based on avg in [-1, +1], matching the original dashboard shading. */
private fun avgColor(avg: Double): String {
    val a = avg.coerceIn(-1.0, 1.0)
    val hue = 60 + a * 60
    val light = 92 - abs(a) * 12
    return "hsl($hue 70% $light%)"
}

private fun Double.toFixed(digits: Int): String = asDynamic().toFixed(digits) as String
