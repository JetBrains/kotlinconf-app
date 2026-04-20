@file:OptIn(ExperimentalWasmJsInterop::class)

package org.jetbrains.kotlinconf

import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.platform.LocalFontFamilyResolver
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.window.ComposeViewport
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.preloadFont
import org.jetbrains.kotlinconf.flags.Flags
import org.jetbrains.kotlinconf.ui.initCoil
import org.jetbrains.kotlinconf.utils.Logger
import org.jetbrains.kotlinconf.web.generated.resources.NotoColorEmoji
import org.jetbrains.kotlinconf.web.generated.resources.Res
import org.koin.core.annotation.Singleton
import kotlin.js.ExperimentalWasmJsInterop

external object Window {
    val supportsNotifications: Boolean?
}

external val window: Window

@Singleton
class WebLogger : Logger {
    override fun log(tag: String, lazyMessage: () -> String) {
        println("[$tag] ${lazyMessage()}")
    }
}

fun main() {
    initCoil()

    initCoreApp(
        Flags(
            supportsNotifications = window.supportsNotifications ?: false
        )
    )

    @OptIn(ExperimentalComposeUiApi::class)
    ComposeViewport {
        @OptIn(ExperimentalResourceApi::class)
        val emojiFont = preloadFont(Res.font.NotoColorEmoji).value
        val fontFamilyResolver = LocalFontFamilyResolver.current
        LaunchedEffect(fontFamilyResolver, emojiFont) {
            if (emojiFont != null) {
                fontFamilyResolver.preload(FontFamily(listOf(emojiFont)))
            }
        }

        App()
    }
}