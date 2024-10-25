@file:OptIn(ExperimentalResourceApi::class)

package org.jetbrains.kotlinconf.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinconfapp.shared.generated.resources.Res
import kotlinconfapp.shared.generated.resources.lunch
import kotlinconfapp.shared.generated.resources.lunch_active
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.kotlinconf.Agenda
import org.jetbrains.kotlinconf.AppController
import org.jetbrains.kotlinconf.Day
import org.jetbrains.kotlinconf.EventDay
import org.jetbrains.kotlinconf.TimeSlot
import org.jetbrains.kotlinconf.ui.components.AgendaDayHeader
import org.jetbrains.kotlinconf.ui.components.AgendaItem
import org.jetbrains.kotlinconf.ui.components.AgendaTimeSlotHeader
import org.jetbrains.kotlinconf.ui.components.Break
import org.jetbrains.kotlinconf.ui.components.LiveIndicator
import org.jetbrains.kotlinconf.ui.components.Party
import org.jetbrains.kotlinconf.ui.components.TabBar
import org.jetbrains.kotlinconf.ui.theme.agendaHeaderColor
import org.jetbrains.kotlinconf.ui.theme.blackWhite
import org.jetbrains.kotlinconf.ui.theme.grey5Black

@Composable
fun AgendaScreen(agenda: Agenda, scrollState: LazyListState, controller: AppController) {
    val coroutineScope = rememberCoroutineScope()
    val daysSize = agenda.days.map { it.itemsCount() }
    val daysIndex: List<Int> = daysSize.scan(0) { acc, i -> acc + i }

    val displayedDay: EventDay? by remember {
        derivedStateOf {
            val days: Set<Int> = scrollState.layoutInfo.visibleItemsInfo.mapNotNull {
                val key = it.key as String
                val lastIndexOf = key.lastIndexOf('-').takeIf { it >= 0 } ?: return@mapNotNull null
                val day = key.substring(lastIndexOf + 1).toIntOrNull()
                day
            }.toSet()

            if (days.isEmpty()) return@derivedStateOf null
            EventDay.from(days.min())
        }
    }

    val liveSlot = agenda.days.flatMap { it.timeSlots }.firstOrNull { it.isLive }

    fun scrollToDay(day: EventDay) {
        var index = daysIndex[agenda.days.indexOfFirst { it.title == day.title }]
        index -= day.ordinal
        if (index >= 0) {
            coroutineScope.launch { scrollState.scrollToItem(index, 0) }
        }
    }

    LaunchedEffect(Unit) {
        if (scrollState.firstVisibleItemIndex == 0 && scrollState.firstVisibleItemScrollOffset == 0) {
            scrollToDay(EventDay.May23)
        }
    }

    Column(
        Modifier.background(MaterialTheme.colors.agendaHeaderColor)
    ) {
        val day = displayedDay
        if (day != null) {
            TabBar(EventDay.entries, day, onSelect = ::scrollToDay)
        }
        if (liveSlot != null) {
            LiveHeader(liveSlot) {
                coroutineScope.launch { scrollState.scrollToItem(agenda.firstLiveIndex(), 0) }
            }
            HDivider()
        }
        LazyColumn(state = scrollState) {
            agenda.days.forEach {
                SessionsList(day = it, controller = controller)
            }
        }
    }
}

@Composable
private fun LiveHeader(liveSlot: TimeSlot, scrollToLive: () -> Unit) {
    Row(
        Modifier.fillMaxWidth().background(MaterialTheme.colors.grey5Black).height(52.dp)
            .clickable { scrollToLive() },
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            liveSlot.title,
            style = MaterialTheme.typography.h3.copy(color = MaterialTheme.colors.blackWhite),
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)
        )
        Spacer(Modifier.weight(1f))
        LiveIndicator()
    }
}

@OptIn(ExperimentalResourceApi::class)
private fun LazyListScope.SessionsList(
    day: Day,
    controller: AppController,
) {
    item(day.title.toString()) {
        AgendaDayHeader(day.day)
    }

    day.timeSlots.forEach { slot ->
        when {
            slot.isLunch -> {
                if (slot.isFinished) return@forEach
                item(slot.key) {
                    Break(
                        duration = slot.duration,
                        title = slot.title,
                        isLive = slot.isLive,
                        icon = Res.drawable.lunch,
                        liveIcon = Res.drawable.lunch_active
                    )
                }
            }

            slot.isBreak -> {
                if (slot.isFinished) return@forEach
                item(slot.key) {
                    Break(duration = slot.duration, title = slot.title, isLive = slot.isLive)
                }
            }

            slot.isParty -> {
                item(slot.key) {
                    Column {
                        AgendaTimeSlotHeader(slot.title, slot.isLive, slot.isFinished)
                        Party(slot.title, slot.isFinished)
                    }
                }
            }

            else -> {
                item(slot.key) {
                    AgendaTimeSlotHeader(
                        slot.title, slot.isLive, slot.isFinished
                    )
                }
            }
        }

        if (slot.isLunch || slot.isBreak || slot.isParty) return@forEach

        items(slot.sessions, key = { it.key }) { session ->
            AgendaItem(session.title,
                session.speakerLine,
                session.locationLine,
                session.badgeTimeLine,
                session.isFavorite,
                session.isFinished,
                session.isLightning,
                session.vote,
                onSessionClick = {
                    controller.showSession(session.id)
                },
                onFavoriteClick = {
                    controller.toggleFavorite(session.id)
                },
                onVote = {
                    controller.vote(session.id, it)
                }
            ) {
                controller.sendFeedback(session.id, it)
            }
        }
    }
}

private fun Day.itemsCount(): Int {
    val sessions =
        timeSlots.flatMap { it.sessions }.filterNot { (it.isLunch || it.isBreak || it.isParty) }
            .count()

    val slots = timeSlots.filterNot { (it.isLunch || it.isBreak || it.isParty) }.count()

    val lunches = timeSlots.filterNot { it.isFinished }.count { it.isLunch || it.isBreak }

    val party = 1
    val dayHeader = 1
    return sessions + slots + lunches + party + dayHeader
}

private fun Agenda.firstLiveIndex(): Int {
    var index = 0
    for (day in days) {
        index += 1 // Day header

        for (slot in day.timeSlots) {
            if (slot.isLive) return index

            if (slot.isFinished && (slot.isLunch || slot.isBreak)) {
                continue
            }

            index += slot.sessions.size + 1 // Time slot header
        }
    }

    return 0
}