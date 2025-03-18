package org.jetbrains.kotlinconf.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.jetbrains.kotlinconf.ConferenceService

sealed class PrivacyPolicyState {
    object Idle : PrivacyPolicyState()
    object Loading : PrivacyPolicyState()
    object Done : PrivacyPolicyState()
}

class PrivacyPolicyViewModel(
    private val service: ConferenceService,
) : ViewModel() {
    private val _policyState = MutableStateFlow<PrivacyPolicyState>(PrivacyPolicyState.Idle)
    val policyState: StateFlow<PrivacyPolicyState> = _policyState.asStateFlow()

    fun acceptPrivacyPolicy(confirmationRequired: Boolean) {
        viewModelScope.launch {
            _policyState.value = PrivacyPolicyState.Loading

            _policyState.value = if (confirmationRequired) {
                if (service.acceptPrivacyPolicy()) {
                    PrivacyPolicyState.Done
                } else {
                    PrivacyPolicyState.Idle
                }
            } else {
                service.acceptPrivacyPolicyAsync()
                PrivacyPolicyState.Done
            }
        }
    }
}
