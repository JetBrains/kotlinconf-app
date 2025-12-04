package org.jetbrains.kotlinconf.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.jetbrains.kotlinconf.ConferenceService

sealed class PrivacyNoticeState {
    object Idle : PrivacyNoticeState()
    object Loading : PrivacyNoticeState()
    object Done : PrivacyNoticeState()
}

class PrivacyNoticeViewModel(
    private val service: ConferenceService,
) : ViewModel() {
    private val _state = MutableStateFlow<PrivacyNoticeState>(PrivacyNoticeState.Idle)
    val state: StateFlow<PrivacyNoticeState> = _state.asStateFlow()

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
