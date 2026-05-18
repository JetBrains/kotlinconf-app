package org.jetbrains.kotlinconf

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import java.awt.Desktop
import java.net.URI
import java.net.URLEncoder

@Composable
actual fun rememberMapHandler(): MapHandler = remember { JvmMapHandler }

private object JvmMapHandler : MapHandler {
    override fun openNavigation(address: String) {
        val encodedAddress = URLEncoder.encode(address, "UTF-8")
        val url = "https://www.google.com/maps/search/?api=1&query=$encodedAddress"
        Desktop.getDesktop().browse(URI(url))
    }
}
