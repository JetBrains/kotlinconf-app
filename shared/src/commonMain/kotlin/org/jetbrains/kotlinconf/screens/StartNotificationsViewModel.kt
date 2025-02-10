package org.jetbrains.kotlinconf.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import org.jetbrains.kotlinconf.ConferenceService
import org.jetbrains.kotlinconf.NotificationSettings

class StartNotificationsViewModel(
    private val service: ConferenceService,
) : ViewModel() {
    val notificationSettings: StateFlow<NotificationSettings> = service.getNotificationSettings()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), NotificationSettings())

    fun setNotificationSettings(settings: NotificationSettings) {
        service.setNotificationSettings(settings)
    }

    fun requestNotificationPermissions() {
        service.requestNotificationPermissions()
    }
}
