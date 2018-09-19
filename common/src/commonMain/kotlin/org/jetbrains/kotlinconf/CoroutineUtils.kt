package org.jetbrains.kotlinconf

import kotlinx.coroutines.*

typealias NativeCallback<T> = (T?, Throwable?) -> Unit

internal fun <T> wrapCallback(callback: NativeCallback<T>, block: suspend () -> T) {
    launch(Unconfined) {
        val result = try {
            block()
        } catch (cause: Throwable) {
            callback(null, cause)
            null
        }

        result?.let { callback(it, null) }
    }
}