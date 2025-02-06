package org.jetbrains.kotlinconf.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.jetbrains.kotlinconf.ConferenceService

class PrivacyPolicyViewModel(
    private val service: ConferenceService,
) : ViewModel() {
    private val _policyAccepted = MutableStateFlow(false)
    val policyAccepted: StateFlow<Boolean> = _policyAccepted.asStateFlow()

    fun acceptPrivacyPolicy() {
        viewModelScope.launch {
            service.acceptPrivacyPolicy()
            _policyAccepted.value = true
        }
    }
}
