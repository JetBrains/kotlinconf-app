package org.jetbrains.kotlinconf.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Slider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
            state = rememberWindowState(
                width = 800.dp, height = 600.dp,
            ),
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
    var densityFloat by remember { mutableStateOf(1f) }
    Column {
        CompositionLocalProvider(LocalDensity provides Density(densityFloat)) {
            Column(Modifier.verticalScroll(rememberScrollState())) {
                ButtonPreview()
                CardTagPreview()
                DayHeaderPreview()
                FilterTagPreview()
                MainNavigationPreview()
                NowButtonPreview()
                NowLabelPreview()
                PageMenuItemPreview()
                SectionTitlePreview()
                ServiceEventsPreview()
                SettingsItemPreview()
                SwitcherItemPreview()
                SwitcherPreview()
                TogglePreview()
                TopMenuButtonPreview()
                TopMenuTitlePreview()
            }
        }
        Slider(densityFloat, onValueChange = { densityFloat = it }, valueRange = 0.5f..4f)
    }
}
