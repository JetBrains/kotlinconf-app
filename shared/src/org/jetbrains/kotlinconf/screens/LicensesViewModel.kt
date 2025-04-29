package org.jetbrains.kotlinconf.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mikepenz.aboutlibraries.Libs
import com.mikepenz.aboutlibraries.entity.Library
import kotlinconfapp.shared.generated.resources.Res
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.stateIn
import org.jetbrains.kotlinconf.utils.performSearch

data class LibraryWithHighlights(
    val library: Library,
    val nameHighlights: List<IntRange> = emptyList(),
    val authorHighlights: List<IntRange> = emptyList(),
    val licenseHighlights: List<IntRange> = emptyList(),
)

class LicensesViewModel : ViewModel() {
    private var searchText = MutableStateFlow("")
    private val libraries = flow {
        Libs.Builder()
            .withJson(Res.readBytes("files/aboutlibraries.json").decodeToString())
            .build()
            .libraries
            .sortedBy { it.name }
            .let { emit(it) }
    }

    fun setSearchText(searchText: String) {
        this.searchText.value = searchText
    }

    private val Library.author: String
        get() = when {
            developers.isNotEmpty() -> developers.joinToString { it.name.toString() }
            else -> organization?.name ?: ""
        }

    private val Library.licenseName: String
        get() = licenses.firstOrNull()?.name ?: "Unknown license"

    val licensesState: StateFlow<List<LibraryWithHighlights>> = combine(
        libraries, searchText
    ) { libs, searchText ->
        if (searchText.isBlank()) {
            libs.map { LibraryWithHighlights(it) }
        } else {
            libs
                .performSearch(
                    searchText = searchText,
                    produceResult = { lib, (nameMatches, authorMatches, licenseMatches) ->
                        LibraryWithHighlights(lib, nameMatches, authorMatches, licenseMatches)
                    },
                    selectors = listOf({ it.name }, { it.author }, { it.licenseName }),
                )
                .sortedBy { it.library.name }
        }
    }
        .flowOn(Dispatchers.Default)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
}
