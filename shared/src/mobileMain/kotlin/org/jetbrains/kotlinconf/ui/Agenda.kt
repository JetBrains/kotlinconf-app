package org.jetbrains.kotlinconf.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import kotlinx.coroutines.launch
import org.jetbrains.kotlinconf.Agenda
import org.jetbrains.kotlinconf.Day
import org.jetbrains.kotlinconf.org.jetbrains.kotlinconf.AppController
import org.jetbrains.kotlinconf.ui.components.AgendaDayHeader
import org.jetbrains.kotlinconf.ui.components.AgendaItem
import org.jetbrains.kotlinconf.ui.components.AgendaTimeSlotHeader
import org.jetbrains.kotlinconf.ui.components.Break
import org.jetbrains.kotlinconf.ui.components.Party

@Composable
fun AgendaView(agenda: Agenda, controller: AppController) {
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()
    var selected: String? by remember { mutableStateOf(agenda.days.firstOrNull()?.title) }

    val daysIndex = listOf(0, if (agenda.days.isNotEmpty()) agenda.days.first().itemsCount() else 0)

    Column {
        TabBar(
            agenda.days.map { it.title },
            selected, onSelect = { title ->
                selected = title
                val index = daysIndex[agenda.days.indexOfFirst { it.title == title }]
                if (index >= 0) {
                    coroutineScope.launch { listState.scrollToItem(index, 0) }
                }
            }
        )
        LazyColumn(state = listState) {
            agenda.days.forEach {
                SessionsList(day = it, controller = controller)
            }
        }
    }

    LaunchedEffect("scrollToLive") {
        listState.scrollToItem(agenda.firstLiveIndex(), 0)
    }
}

private fun LazyListScope.SessionsList(
    day: Day,
    controller: AppController,
) {
    item {
        AgendaDayHeader(day.title)
    }

    day.timeSlots.forEach { slot ->
        when {
            slot.isLunch -> {
                if (slot.isFinished) return@forEach
                item("break-${slot.id}") {
                    Break(
                        duration = slot.duration,
                        title = slot.title,
                        isLive = slot.isLive,
                        icon = "lunch",
                        icon_live = "lunch_active"
                    )
                }
            }
            slot.isBreak -> {
                if (slot.isFinished) return@forEach
                item("break-${slot.id}") {
                    Break(duration = slot.duration, title = slot.title, isLive = slot.isLive)
                }
            }
            slot.isParty -> {
                item("party-${slot.id}") {
                    Column {
                        AgendaTimeSlotHeader(slot.title, slot.isLive, slot.isFinished)
                        Party(slot.isFinished)
                    }
                }
            }
            else -> {
                item("time-header-${slot.id}") {
                    AgendaTimeSlotHeader(
                        slot.title,
                        slot.isLive,
                        slot.isFinished
                    )
                }
            }
        }

        if (slot.isLunch || slot.isBreak || slot.isParty) return@forEach

        items(slot.sessions, key = { it.id }) { session ->
            AgendaItem(
                session.title,
                session.speakerLine,
                session.locationLine,
                session.badgeTimeLine,
                session.isFavorite,
                session.isFinished,
                session.isLightning,
                session.isCodeLab,
                session.isAWSLab,
                session.vote,
                onSessionClick = {
                    controller.showSession(session.id)
                },
                onFavoriteClick = {
                    controller.toggleFavorite(session.id)
                },
                onVote = {
                    controller.vote(session.id, it)
                },
                onFeedback = {
                    controller.sendFeedback(session.id, it)
                }
            )
        }
    }
}

private fun Day.itemsCount(): Int {
    val sessions = timeSlots
        .flatMap { it.sessions }
        .filterNot { (it.isLunch || it.isBreak || it.isParty) }
        .count()

    val slots = timeSlots
        .filterNot { (it.isLunch || it.isBreak || it.isParty) }
        .count()

    val lunches = timeSlots
        .filterNot { it.isFinished }.count { it.isLunch || it.isBreak }

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