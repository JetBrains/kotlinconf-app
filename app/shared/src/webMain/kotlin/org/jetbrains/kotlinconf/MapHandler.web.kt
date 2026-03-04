package org.jetbrains.kotlinconf

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import web.window.WindowTarget
import web.window._blank
import web.window.window
import kotlin.js.js

@Composable
actual fun rememberMapHandler(): MapHandler = remember { WebMapHandler }

private object WebMapHandler : MapHandler {
    override fun openNavigation(address: String) {
        val encodedAddress = encodeURIComponent(address)
        val url = "https://www.google.com/maps/search/?api=1&query=$encodedAddress"
        window.open(url, WindowTarget._blank)
    }
}

private fun encodeURIComponent(str: String): String = js("encodeURIComponent(str)")
