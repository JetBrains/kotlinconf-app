package org.jetbrains.kotlinconf.ui.components.zoomable.internal

import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.geometry.isSpecified
import androidx.compose.ui.unit.LayoutDirection
import org.jetbrains.kotlinconf.ui.components.zoomable.zoomable
import kotlin.LazyThreadSafetyMode.NONE

/**
 * Calculates the top-left offset of this [Rect] such that it always overlaps with [destination].
 *
 * This is used by [Modifier.zoomable] to prevent panning of its content outside of its layout bounds.
 */
internal fun Rect.calculateTopLeftToOverlapWith(
  destination: Size,
  alignment: Alignment,
  layoutDirection: LayoutDirection,
): Offset {
  check(destination.isSpecified) {
    "Whoops Modifier.zoomable() is not supposed to handle gestures yet. " +
      "Please file an issue on https://github.com/saket/telephoto/issues?"
  }

  val alignedOffset by lazy(NONE) {
    // Rounding of floats to ints will cause some loss in precision because the final
    // offset is calculated by combining offset & zoom, but hopefully this is okay.
    // The alternative would be to fork Alignment's code to work with floats.
    alignment.align(
      size = size.roundToIntSize(),
      space = destination.roundToIntSize(),
      layoutDirection = layoutDirection,
    )
  }
  return topLeft.copy(
    x = if (width >= destination.width) {
      topLeft.x.coerceIn(
        minimumValue = (destination.width - width).coerceAtMost(0f),
        maximumValue = 0f
      )
    } else {
      alignedOffset.x.toFloat()
    },
    y = if (height >= destination.height) {
      topLeft.y.coerceIn(
        minimumValue = (destination.height - height).coerceAtMost(0f),
        maximumValue = 0f
      )
    } else {
      alignedOffset.y.toFloat()
    }
  )
}
