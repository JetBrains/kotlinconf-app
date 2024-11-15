package org.jetbrains.kotlinconf.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import org.jetbrains.compose.reload.DevelopmentEntryPoint

fun main() {
    application {
        Window(
            onCloseRequest = ::exitApplication,
            alwaysOnTop = true,
            state = rememberWindowState(width = 1000.dp, height = 800.dp),
            title = "Gallery",
        ) {
            DevelopmentEntryPoint {
                GalleryApp()
            }
        }
    }
}

@Composable
private fun GalleryApp() {
    CompositionLocalProvider(LocalDensity provides Density(2f)) {
        Column(Modifier.verticalScroll(rememberScrollState())) {
            CardTagPreview()
            DayHeaderPreview()
            FilterTagPreview()
            NowButtonPreview()
            NowLabelPreview()
            SectionTitlePreview()
            SwitcherItemPreview()
            SwitcherPreview()
            TopMenuButtonPreview()
            TopMenuTitlePreview()
        }
    }
}
