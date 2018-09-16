package org.jetbrains.kotlinconf

import kotlin.coroutines.*
import kotlinx.coroutines.*

fun launchAndCatch(
        context: CoroutineContext,
        onError: (Throwable) -> Unit,
        onFinally: () -> Unit = {},
        function: suspend () -> Unit
) {
    launch(context) {
        try {
            function()
        } catch (e: Throwable) {
            onError(e)
        } finally {
            onFinally()
        }
    }
}