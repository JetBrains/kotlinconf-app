package org.jetbrains.kotlinconf.screens

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.StateFlow
import org.jetbrains.kotlinconf.ConferenceService
import org.jetbrains.kotlinconf.Theme

class SettingsViewModel(
    private val service: ConferenceService,
) : ViewModel() {
    val theme: StateFlow<Theme> = service.theme

    fun setTheme(theme: Theme) {
        service.setTheme(theme)
    }
}
