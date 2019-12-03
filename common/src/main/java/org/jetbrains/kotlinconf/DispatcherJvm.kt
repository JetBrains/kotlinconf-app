package org.jetbrains.kotlinconf

import kotlinx.coroutines.*
import java.util.*

internal actual fun dispatcher(): CoroutineDispatcher = Dispatchers.Main

internal actual fun generateUserId(): String = "android-" + UUID.randomUUID().toString()
