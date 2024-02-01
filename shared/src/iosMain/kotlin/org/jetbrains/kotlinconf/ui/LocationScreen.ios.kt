package org.jetbrains.kotlinconf.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.rememberTransformableState
import androidx.compose.foundation.gestures.transformable
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.graphics.nativeCanvas
import kotlinconfapp.shared.generated.resources.Res
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.skia.Data
import org.jetbrains.skia.svg.SVGDOM

@OptIn(ExperimentalResourceApi::class)
@Composable
actual fun SvgMapView(filePath: String, modifier: Modifier) {
    var svgBytes: ByteArray? by remember { mutableStateOf(null) }
    LaunchedEffect(Unit) {
        svgBytes = Res.readBytes(filePath)
    }

    var scale by remember { mutableFloatStateOf(1f) }
    var offset by remember { mutableStateOf(Offset(0f, 0f)) }
    val state = rememberTransformableState { zoomChange, offsetChange, _ ->
        scale *= zoomChange
        offset += offsetChange
    }

    val svg = svgBytes?.let { SVGDOM(Data.makeFromBytes(it)) }

    Canvas(modifier.transformable(state)) {
        translate(offset.x, offset.y) {
            scale(scale) {
                drawIntoCanvas { canvas ->
                    svg?.render(canvas.nativeCanvas)
                }
            }
        }
    }
}
