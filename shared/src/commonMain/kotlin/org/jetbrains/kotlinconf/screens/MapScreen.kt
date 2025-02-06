package org.jetbrains.kotlinconf.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.layout.FixedScale
import androidx.compose.ui.unit.dp
import kotlinconfapp.shared.generated.resources.Res
import kotlinconfapp.shared.generated.resources.map_first_floor
import kotlinconfapp.shared.generated.resources.map_ground_floor
import kotlinconfapp.shared.generated.resources.map_title
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.kotlinconf.ui.components.Divider
import org.jetbrains.kotlinconf.ui.components.MainHeaderTitleBar
import org.jetbrains.kotlinconf.ui.components.Switcher
import org.jetbrains.kotlinconf.ui.theme.KotlinConfTheme
import org.jetbrains.kotlinconf.utils.Screen
import org.jetbrains.kotlinconf.utils.isTooWide
import org.jetbrains.kotlinconf.zoomable.rememberZoomableState
import org.jetbrains.kotlinconf.zoomable.zoomable

@Composable
fun MapScreen() {
    LocationScreen()
}

enum class Floor(
    val title: StringResource,
    val resourceLight: String,
    val resourceDark: String,
) {
    GROUND(
        Res.string.map_ground_floor,
        "files/ground-floor.svg",
        "files/ground-floor-dark.svg"
    ),
    FIRST(
        Res.string.map_first_floor,
        "files/first-floor.svg",
        "files/first-floor-dark.svg",
    );
}

val Floor.resource: String
    @Composable get() = if (isSystemInDarkTheme()) resourceDark else resourceLight

@OptIn(ExperimentalResourceApi::class)
@Composable
fun LocationScreen() {
    var floor: Floor by remember { mutableStateOf(Floor.GROUND) }
    var svg: Svg? by remember { mutableStateOf(null) }
    val path = floor.resource
    val state = rememberZoomableState()
    val isScreenTooWide = Screen.isTooWide()

    LaunchedEffect(path) {
        svg = Svg(Res.readBytes(path))
        state.contentScale = FixedScale(if (isScreenTooWide) 1.7f else 2.7f)
    }

    Column(Modifier.fillMaxSize()) {
        MainHeaderTitleBar(
            title = stringResource(Res.string.map_title),
        )
        Divider(thickness = 1.dp, color = KotlinConfTheme.colors.strokePale)

        Switcher(
            items = Floor.entries.map { stringResource(it.title) },
            selectedIndex = floor.ordinal,
            onSelect = { index -> floor = Floor.entries[index] },
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
        )

        Canvas(
            Modifier
                .clipToBounds()
                .fillMaxSize()
                .zoomable(state)
        ) {
            val currentSvg = svg ?: return@Canvas
            val scale = size.width / currentSvg.width
            val imageHeight = currentSvg.height * scale
            val offsetY = (size.height - imageHeight) / 2

            translate(0f, offsetY) {
                scale(scale, pivot = Offset.Zero) {
                    currentSvg.renderTo(this)
                }
            }
        }
    }
}

expect class Svg(svgBytes: ByteArray) {
    val width: Float
    val height: Float

    fun renderTo(scope: DrawScope)
}
