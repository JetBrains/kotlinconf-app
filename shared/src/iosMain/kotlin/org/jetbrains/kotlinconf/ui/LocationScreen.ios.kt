package org.jetbrains.kotlinconf.ui

import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import org.jetbrains.skia.Data
import org.jetbrains.skia.svg.SVGDOM

actual class Svg actual constructor(svgBytes: ByteArray) {
    private val svg = SVGDOM(Data.makeFromBytes(svgBytes))

    actual fun renderTo(scope: DrawScope) {
        scope.drawIntoCanvas { canvas ->
            svg.render(canvas.nativeCanvas)
        }
    }
}