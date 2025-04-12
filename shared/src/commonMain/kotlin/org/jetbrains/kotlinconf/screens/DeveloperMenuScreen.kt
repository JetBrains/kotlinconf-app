package org.jetbrains.kotlinconf.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import kotlinconfapp.shared.generated.resources.Res
import kotlinconfapp.shared.generated.resources.kodee_frightened
import kotlinconfapp.ui_components.generated.resources.arrow_left_24
import kotlinconfapp.ui_components.generated.resources.main_header_back
import kotlinx.coroutines.delay
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.kotlinconf.FlagsManager
import org.jetbrains.kotlinconf.LocalFlags
import org.jetbrains.kotlinconf.ui.components.Button
import org.jetbrains.kotlinconf.ui.components.Divider
import org.jetbrains.kotlinconf.ui.components.MainHeaderTitleBar
import org.jetbrains.kotlinconf.ui.components.SettingsItem
import org.jetbrains.kotlinconf.ui.components.Text
import org.jetbrains.kotlinconf.ui.components.TopMenuButton
import org.jetbrains.kotlinconf.ui.theme.KotlinConfTheme
import org.jetbrains.kotlinconf.utils.DebugLogger
import org.jetbrains.kotlinconf.utils.Logger
import org.jetbrains.kotlinconf.utils.bottomInsetPadding
import org.jetbrains.kotlinconf.utils.plus
import org.jetbrains.kotlinconf.utils.topInsetPadding
import org.koin.compose.koinInject
import kotlin.time.Duration.Companion.seconds
import kotlinconfapp.ui_components.generated.resources.Res as UiRes

@Composable
fun DeveloperMenuScreen(
    onBack: () -> Unit,
) {
    val realFlags = LocalFlags.current
    var flags by remember { mutableStateOf(realFlags) }
    var showWarning by remember { mutableStateOf(true) }

    val contentScrollState: ScrollState = rememberScrollState()

    Box(modifier = Modifier.fillMaxSize()) {
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
                        icon = UiRes.drawable.arrow_left_24,
                        contentDescription = stringResource(UiRes.string.main_header_back),
                        onClick = onBack,
                    )
                }
            )

            Divider(thickness = 1.dp, color = KotlinConfTheme.colors.strokePale)

            if (!showWarning) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier
                        .verticalScroll(contentScrollState)
                        .padding(12.dp)
                        .weight(1f)
                ) {
                    Text(
                        "Modifying any of these settings will move you from the production backend to the staging backend automatically.",
                        color = KotlinConfTheme.colors.primaryText,
                    )

                    SettingsItem(
                        title = "Enable back on main screens",
                        note = "Allow users to use back navigation between the top-level destinations on the Main screen",
                        enabled = flags.enableBackOnMainScreens,
                        onToggle = { flags = flags.copy(enableBackOnMainScreens = it) }
                    )

                    SettingsItem(
                        title = "Supports notifications",
                        note = "Whether this device should display notification settings",
                        enabled = flags.supportsNotifications,
                        onToggle = { flags = flags.copy(supportsNotifications = it) }
                    )

                    SettingsItem(
                        title = "Ripple enabled",
                        note = "Show ripple animations on tapped elements",
                        enabled = flags.rippleEnabled,
                        onToggle = { flags = flags.copy(rippleEnabled = it) }
                    )

                    SettingsItem(
                        title = "Redirect feedback to session page",
                        note = "Don't allow typing in detailed feedback on the schedule screen, redirect to the session screen for written responses instead",
                        enabled = flags.redirectFeedbackToSessionPage,
                        onToggle = { flags = flags.copy(redirectFeedbackToSessionPage = it) }
                    )

                    SettingsItem(
                        title = "Hide keyboard on drag",
                        note = "Hides the keyboard when the content is scrolled",
                        enabled = flags.hideKeyboardOnDrag,
                        onToggle = { flags = flags.copy(hideKeyboardOnDrag = it) }
                    )

                    SettingsItem(
                        title = "Use fake time (requires restart)",
                        note = "Simulate a date and time in the middle of the conference. Useful for testing voting and feedback features which are only available for sessions that already started. Fake time passes at 20x speed, so you'll see how the schedule changes and receive reminder notifications much quicker. Fake time restarts from the same point on every app start.",
                        enabled = flags.useFakeTime,
                        onToggle = { flags = flags.copy(useFakeTime = it) }
                    )

                    SettingsItem(
                        title = "Enable debug logging (requires restart)",
                        note = "Store logs in memory for debugging purposes. Logs can be copied to clipboard.",
                        enabled = flags.debugLogging,
                        onToggle = { flags = flags.copy(debugLogging = it) }
                    )
                    val debugLogger = koinInject<Logger>() as? DebugLogger
                    val clipboardManager = LocalClipboardManager.current
                    Button(
                        label = "Copy logs to clipboard",
                        onClick = {
                            debugLogger?.getAllLogs()?.let {
                                clipboardManager.setText(AnnotatedString(it))
                            }
                        },
                        modifier = Modifier.align(Alignment.CenterHorizontally),
                        primary = true,
                        enabled = flags.debugLogging && debugLogger != null,
                    )
                }

                Divider(thickness = 1.dp, color = KotlinConfTheme.colors.strokePale)

                val flagsManager = koinInject<FlagsManager>()
                Row(
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier.fillMaxWidth().padding(16.dp)
                ) {
                    Button(
                        label = "Reset all",
                        onClick = {
                            flagsManager.resetFlags()
                            onBack()
                        },
                        modifier = Modifier.weight(1f),
                        primary = false
                    )

                    Button(
                        label = "Save changes",
                        onClick = {
                            // Changes are already saved when toggles are changed
                            flagsManager.updateFlags(flags)
                            onBack()
                        },
                        modifier = Modifier.weight(1f),
                        primary = true,
                        enabled = flags != realFlags,
                    )
                }
            }
        }

        AnimatedVisibility(showWarning, modifier = Modifier.fillMaxSize()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(KotlinConfTheme.colors.mainBackground)
                    .padding(24.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Image(
                        painter = painterResource(Res.drawable.kodee_frightened),
                        contentDescription = null,
                        modifier = Modifier.align(Alignment.CenterHorizontally).padding(bottom = 12.dp)
                    )
                    Spacer(Modifier.height(24.dp))
                    Text(
                        text = "Warning!",
                        style = KotlinConfTheme.typography.h2.copy(textAlign = TextAlign.Center),
                        color = KotlinConfTheme.colors.primaryText,
                    )
                    Spacer(Modifier.height(24.dp))
                    Text(
                        text = "DO NOT USE THIS MENU while you're trying to use the app at the conference. The Developer Menu is available only for testing purposes. Changing settings here may lead to data loss, unpredictable behavior, bugs, and making Kodee sad. ",
                        style = KotlinConfTheme.typography.text1.copy(textAlign = TextAlign.Center),
                        color = KotlinConfTheme.colors.primaryText,
                        modifier = Modifier.widthIn(max = 440.dp)
                    )
                    Spacer(Modifier.height(24.dp))

                    val enabled by produceState(false) {
                        delay(3.seconds)
                        value = true
                    }
                    Button(
                        label = "Proceed",
                        onClick = { showWarning = false },
                        modifier = Modifier.width(200.dp),
                        primary = true,
                        enabled = enabled,
                    )
                    Spacer(Modifier.height(8.dp))
                    Button(
                        label = "Go back",
                        onClick = { onBack() },
                        modifier = Modifier.width(200.dp),
                        primary = false,
                        enabled = enabled,
                    )
                }
            }
        }
    }
}
