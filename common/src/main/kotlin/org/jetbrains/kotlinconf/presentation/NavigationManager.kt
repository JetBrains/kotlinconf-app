package org.jetbrains.kotlinconf.presentation

interface NavigationManager {
    fun showSessionList()
    fun showSessionDetails(sessionId: String)
    fun showPrivacyPolicyDialog()
}