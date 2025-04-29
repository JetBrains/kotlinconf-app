package org.jetbrains.kotlinconf

import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.window.ComposeViewport
import kotlinx.browser.window
import org.jetbrains.kotlinconf.ui.initCoil
import org.jetbrains.kotlinconf.utils.Logger
import org.w3c.dom.get

@JsModule("@js-joda/timezone")
external object JsJodaTimeZoneModule

private val jsJodaTz = JsJodaTimeZoneModule

@OptIn(ExperimentalComposeUiApi::class)
fun main() {
    initCoil()

    val supportsNotifications = window["supportsNotifications"] as? Boolean ?: false
    initApp(
        platformLogger = object : Logger {
            override fun log(tag: String, lazyMessage: () -> String) {
                println("[$tag] ${lazyMessage()}")
            }
        },
        platformModule = platformModule,
        flags = Flags(
            supportsNotifications = supportsNotifications
        ),
    )

    ComposeViewport("ComposeApp") {
        App()
    }
}
