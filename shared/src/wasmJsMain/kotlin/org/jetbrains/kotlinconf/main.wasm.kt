package org.jetbrains.kotlinconf

import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.window.ComposeViewport
import kotlinx.browser.window
import org.jetbrains.kotlinconf.ui.initCoil
import org.w3c.dom.get

@JsModule("@js-joda/timezone")
external object JsJodaTimeZoneModule

private val jsJodaTz = JsJodaTimeZoneModule

@OptIn(ExperimentalComposeUiApi::class)
fun main() {
    initCoil()

    val supportsNotifications = window["supportsNotifications"] as? Boolean ?: false
    initApp(
        platformModule = platformModule,
        flags = Flags(
            supportsNotifications = supportsNotifications
        ),
    )

    ComposeViewport("ComposeApp") {
        App()
    }
}
