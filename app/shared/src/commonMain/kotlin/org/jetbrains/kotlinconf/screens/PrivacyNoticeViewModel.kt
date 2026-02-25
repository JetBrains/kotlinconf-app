package org.jetbrains.kotlinconf.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesIntoMap
import dev.zacsweers.metrox.viewmodel.ViewModelKey
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import org.jetbrains.kotlinconf.ConferenceService
import org.jetbrains.kotlinconf.utils.ErrorLoadingState

sealed class PrivacyNoticeState {
    object Idle : PrivacyNoticeState()
    object Loading : PrivacyNoticeState()
    object Done : PrivacyNoticeState()
}

@ContributesIntoMap(AppScope::class)
@ViewModelKey(PrivacyNoticeViewModel::class)
class PrivacyNoticeViewModel(
    private val service: ConferenceService,
) : ViewModel() {
    private val _state = MutableStateFlow<PrivacyNoticeState>(PrivacyNoticeState.Idle)
    val state: StateFlow<PrivacyNoticeState> = _state.asStateFlow()

    private var documentLoading = MutableStateFlow(true)
    private var document = MutableStateFlow<String?>(null)

    val documentState: StateFlow<ErrorLoadingState<String>> = combine(
        document, documentLoading
    ) { doc, loading ->
        when {
            loading -> ErrorLoadingState.Loading
            doc != null -> ErrorLoadingState.Content(doc)
            else -> ErrorLoadingState.Error
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), ErrorLoadingState.Loading)

    init {
        refreshDocument()
    }

    fun refreshDocument() {
        viewModelScope.launch {
            documentLoading.value = true
            try {
                document.value = service.getFile("documents/app-privacy-notice.md")
            } finally {
                documentLoading.value = false
            }
        }
    }

    fun acceptPrivacyNotice(confirmationRequired: Boolean) {
        viewModelScope.launch {
            _state.value = PrivacyNoticeState.Loading

            _state.value = if (confirmationRequired) {
                if (service.acceptPrivacyNotice()) {
                    PrivacyNoticeState.Done
                } else {
                    PrivacyNoticeState.Idle
                }
            } else {
                service.acceptPrivacyNoticeAsync()
                PrivacyNoticeState.Done
            }
        }
    }
}
