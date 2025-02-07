package org.jetbrains.kotlinconf.screens


import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinconfapp.shared.generated.resources.kodee_notifications
import kotlinconfapp.shared.generated.resources.notifications_description
import kotlinconfapp.shared.generated.resources.notifications_lets_get_started
import kotlinconfapp.shared.generated.resources.notifications_title
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource
import org.jetbrains.kotlinconf.NotificationSettings
import org.jetbrains.kotlinconf.ui.components.Button
import org.jetbrains.kotlinconf.ui.components.StyledText
import org.jetbrains.kotlinconf.ui.theme.KotlinConfTheme
import kotlinconfapp.shared.generated.resources.Res as AppRes

@Composable
fun StartNotificationsScreen(
    onDone: (NotificationSettings) -> Unit,
) {
    // TODO populate with real values https://github.com/JetBrains/kotlinconf-app/issues/252
    var notificationSettings by remember { mutableStateOf(NotificationSettings(false, false, false)) }

    Column(modifier = Modifier.fillMaxSize()) {
        Column(
            verticalArrangement = Arrangement.spacedBy(24.dp),
            modifier = Modifier
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 12.dp, vertical = 16.dp)
                .weight(1f)
        ) {
            Image(
                imageVector = vectorResource(AppRes.drawable.kodee_notifications),
                contentDescription = null,
                modifier = Modifier.fillMaxWidth()
                    .size(160.dp)
            )
            StyledText(
                stringResource(AppRes.string.notifications_title),
                style = KotlinConfTheme.typography.h1
            )
            StyledText(
                stringResource(AppRes.string.notifications_description),
                color = KotlinConfTheme.colors.longText,
            )
            NotificationSettings(notificationSettings, { notificationSettings = it })
        }

        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 16.dp)
        ) {
            Button(
                label = stringResource(AppRes.string.notifications_lets_get_started),
                onClick = { onDone(notificationSettings) },
                modifier = Modifier.weight(1f),
                primary = true,
            )
        }
    }
}
