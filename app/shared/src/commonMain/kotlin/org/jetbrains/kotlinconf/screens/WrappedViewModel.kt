package org.jetbrains.kotlinconf.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesIntoMap
import dev.zacsweers.metrox.viewmodel.ViewModelKey
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.stateIn
import org.jetbrains.kotlinconf.ConferenceService
import org.jetbrains.kotlinconf.TimeProvider
import org.jetbrains.kotlinconf.wrapped.WrappedStats
import org.jetbrains.kotlinconf.wrapped.computeWrappedStats

@ContributesIntoMap(AppScope::class)
@ViewModelKey
class WrappedViewModel(
    service: ConferenceService,
    timeProvider: TimeProvider,
) : ViewModel() {
    /**
     * The Wrapped summary, or null while the conference data is still loading.
     */
    val stats: StateFlow<WrappedStats?> = combine(
        service.agenda,
        service.speakers,
        service.conferenceInfo,
        service.usageStats,
        service.currentYear,
    ) { agenda, speakers, conferenceInfo, usage, year ->
        if (agenda.isEmpty() || year == null) return@combine null

        computeWrappedStats(
            year = year,
            sessions = agenda.flatMap { day -> day.timeSlots.flatMap { it.sessions } },
            speakers = speakers,
            tagInfo = conferenceInfo?.tags,
            usage = usage,
            now = timeProvider.now(),
        )
    }
        .flowOn(Dispatchers.Default)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)
}
