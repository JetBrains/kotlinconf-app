package org.jetbrains.kotlinconf.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.rememberTransformableState
import androidx.compose.foundation.gestures.transformable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.graphics.drawscope.translate
import kotlinconfapp.shared.generated.resources.Res
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.kotlinconf.ui.components.Tab
import org.jetbrains.kotlinconf.ui.components.TabBar

enum class Floor(
    override val title: String,
    val resource: String,
    val initialOffset: Offset,
    val minOffset: Offset,
    val maxOffset: Offset,
    val initialScale: Float,
    val minScale: Float,
    val maxScale: Float
) : Tab {
    FIRST(
        "1st floor",
        "files/map-first.svg",
        Offset(0f, 0f),
        Offset(-1000f, -1000f),
        Offset(1000f, 1000f),
        1.0f,
        0.5f,
        2f
    ),
    SECOND(
        "2nd floor", "files/map-second.svg",
        Offset(0f, 0f),
        Offset(-1000f, -1000f),
        Offset(1000f, 1000f),
        1.0f,
        0.5f,
        2f
    );

    fun coerceScale(scale: Float): Float {
        return scale.coerceIn(minScale, maxScale)
    }

    fun coerceTranslate(translate: Offset): Offset {
        val x = translate.x.coerceIn(minOffset.x, maxOffset.x)
        val y = translate.y.coerceIn(minOffset.y, maxOffset.y)
        return Offset(x, y)
    }
}

@OptIn(ExperimentalResourceApi::class)
@Composable
fun LocationScreen() {
    var floor: Floor by remember { mutableStateOf(Floor.FIRST) }
    var svgString: Svg? by remember { mutableStateOf(null) }

    LaunchedEffect(floor) {
        svgString = Svg(Res.readBytes(floor.resource))
    }

    var scale by remember { mutableFloatStateOf(floor.initialScale) }
    var translate by remember { mutableStateOf(Offset(0f, 0f)) }
    val state = rememberTransformableState { zoomChange, offsetChange, _ ->
        scale *= zoomChange
        translate += offsetChange
    }

    val offset = floor.coerceTranslate(floor.initialOffset + translate)
    scale = floor.coerceScale(scale)

    Box {
        Canvas(
            Modifier
                .fillMaxSize()
                .transformable(state)
        ) {
            translate(offset.x, offset.y) {
                scale(scale) {
                    svgString?.renderTo(this)
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
    fun renderTo(scope: DrawScope)
}