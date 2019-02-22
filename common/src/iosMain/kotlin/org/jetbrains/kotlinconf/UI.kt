package org.jetbrains.kotlinconf

import kotlinx.coroutines.*
import platform.darwin.*
import kotlin.coroutines.*

class UI : CoroutineDispatcher() {
    override fun dispatch(context: CoroutineContext, block: Runnable) {
        val queue = dispatch_get_main_queue()
        dispatch_async(queue) {
            block.run()
        }
    }
}