package org.jetbrains.kotlinconf.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.painter.Painter
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.painterResource

@OptIn(ExperimentalResourceApi::class)
@Composable
internal fun iconResource(name: String): Painter {
    return painterResource("icons/$name")
}

@OptIn(ExperimentalResourceApi::class)
@Composable
internal fun drawableResource(name: String): Painter {
    val folder = if (isSystemInDarkTheme()) {
        "drawable-night"
    } else {
        "drawable"
    }

    return painterResource("$folder/$name")
}
