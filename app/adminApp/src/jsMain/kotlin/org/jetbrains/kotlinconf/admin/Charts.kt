// ABOUTME: Histogram charts for the admin overview: how talk ratings and popularity scores are distributed.
// ABOUTME: Each talk is counted once (deduped by session) and bucketed into 15 groups spanning 0.0–3.0.
package org.jetbrains.kotlinconf.admin

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import org.jetbrains.compose.web.dom.*

private const val BUCKET_COUNT = 15
private const val RANGE_MAX = 3.0
private const val BUCKET_WIDTH = RANGE_MAX / BUCKET_COUNT // 0.2

/** Counts of distinct, voted-on talks whose score falls in each of the 15 buckets. */
private fun distribution(values: List<Double>): IntArray {
    val buckets = IntArray(BUCKET_COUNT)
    for (v in values) {
        val index = (v / BUCKET_WIDTH).toInt().coerceIn(0, BUCKET_COUNT - 1)
        buckets[index]++
    }
    return buckets
}

@Composable
fun DistributionCharts(data: AggregatedData) {
    // One score per talk; speaker rows of the same session share counts, so dedupe by session.
    val talks = remember(data) {
        data.rows.distinctBy { it.sessionId }.map { it.counts }.filter { it.total > 0 }
    }

    Div(attrs = { classes("card") }) {
        H2(attrs = { classes("section-title") }) { Text("Score distribution") }
        if (talks.isEmpty()) {
            Div(attrs = { classes("empty-state") }) { Text("No voted-on talks to chart yet.") }
            return@Div
        }
        Div(attrs = { classes("charts-grid") }) {
            Histogram("Rating", distribution(talks.map { it.rating }), ::ratingColor)
            Histogram("Pop. score", distribution(talks.map { it.popularity }), ::popularityColor)
        }
    }
}

@Composable
private fun Histogram(title: String, buckets: IntArray, color: (Double) -> String) {
    val max = buckets.max().coerceAtLeast(1)
    Div(attrs = { classes("chart") }) {
        Div(attrs = { classes("chart-title") }) { Text(title) }
        Div(attrs = { classes("chart-bars") }) {
            buckets.forEachIndexed { index, count ->
                val lo = index * BUCKET_WIDTH
                val hi = lo + BUCKET_WIDTH
                Div(
                    attrs = {
                        classes("chart-col")
                        attr(
                            "title",
                            "${lo.toFixed(1)}–${hi.toFixed(1)}: $count talk" +
                            if (count == 1) "" else "s",
                        )
                    },
                ) {
                    Div(attrs = { classes("chart-count") }) {
                        Text(if (count > 0) count.toString() else "")
                    }
                    Div(
                        attrs = {
                            classes("chart-bar")
                            style {
                                property("height", "${100.0 * count / max}%")
                                property("background", color(lo + BUCKET_WIDTH / 2))
                            }
                        },
                    ) {}
                    Div(attrs = { classes("chart-tick") }) { Text(lo.toFixed(1)) }
                }
            }
        }
    }
}
