package org.jetbrains.kotlinconf.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import org.jetbrains.kotlinconf.ui.components.Tab
import org.jetbrains.kotlinconf.ui.components.TabBar


enum class Floor(override val title: String, val resource: String) : Tab {
    FIRST("1st floor", "files/map-first.svg"),
    SECOND("2nd floor", "files/map-second.svg")
}

@Composable
fun LocationScreen() {
    var floor: Floor by remember { mutableStateOf(Floor.FIRST) }

    Box {
        SvgMapView(floor.resource, Modifier.fillMaxSize())
        TabBar(
            Floor.entries,
            selected = floor,
            onSelect = { floor = it },
        )

    }
}

@Composable
expect fun SvgMapView(filePath: String, modifier: Modifier = Modifier)