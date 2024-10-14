package org.jetbrains.kotlinconf.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.layout.FixedScale
import kotlinconfapp.shared.generated.resources.Res
import kotlinconfapp.shared.generated.resources.floor_1
import kotlinconfapp.shared.generated.resources.floor_2
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.kotlinconf.ui.components.Tab
import org.jetbrains.kotlinconf.ui.components.TabBar
import org.jetbrains.kotlinconf.ui.components.zoomable.rememberZoomableState
import org.jetbrains.kotlinconf.ui.components.zoomable.zoomable
import org.jetbrains.kotlinconf.ui.theme.mapColor
import org.jetbrains.kotlinconf.utils.Screen
import org.jetbrains.kotlinconf.utils.isTooWide

@OptIn(ExperimentalResourceApi::class)
enum class Floor(
    override val title: StringResource,
    val resourceLight: String,
    val resourceDark: String,
) : Tab {
    GROUND(
        Res.string.floor_1,
        "files/ground-floor.svg",
        "files/ground-floor-dark.svg"
    ),
    FIRST(
        Res.string.floor_2,
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

    Box(
        Modifier
            .fillMaxSize()
            .background(MaterialTheme.colors.mapColor)
    ) {
        Canvas(
            Modifier
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
        TabBar(
            Floor.entries,
            selected = floor,
            onSelect = { floor = it },
        )
    }
}

expect class Svg(svgBytes: ByteArray) {
    val width: Float
    val height: Float

    fun renderTo(scope: DrawScope)
}