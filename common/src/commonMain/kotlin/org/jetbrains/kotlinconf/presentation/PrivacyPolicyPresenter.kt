package org.jetbrains.kotlinconf.presentation

class PrivacyPolicyPresenter(
    private val repository: DataRepository
) {
    fun onAcceptPrivacyPolicyClicked() {
        repository.privacyPolicyAccepted = true
    }
}