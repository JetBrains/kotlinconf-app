package org.jetbrains.kotlinconf.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import kotlinconfapp.shared.generated.resources.Res
import kotlinconfapp.shared.generated.resources.notifications_jetbrains_news_description
import kotlinconfapp.shared.generated.resources.notifications_jetbrains_news_title
import kotlinconfapp.shared.generated.resources.notifications_kotlinconf_news_description
import kotlinconfapp.shared.generated.resources.notifications_kotlinconf_news_title
import kotlinconfapp.shared.generated.resources.notifications_schedule_update_description
import kotlinconfapp.shared.generated.resources.notifications_schedule_update_title
import kotlinconfapp.shared.generated.resources.notifications_session_reminders_description
import kotlinconfapp.shared.generated.resources.notifications_session_reminders_title
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.kotlinconf.NotificationSettings
import org.jetbrains.kotlinconf.ui.components.SettingsItem


@Composable
fun NotificationSettings(
    notificationSettings: NotificationSettings,
    onChangeSettings: (NotificationSettings) -> Unit,
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        SettingsItem(
            title = stringResource(Res.string.notifications_session_reminders_title),
            enabled = notificationSettings.sessionReminders,
            onToggle = { enabled -> onChangeSettings(notificationSettings.copy(sessionReminders = enabled)) },
            note = stringResource(Res.string.notifications_session_reminders_description),
        )
        SettingsItem(
            title = stringResource(Res.string.notifications_schedule_update_title),
            enabled = notificationSettings.scheduleUpdates,
            onToggle = { enabled -> onChangeSettings(notificationSettings.copy(scheduleUpdates = enabled)) },
            note = stringResource(Res.string.notifications_schedule_update_description),
        )
        SettingsItem(
            title = stringResource(Res.string.notifications_kotlinconf_news_title),
            enabled = notificationSettings.kotlinConfNews,
            onToggle = { enabled -> onChangeSettings(notificationSettings.copy(kotlinConfNews = enabled)) },
            note = stringResource(Res.string.notifications_kotlinconf_news_description),
        )
        SettingsItem(
            title = stringResource(Res.string.notifications_jetbrains_news_title),
            enabled = notificationSettings.jetBrainsNews,
            onToggle = { enabled -> onChangeSettings(notificationSettings.copy(jetBrainsNews = enabled)) },
            note = stringResource(Res.string.notifications_jetbrains_news_description),
        )
    }
}
