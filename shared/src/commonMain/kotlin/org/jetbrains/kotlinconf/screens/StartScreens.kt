package org.jetbrains.kotlinconf.screens

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinconfapp.shared.generated.resources.kodee_notifications
import kotlinconfapp.shared.generated.resources.kodee_privacy
import kotlinconfapp.shared.generated.resources.notifications_description
import kotlinconfapp.shared.generated.resources.notifications_lets_get_started
import kotlinconfapp.shared.generated.resources.notifications_title
import kotlinconfapp.shared.generated.resources.privacy_policy_accept
import kotlinconfapp.shared.generated.resources.privacy_policy_back
import kotlinconfapp.shared.generated.resources.privacy_policy_description
import kotlinconfapp.shared.generated.resources.privacy_policy_read_action
import kotlinconfapp.shared.generated.resources.privacy_policy_reject
import kotlinconfapp.shared.generated.resources.privacy_policy_title
import kotlinconfapp.ui_components.generated.resources.Res
import kotlinconfapp.ui_components.generated.resources.arrow_left_24
import kotlinconfapp.ui_components.generated.resources.arrow_right_24
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource
import org.jetbrains.kotlinconf.NotificationSettings
import org.jetbrains.kotlinconf.ui.components.Action
import org.jetbrains.kotlinconf.ui.components.ActionSize
import org.jetbrains.kotlinconf.ui.components.Button
import org.jetbrains.kotlinconf.ui.components.Divider
import org.jetbrains.kotlinconf.ui.components.MainHeaderTitleBar
import org.jetbrains.kotlinconf.ui.components.MarkdownView
import org.jetbrains.kotlinconf.ui.components.StyledText
import org.jetbrains.kotlinconf.ui.components.TopMenuButton
import org.jetbrains.kotlinconf.ui.theme.KotlinConfTheme
import kotlinconfapp.shared.generated.resources.Res as AppRes


@Composable
fun StartPrivacyPolicyScreen(
    onRejectPolicy: () -> Unit,
    onAcceptPolicy: () -> Unit,
) {
    var detailsVisible by remember { mutableStateOf(false) }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        AnimatedContent(
            targetState = detailsVisible,
            modifier = Modifier.weight(1f),
            transitionSpec = {
                fadeIn(animationSpec = tween(100, delayMillis = 90))
                    .togetherWith(fadeOut(animationSpec = tween(90)))
            }
        ) { detailsVis ->
            if (detailsVis) {
                Column {
                    MainHeaderTitleBar(
                        stringResource(AppRes.string.privacy_policy_title),
                        startContent = {
                            TopMenuButton(
                                icon = Res.drawable.arrow_left_24,
                                contentDescription = stringResource(AppRes.string.privacy_policy_back),
                                onClick = { detailsVisible = false },
                            )
                        }
                    )
                    Divider(
                        thickness = 1.dp,
                        color = KotlinConfTheme.colors.strokePale,
                    )
                    MarkdownView(
                        loadText = {
                            @OptIn(ExperimentalResourceApi::class)
                            AppRes.readBytes("files/app-privacy-policy.md")
                        },
                        modifier = Modifier.padding(horizontal = 12.dp).verticalScroll(rememberScrollState())
                    )
                    Spacer(Modifier.weight(1f))
                    Divider(
                        thickness = 1.dp,
                        color = KotlinConfTheme.colors.strokePale,
                    )
                }
            } else {
                Column(
                    verticalArrangement = Arrangement.spacedBy(24.dp),
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 16.dp)
                ) {
                    Image(
                        imageVector = vectorResource(AppRes.drawable.kodee_privacy),
                        contentDescription = null,
                        modifier = Modifier.fillMaxWidth()
                            .size(160.dp)
                    )
                    StyledText(
                        stringResource(AppRes.string.privacy_policy_title),
                        style = KotlinConfTheme.typography.h1
                    )
                    StyledText(
                        stringResource(AppRes.string.privacy_policy_description),
                        color = KotlinConfTheme.colors.longText,
                    )
                    Action(
                        stringResource(AppRes.string.privacy_policy_read_action),
                        icon = Res.drawable.arrow_right_24,
                        size = ActionSize.Large,
                        enabled = true,
                        onClick = { detailsVisible = true }
                    )
                }
                Spacer(Modifier.weight(1f))
            }
        }

        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 16.dp)
        ) {
            Button(
                label = stringResource(AppRes.string.privacy_policy_reject),
                onClick = { onRejectPolicy() },
            )
            Button(
                label = stringResource(AppRes.string.privacy_policy_accept),
                onClick = { onAcceptPolicy() },
                modifier = Modifier.weight(1f),
                primary = true,
            )
        }
    }
}

@Composable
fun StartNotificationsScreen(
    onDone: (NotificationSettings) -> Unit,
) {
    // TODO populate with real values
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
