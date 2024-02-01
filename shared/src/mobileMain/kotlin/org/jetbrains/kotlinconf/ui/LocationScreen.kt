package org.jetbrains.kotlinconf.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import org.jetbrains.kotlinconf.ui.components.TabBar

const val MAP_PATH: String = "files/map.svg"

@Composable
fun LocationScreen() {
    var floor by remember { mutableStateOf("FLOOR 0") }

    Box {
        SvgMapView(MAP_PATH, Modifier.fillMaxSize())
        TabBar(
            tabs = listOf("FLOOR -1", "FLOOR 0", "FLOOR 1"),
            selected = floor,
            onSelect = { floor = it },
        )

    }
}

@Composable
expect fun SvgMapView(filePath: String, modifier: Modifier = Modifier)