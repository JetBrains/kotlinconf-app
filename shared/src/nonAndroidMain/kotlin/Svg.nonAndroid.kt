package org.jetbrains.kotlinconf.screens

import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import org.jetbrains.skia.Data
import org.jetbrains.skia.svg.SVGDOM

actual class Svg actual constructor(svgBytes: ByteArray) {
    private val svg = SVGDOM(Data.makeFromBytes(svgBytes))

    actual val width: Float get() = svg.root?.width?.value ?: 0f
    actual val height: Float get() = svg.root?.height?.value ?: 0f

    actual fun renderTo(scope: DrawScope) {
        scope.drawIntoCanvas { canvas ->
            svg.render(canvas.nativeCanvas)
        }
    }
}
