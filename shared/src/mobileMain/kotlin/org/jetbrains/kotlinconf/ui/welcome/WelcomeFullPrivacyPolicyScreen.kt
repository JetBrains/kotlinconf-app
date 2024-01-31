package org.jetbrains.kotlinconf.ui.welcome

import androidx.compose.runtime.Composable
import org.jetbrains.kotlinconf.ui.PrivacyPolicyScreen


@Composable
fun WelcomeFullPrivacyPolicyScreen(
    onAccept: () -> Unit,
    onClose: () -> Unit,
    onDismiss: () -> Unit
) {
    FormWithButtons(
        onAccept = {
            onAccept()
            onClose()
        }, onReject = onClose
    ) {
        PrivacyPolicyScreen(onDismiss)
    }
}