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
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.viewinterop.AndroidView
import com.caverock.androidsvg.SVG
import kotlinconfapp.shared.generated.resources.Res
import org.jetbrains.compose.resources.ExperimentalResourceApi

@OptIn(ExperimentalResourceApi::class)
@Composable
actual fun SvgMapView(filePath: String, modifier: Modifier) {
    var svgString by remember { mutableStateOf("") }
    LaunchedEffect(Unit) {
        svgString = Res.readBytes(filePath)
            .decodeToString()
    }

    var scale by remember { mutableFloatStateOf(0f) }
    var offset by remember { mutableStateOf(Offset(0f, 0f)) }
    val state = rememberTransformableState { zoomChange, offsetChange, _ ->
        scale *= zoomChange
        offset += offsetChange
    }

    val svg = SVG.getFromString(svgString)
    Canvas(modifier.transformable(state)) {
        translate(offset.x, offset.y) {
            scale(scale) {
                drawIntoCanvas { canvas ->
                    svg.renderToCanvas(canvas.nativeCanvas)
                }
            }
        }
    }
}