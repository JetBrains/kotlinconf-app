package org.jetbrains.kotlinconf.utils

import androidx.compose.runtime.Composable
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.backhandler.BackHandler

@Composable
actual fun MainBackHandler() {
    // Empty BackHandler to prevent swiping to go back from the current screen
    @OptIn(ExperimentalComposeUiApi::class)
    BackHandler(true) {  }
}
