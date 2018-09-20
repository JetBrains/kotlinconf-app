package org.jetbrains.kotlinconf.presentation

class MainPresenter(
    private val navigationManager: NavigationManager,
    private val repository: DataRepository
) {
    fun onCreate() {
        if (!repository.privacyPolicyAccepted) {
            navigationManager.showPrivacyPolicyDialog()
        }
    }
}