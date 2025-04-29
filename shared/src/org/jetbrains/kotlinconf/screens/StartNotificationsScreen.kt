package org.jetbrains.kotlinconf.screens


import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinconfapp.shared.generated.resources.kodee_notifications
import kotlinconfapp.shared.generated.resources.notifications_description
import kotlinconfapp.shared.generated.resources.notifications_lets_get_started
import kotlinconfapp.shared.generated.resources.notifications_title
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource
import org.jetbrains.kotlinconf.ui.components.Button
import org.jetbrains.kotlinconf.ui.components.Text
import org.jetbrains.kotlinconf.ui.theme.KotlinConfTheme
import org.koin.compose.viewmodel.koinViewModel
import kotlinconfapp.shared.generated.resources.Res as AppRes

@Composable
fun StartNotificationsScreen(
    onDone: () -> Unit,
    viewModel: StartNotificationsViewModel = koinViewModel(),
) {
    val notificationSettings = viewModel.notificationSettings.collectAsStateWithLifecycle().value

    LaunchedEffect(Unit) {
        viewModel.permissionHandlingDone.collect { done ->
            if (done) onDone()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(color = KotlinConfTheme.colors.mainBackground)
            .windowInsetsPadding(WindowInsets.safeDrawing)
    ) {
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
            Text(
                stringResource(AppRes.string.notifications_title),
                style = KotlinConfTheme.typography.h1,
                modifier = Modifier.semantics {
                    heading()
                }
            )
            Text(
                stringResource(AppRes.string.notifications_description),
                color = KotlinConfTheme.colors.longText,
            )
            if (notificationSettings != null) {
                NotificationSettings(
                    notificationSettings = notificationSettings,
                    onChangeSettings = { viewModel.setNotificationSettings(it) }
                )
            }
        }

        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 16.dp)
        ) {
            Button(
                label = stringResource(AppRes.string.notifications_lets_get_started),
                onClick = {
                    viewModel.requestNotificationPermissions()
                },
                modifier = Modifier.weight(1f),
                primary = true,
            )
        }
    }
}
