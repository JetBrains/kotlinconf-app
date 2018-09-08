package org.jetbrains.kotlinconf.ui

interface NavigationManager {
    fun showSessionList()
    fun showSessionDetails(sessionId: String)
    fun removeCodePromptFragment()
    fun showCodeEnterFragment()
}