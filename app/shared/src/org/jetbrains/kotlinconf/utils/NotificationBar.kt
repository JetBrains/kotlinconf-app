package org.jetbrains.kotlinconf.utils

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.seconds

@Stable
class NotificationBarState(private val scope: CoroutineScope) {
    var message: String? by mutableStateOf(null)
        private set

    private var dismissJob: Job? = null

    fun show(message: String) {
        dismissJob?.cancel()
        this.message = message
        dismissJob = scope.launch {
            delay(5.seconds)
            this@NotificationBarState.message = null
        }
    }
}

val LocalNotificationBar = compositionLocalOf<NotificationBarState> {
    error("LocalNotificationBar not set")
}

@Composable
fun rememberNotificationBarState(): NotificationBarState {
    val scope = rememberCoroutineScope()
    return remember { NotificationBarState(scope) }
}
