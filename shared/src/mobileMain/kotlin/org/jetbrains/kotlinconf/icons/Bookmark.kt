package org.jetbrains.kotlinconf.icons

import androidx.compose.material.icons.materialIcon
import androidx.compose.material.icons.materialPath
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathFillType
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp


private var _bookmark: ImageVector? = null

val Bookmark: ImageVector
    get() {
        if (_bookmark != null) {
            return _bookmark!!
        }

        _bookmark = materialIcon(name = "Filled.Bookmark"){
            materialPath {

            }
        }
        _bookmark = ImageVector.Builder(
            name = "Filled.Bookmark",
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
                moveTo(16.5f, 5f)
                curveTo(12.9281f, 5f, 10f, 7.9281f, 10f, 11.5f)
                lineTo(10f, 41.5f)
                arcTo(1.50015f, 1.50015f, 0f, isMoreThanHalf = false, isPositiveArc = false, 12.376953f, 42.716797f)
                lineTo(24f, 34.347656f)
                lineTo(35.623047f, 42.716797f)
                arcTo(1.50015f, 1.50015f, 0f, isMoreThanHalf = false, isPositiveArc = false, 38f, 41.5f)
                lineTo(38f, 11.5f)
                curveTo(38f, 7.9281f, 35.0719f, 5f, 31.5f, 5f)
                lineTo(16.5f, 5f)
                close()
                moveTo(16.5f, 8f)
                lineTo(31.5f, 8f)
                curveTo(33.4501f, 8f, 35f, 9.5499f, 35f, 11.5f)
                lineTo(35f, 38.572266f)
                lineTo(24.876953f, 31.283203f)
                arcTo(1.50015f, 1.50015f, 0f, isMoreThanHalf = false, isPositiveArc = false, 23.123047f, 31.283203f)
                lineTo(13f, 38.572266f)
                lineTo(13f, 11.5f)
                curveTo(13f, 9.5499f, 14.5499f, 8f, 16.5f, 8f)
                close()
            }
        }.build()
        return _bookmark!!
    }