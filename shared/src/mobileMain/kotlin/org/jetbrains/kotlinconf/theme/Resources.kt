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
internal fun drawablePartners(name: String): Painter {
    val folder = if (isSystemInDarkTheme()) {
        "partners-night"
    } else {
        "parnters"
    }

    return painterResource("$folder/$name")
}

@OptIn(ExperimentalResourceApi::class)
@Composable
internal fun drawableVector(name: String): Painter {
    return painterResource("vector/$name")
}
