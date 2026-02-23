package org.jetbrains.kotlinconf.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.Assisted
import dev.zacsweers.metro.AssistedFactory
import dev.zacsweers.metro.AssistedInject
import dev.zacsweers.metro.ContributesIntoMap
import dev.zacsweers.metrox.viewmodel.ManualViewModelAssistedFactory
import dev.zacsweers.metrox.viewmodel.ManualViewModelAssistedFactoryKey
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import org.jetbrains.kotlinconf.ConferenceService
import org.jetbrains.kotlinconf.DocumentState

@AssistedInject
class DocumentsViewModel(
    private val service: ConferenceService,
    @Assisted private val documentName: String,
) : ViewModel() {
    private var loading = MutableStateFlow(false)

    val state = combine(
        service.getDocument(documentName),
        loading
    ) { doc, loading ->
        when {
            loading -> DocumentState.Loading
            doc == null -> DocumentState.Error
            else -> DocumentState.Success(doc)
        }
    }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), DocumentState.Error)

    fun refresh() {
        viewModelScope.launch {
            loading.value = true
            try {
                service.downloadDocument(documentName)
            } finally {
                loading.value = false
            }
        }
    }

    @AssistedFactory
    @ManualViewModelAssistedFactoryKey(Factory::class)
    @ContributesIntoMap(AppScope::class)
    fun interface Factory : ManualViewModelAssistedFactory {
        fun create(documentName: String): DocumentsViewModel
    }
}
