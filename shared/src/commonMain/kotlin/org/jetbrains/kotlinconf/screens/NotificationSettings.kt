package org.jetbrains.kotlinconf.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.unit.dp
import kotlinconfapp.shared.generated.resources.Res
import kotlinconfapp.shared.generated.resources.notifications_jetbrains_news_description
import kotlinconfapp.shared.generated.resources.notifications_jetbrains_news_title
import kotlinconfapp.shared.generated.resources.notifications_kotlinconf_news_description
import kotlinconfapp.shared.generated.resources.notifications_kotlinconf_news_title
import kotlinconfapp.shared.generated.resources.notifications_schedule_update_description
import kotlinconfapp.shared.generated.resources.notifications_schedule_update_title
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.kotlinconf.NotificationSettings
import org.jetbrains.kotlinconf.ui.components.SettingsItem

class NotificationSettingsState {
    var scheduleUpdates by mutableStateOf(true)
    var kotlinConfNews by mutableStateOf(true)
    var jetbrainsNews by mutableStateOf(true)

    val model: NotificationSettings
        @Composable
        get() = NotificationSettings(
            scheduleUpdates = scheduleUpdates,
            kotlinConfNews = kotlinConfNews,
            jetbrainsNews = jetbrainsNews,
        )
}

@Composable
fun rememberNotificationSettingsState() = remember { NotificationSettingsState() }

@Composable
fun NotificationSettings(notificationSettingsState: NotificationSettingsState) {
    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        SettingsItem(
            title = stringResource(Res.string.notifications_schedule_update_title),
            enabled = notificationSettingsState.scheduleUpdates,
            onToggle = { notificationSettingsState.scheduleUpdates = it },
            note = stringResource(Res.string.notifications_schedule_update_description),
        )
        SettingsItem(
            title = stringResource(Res.string.notifications_kotlinconf_news_title),
            enabled = notificationSettingsState.kotlinConfNews,
            onToggle = { notificationSettingsState.kotlinConfNews = it },
            note = stringResource(Res.string.notifications_kotlinconf_news_description),
        )
        SettingsItem(
            title = stringResource(Res.string.notifications_jetbrains_news_title),
            enabled = notificationSettingsState.jetbrainsNews,
            onToggle = { notificationSettingsState.jetbrainsNews = it },
            note = stringResource(Res.string.notifications_jetbrains_news_description),
        )
    }
}
