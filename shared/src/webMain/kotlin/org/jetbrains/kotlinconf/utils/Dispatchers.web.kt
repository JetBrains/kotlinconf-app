package org.jetbrains.kotlinconf.utils

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

actual val Dispatchers.App: CoroutineDispatcher
    get() = Dispatchers.App

actual val Dispatchers.IO: CoroutineDispatcher
    get() = Dispatchers.Default
