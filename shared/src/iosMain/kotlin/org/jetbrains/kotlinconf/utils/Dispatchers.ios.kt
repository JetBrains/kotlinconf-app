package org.jetbrains.kotlinconf.utils

import kotlinx.coroutines.*
import kotlinx.coroutines.IO as kxIO

actual val Dispatchers.IO_MP: CoroutineDispatcher
    get() = kxIO
