package org.jetbrains.kotlinconf.screens

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinconfapp.ui_components.generated.resources.Res
import kotlinconfapp.ui_components.generated.resources.arrow_left_24
import kotlinconfapp.ui_components.generated.resources.main_header_back
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.kotlinconf.FlagsManager
import org.jetbrains.kotlinconf.LocalFlags
import org.jetbrains.kotlinconf.ui.components.Button
import org.jetbrains.kotlinconf.ui.components.Divider
import org.jetbrains.kotlinconf.ui.components.MainHeaderTitleBar
import org.jetbrains.kotlinconf.ui.components.SettingsItem
import org.jetbrains.kotlinconf.ui.components.TopMenuButton
import org.jetbrains.kotlinconf.ui.theme.KotlinConfTheme
import org.jetbrains.kotlinconf.utils.bottomInsetPadding
import org.jetbrains.kotlinconf.utils.plus
import org.jetbrains.kotlinconf.utils.topInsetPadding
import org.koin.compose.koinInject

@Composable
fun DeveloperMenuScreen(
    onBack: () -> Unit,
) {
    val flags = LocalFlags.current
    val flagsManager = koinInject<FlagsManager>()

    val contentScrollState: ScrollState = rememberScrollState()
    Column(
        Modifier
            .fillMaxSize()
            .background(color = KotlinConfTheme.colors.mainBackground)
            .padding(topInsetPadding() + bottomInsetPadding())
    ) {
        MainHeaderTitleBar(
            title = "Developer Menu",
            startContent = {
                TopMenuButton(
                    icon = Res.drawable.arrow_left_24,
                    contentDescription = stringResource(Res.string.main_header_back),
                    onClick = onBack,
                )
            }
        )

        Divider(thickness = 1.dp, color = KotlinConfTheme.colors.strokePale)

        Column(
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier
                .verticalScroll(contentScrollState)
                .padding(12.dp)
                .weight(1f)
        ) {
            SettingsItem(
                title = "Enable back on main screens",
                note = "Allow users to use back navigation between the top-level destinations on the Main screen",
                enabled = flags.enableBackOnMainScreens,
                onToggle = {
                    flagsManager.updateFlags(flags.copy(enableBackOnMainScreens = it))
                }
            )

            SettingsItem(
                title = "Supports notifications",
                note = "Whether this device should display notification settings",
                enabled = flags.supportsNotifications,
                onToggle = {
                    flagsManager.updateFlags(flags.copy(supportsNotifications = it))
                }
            )

            SettingsItem(
                title = "Ripple enabled",
                note = "Show ripple animations on tapped elements",
                enabled = flags.rippleEnabled,
                onToggle = {
                    flagsManager.updateFlags(flags.copy(rippleEnabled = it))
                }
            )

            SettingsItem(
                title = "Redirect feedback to session page",
                note = "Don't allow typing in detailed feedback on the schedule screen, redirect to the session screen for written responses instead",
                enabled = flags.redirectFeedbackToSessionPage,
                onToggle = {
                    flagsManager.updateFlags(flags.copy(redirectFeedbackToSessionPage = it))
                }
            )

            SettingsItem(
                title = "Hide keyboard on drag",
                note = "Hides the keyboard when the content is scrolled",
                enabled = flags.hideKeyboardOnDrag,
                onToggle = {
                    flagsManager.updateFlags(flags.copy(hideKeyboardOnDrag = it))
                }
            )

            SettingsItem(
                title = "Use fake time (requires app restart)",
                note = "Simulate a date and time in the middle of the conference. Useful for testing voting and feedback features which are only available for sessions that already started. Fake time passes at 20x speed, so you'll see how the schedule changes and receive reminder notifications much quicker. Fake time restarts from the same point on every app start.",
                enabled = flags.useFakeTime,
                onToggle = {
                    flagsManager.updateFlags(flags.copy(useFakeTime = it))
                }
            )
        }

        Divider(thickness = 1.dp, color = KotlinConfTheme.colors.strokePale)

        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.fillMaxWidth().padding(16.dp)
        ) {
            Button(
                label = "Reset to Platform Defaults",
                onClick = { flagsManager.resetFlags() },
                modifier = Modifier.fillMaxWidth(),
                primary = true
            )
        }
    }
}
