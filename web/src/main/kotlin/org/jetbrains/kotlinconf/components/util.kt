package org.jetbrains.kotlinconf.components

import org.jetbrains.kotlinconf.Date
import org.jetbrains.kotlinconf.parseDate
import org.jetbrains.kotlinconf.toReadableDateTimeString
import org.jetbrains.kotlinconf.toReadableString
import kotlinext.js.clone
import kotlinext.js.jsObject
import react.RBuilder
import react.RState
import react.React
import react.dom.div
import react.dom.span

inline fun <T : RState> React.Component<*, T>.setState(action: T.() -> Unit) {
    setState(jsObject(action))
}

inline fun <T : RState> React.Component<*, T>.updateState(action: T.() -> Unit) {
    setState(clone(state).apply(action))
}

inline fun <T> RBuilder.loading(value: T?, action: (T) -> Unit) {
    if (value == null) {
        div(classes = "loading") {
            +"Loading data..."
        }
    }
    else {
        action(value)
    }
}

fun RBuilder.dateRange(range: Pair<String?, String?>) =
        dateRange(range.first?.let { parseDate(it) } to range.second?.let { parseDate(it) })

fun RBuilder.dateRange(range: Pair<Date?, Date?>) {
    val (startsAt, endsAt) = range
    div(classes = "session-time") {
        if (startsAt != null) {
            +if (endsAt != null) {
                (startsAt to endsAt).toReadableString()
            }
            else {
                startsAt.toReadableDateTimeString()
            }
        }
        else {
            span(classes = "session-time-unknown") { +"Time unknown" }
        }
    }
}


@Suppress("NOTHING_TO_INLINE")
inline fun Double.toFixed(precision: Int): String = asDynamic().toFixed(precision)