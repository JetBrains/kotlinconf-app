package org.jetbrains.kotlinconf.ui.welcome

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue

@Composable
fun WelcomeScreen(
    onAcceptPrivacy: () -> Unit,
    onRejectPrivacy: () -> Unit,
    onAcceptNotifications: () -> Unit,
    onClose: () -> Unit
) {
    var step by remember { mutableStateOf(0) }
    var showFullPrivacyPolicy by remember { mutableStateOf(false) }

    if (showFullPrivacyPolicy) {
        WelcomeFullPrivacyPolicyScreen(onAccept = {
            onAcceptPrivacy()
        }, onClose = {
            onRejectPrivacy()
            showFullPrivacyPolicy = false
            step += 1
        }, onDismiss = {
            showFullPrivacyPolicy = false
        })
    } else if (step == 0) {
        WelcomePrivacyPolicyScreen(onAcceptPrivacy, showDetails = {
            showFullPrivacyPolicy = true
        }) {
            onRejectPrivacy()
            step += 1
        }
    } else {
        WelcomeNotificationsScreen(onAcceptNotifications) {
            onClose()
        }
    }
}

