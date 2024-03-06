package org.jetbrains.kotlinconf.ui

import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import com.caverock.androidsvg.SVG

actual class Svg actual constructor(svgBytes: ByteArray) {
    private val svg = SVG.getFromString(svgBytes.decodeToString())

    actual fun renderTo(scope: DrawScope) {
        scope.drawIntoCanvas { canvas ->
            svg.renderToCanvas(canvas.nativeCanvas)
        }
    }
}