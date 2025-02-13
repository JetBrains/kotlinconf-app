package org.jetbrains.kotlinconf.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import kotlinconfapp.ui_components.generated.resources.Res
import kotlinconfapp.ui_components.generated.resources.action_error_reload
import kotlinconfapp.ui_components.generated.resources.kodee_error_loading
import kotlinconfapp.ui_components.generated.resources.kodee_error_loading_dark
import kotlinconfapp.ui_components.generated.resources.kodee_error_lost
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.jetbrains.kotlinconf.ui.theme.KotlinConfTheme
import org.jetbrains.kotlinconf.ui.theme.PreviewHelper

enum class ErrorSeverity {
    Minor,
    Normal,
    Major,
}

@Composable
fun Error(
    message: String,
    severity: ErrorSeverity,
    modifier: Modifier = Modifier,
    retryText: String = stringResource(Res.string.action_error_reload),
    onRetry: (() -> Unit)? = null,
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        if (severity == ErrorSeverity.Normal) {
            Image(
                imageVector = vectorResource(
                    if (KotlinConfTheme.colors.isDark)
                        Res.drawable.kodee_error_loading_dark
                    else
                        Res.drawable.kodee_error_loading
                ),
                contentDescription = null,
            )
            Spacer(Modifier.height(16.dp))
        }

        if (severity == ErrorSeverity.Major) {
            Image(
                imageVector = vectorResource(Res.drawable.kodee_error_lost),
                contentDescription = null,
            )
            Spacer(Modifier.height(16.dp))
        }

        if (severity == ErrorSeverity.Normal || severity == ErrorSeverity.Major) {
            StyledText(
                message,
                color = KotlinConfTheme.colors.secondaryText,
                modifier = Modifier.widthIn(max = 220.dp),
                style = KotlinConfTheme.typography.text1.copy(
                    textAlign = TextAlign.Center,
                )
            )
        } else {
            StyledText(
                message,
                color = KotlinConfTheme.colors.secondaryText,
                modifier = Modifier.padding(vertical = 32.dp),
                style = KotlinConfTheme.typography.text1.copy(
                    textAlign = TextAlign.Center,
                )
            )
        }

        if (onRetry != null) {
            Spacer(Modifier.height(24.dp))
            Button(retryText, onRetry)
        }
    }
}

@Preview
@Composable
internal fun ErrorPreview() {
    PreviewHelper {
        Error(
            message = "No search results",
            severity = ErrorSeverity.Minor,
        )
        Error(
            message = "Offline mode activated! Reconnect to bring everything back to life.",
            severity = ErrorSeverity.Normal,
        )
        Error(
            message = "Hold on! Our schedule is stuck in traffic. We’re clearing the way!",
            severity = ErrorSeverity.Normal,
            onRetry = {},
        )
        Error(
            message = "Offline mode activated! Reconnect to bring everything back to life.",
            severity = ErrorSeverity.Major,
        )
        Error(
            message = "Hold on! Our schedule is stuck in traffic. We’re clearing the way!",
            severity = ErrorSeverity.Major,
            onRetry = {},
        )
    }
}
