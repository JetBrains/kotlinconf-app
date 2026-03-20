package org.jetbrains.kotlinconf.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesIntoMap
import dev.zacsweers.metrox.viewmodel.ViewModelKey
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import org.jetbrains.kotlinconf.ConferenceService
import org.jetbrains.kotlinconf.MapData
import org.jetbrains.kotlinconf.utils.ErrorLoadingState
import org.jetbrains.kotlinconf.utils.Logger
import org.jetbrains.kotlinconf.utils.tagged

data class MapContent(
    val mapData: MapData,
    val svgsByPath: Map<String, String>,
)

@ContributesIntoMap(AppScope::class)
@ViewModelKey
class MapViewModel(
    private val service: ConferenceService,
    logger: Logger
) : ViewModel() {
    private var loading = MutableStateFlow(false)

    private val taggedLogger = logger.tagged("MapViewModel")

    fun refresh() {
        viewModelScope.launch {
            taggedLogger.log { "Loading..." }
            loading.value = true
            try {
                taggedLogger.log { "Download started" }
                service.loadConferenceData()
                service.downloadAllAssets()
                taggedLogger.log { "Download done" }
            } finally {
                taggedLogger.log { "Loading complete" }
                loading.value = false
            }
        }
    }

    val useNativeNavigation get() = service.isExternalNavigation()

    val state: StateFlow<ErrorLoadingState<MapContent>> = combine(
        service.mapData, loading,
    ) { mapData, loading ->
        when {
            loading -> ErrorLoadingState.Loading
            mapData == null -> ErrorLoadingState.Error
            else -> {
                val allSvgPaths = buildList {
                    mapData.floors.forEach {
                        add(it.svgPathLight)
                        add(it.svgPathDark)
                    }
                }
                val svgFilesByPath = allSvgPaths.associateWith { service.getAsset(it) }

                if (svgFilesByPath.values.any { it == null }) {
                    ErrorLoadingState.Error
                } else {
                    @Suppress("UNCHECKED_CAST")
                    ErrorLoadingState.Content(
                        MapContent(
                            mapData = mapData,
                            svgsByPath = svgFilesByPath as Map<String, String>,
                        )
                    )
                }
            }
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), ErrorLoadingState.Loading)
}
