package org.jetbrains.kotlinconf.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import org.jetbrains.kotlinconf.ConferenceService
import org.jetbrains.kotlinconf.utils.ErrorLoadingState
import org.koin.core.annotation.InjectedParam
import org.koin.core.annotation.KoinViewModel

@KoinViewModel
class DocumentsViewModel(
    private val service: ConferenceService,
    @InjectedParam private val documentPath: String,
) : ViewModel() {
    private var loading = MutableStateFlow(true)
    private var document = MutableStateFlow<String?>(null)

    init {
        refresh()
    }

    fun refresh() {
        viewModelScope.launch {
            loading.value = true
            try {
                document.value = service.getAsset(documentPath)
            } finally {
                loading.value = false
            }
        }
    }

    val state: StateFlow<ErrorLoadingState<String>> = combine(
        document, loading
    ) { doc, loading ->
        when {
            loading -> ErrorLoadingState.Loading
            doc != null -> ErrorLoadingState.Content(doc)
            else -> ErrorLoadingState.Error
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), ErrorLoadingState.Loading)
}
