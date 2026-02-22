package org.jetbrains.kotlinconf

import androidx.compose.runtime.Composable
import androidx.compose.runtime.staticCompositionLocalOf

interface MapHandler {
    fun openNavigation(address: String)
}

val LocalMapHandler = staticCompositionLocalOf<MapHandler> {
    error("No MapHandler provided")
}

@Composable
expect fun rememberMapHandler(): MapHandler
