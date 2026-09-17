package org.jetbrains.kotlinconf

import org.jetbrains.skiko.hostOs

internal actual val isMacPlatform: Boolean by lazy {
    hostOs.isMacOS
}