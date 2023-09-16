package org.jetbrains.kotlinconf.icons

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathFillType
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

private var _bookmarked: ImageVector? = null

val Bookmarked: ImageVector
    get() {
        if (_bookmarked != null) {
            return _bookmarked!!
        }
        _bookmarked = ImageVector.Builder(
            name = "Filled.Bookmarked",
            defaultWidth = 48.dp,
            defaultHeight = 48.dp,
            viewportWidth = 48f,
            viewportHeight = 48f
        ).apply {
            path(
                fill = SolidColor(Color.Black),
                fillAlpha = 1.0f,
                stroke = null,
                strokeAlpha = 1.0f,
                strokeLineWidth = 1.0f,
                strokeLineCap = StrokeCap.Butt,
                strokeLineJoin = StrokeJoin.Miter,
                strokeLineMiter = 1.0f,
                pathFillType = PathFillType.NonZero
            ) {
                moveTo(36.5f, 43f)
                curveToRelative(-0.309f, 0f, -0.616f, -0.095f, -0.876f, -0.283f)
                lineTo(24f, 34.348f)
                lineToRelative(-11.624f, 8.369f)
                curveToRelative(-0.458f, 0.329f, -1.06f, 0.375f, -1.561f, 0.118f)
                curveTo(10.315f, 42.579f, 10f, 42.063f, 10f, 41.5f)
                verticalLineToRelative(-30f)
                curveTo(10f, 7.916f, 12.916f, 5f, 16.5f, 5f)
                horizontalLineToRelative(15f)
                curveToRelative(3.584f, 0f, 6.5f, 2.916f, 6.5f, 6.5f)
                verticalLineToRelative(30f)
                curveToRelative(0f, 0.563f, -0.315f, 1.079f, -0.816f, 1.335f)
                curveTo(36.968f, 42.945f, 36.734f, 43f, 36.5f, 43f)
                close()
            }
        }.build()
        return _bookmarked!!
    }