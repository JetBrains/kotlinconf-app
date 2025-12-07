package org.jetbrains.kotlinconf.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesIntoMap
import dev.zacsweers.metro.Inject
import dev.zacsweers.metrox.viewmodel.ViewModelKey
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import org.jetbrains.kotlinconf.ConferenceService
import org.jetbrains.kotlinconf.NotificationSettings
import org.jetbrains.kotlinconf.Theme

@Inject
@ContributesIntoMap(AppScope::class)
@ViewModelKey(SettingsViewModel::class)
class SettingsViewModel(
    private val service: ConferenceService,
) : ViewModel() {
    val theme: StateFlow<Theme> = service.getTheme()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), Theme.SYSTEM)

    val notificationSettings: StateFlow<NotificationSettings?> = service.getNotificationSettings()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    fun setTheme(theme: Theme) {
        service.setTheme(theme)
    }

    fun setNotificationSettings(settings: NotificationSettings) {
        viewModelScope.launch {
            service.setNotificationSettings(settings)
            if (settings.hasAnyEnabled()) {
                service.requestNotificationPermissions()
            }
        }
    }
}
