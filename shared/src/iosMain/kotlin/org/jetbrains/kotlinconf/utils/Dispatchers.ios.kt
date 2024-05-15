package org.jetbrains.kotlinconf.utils

import kotlinx.coroutines.*
import kotlinx.coroutines.IO as kxIO

actual val Dispatchers.App: CoroutineDispatcher
    get() = kxIO
