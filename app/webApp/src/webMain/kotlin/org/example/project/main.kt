@file:OptIn(ExperimentalWasmJsInterop::class)

package org.example.project

import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.window.ComposeViewport
import dev.zacsweers.metro.createGraphFactory
import org.jetbrains.kotlinconf.App
import org.jetbrains.kotlinconf.Flags
import org.jetbrains.kotlinconf.di.WebAppGraph
import org.jetbrains.kotlinconf.initApp
import org.jetbrains.kotlinconf.ui.initCoil
import org.jetbrains.kotlinconf.utils.Logger
import kotlin.js.ExperimentalWasmJsInterop

external object Window {
    val supportsNotifications: Boolean?
}

external val window: Window

@OptIn(ExperimentalComposeUiApi::class)
fun main() {
    initCoil()

    val appGraph = createGraphFactory<WebAppGraph.Factory>().create(
        platformFlags = Flags(
            supportsNotifications = window.supportsNotifications ?: false
        )
    )

    initApp(
        appGraph = appGraph,
        platformLogger = object : Logger {
            override fun log(tag: String, lazyMessage: () -> String) {
                println("[$tag] ${lazyMessage()}")
            }
        },
    )
    ComposeViewport {
        App(appGraph)
    }
}