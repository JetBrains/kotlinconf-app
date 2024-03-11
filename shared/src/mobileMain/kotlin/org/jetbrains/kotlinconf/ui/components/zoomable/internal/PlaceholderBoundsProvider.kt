package org.jetbrains.kotlinconf.ui.components.zoomable.internal

import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.geometry.isSpecified
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.toSize
import org.jetbrains.kotlinconf.ui.components.zoomable.RealZoomableState
import org.jetbrains.kotlinconf.ui.components.zoomable.ZoomableState
import org.jetbrains.kotlinconf.ui.components.zoomable.RelativeContentLocation
import org.jetbrains.kotlinconf.ui.components.zoomable.ZoomableContentLocation

/**
 * Used by [org.jetbrains.kotlinconf.ui.components.zoomable.ZoomableImage] to provide a fallback value for
 * [ZoomableState.transformedContentBounds] before the full quality image is loaded. This
 * ensures that the bounds aren't empty while a placeholder image is visible.
 */
internal data class PlaceholderBoundsProvider(val contentSize: Size) {
  private var layoutSize: IntSize? by mutableStateOf(null)

  @Stable
  fun calculate(state: RealZoomableState): Rect? {
    val layoutSize = layoutSize ?: return null

    return if (contentSize.isSpecified) {
      RelativeContentLocation(
        size = contentSize,
        scale = state.contentScale,
        alignment = state.contentAlignment,
      )
    } else {
      ZoomableContentLocation.SameAsLayoutBounds
    }.location(
      layoutSize = layoutSize.toSize(),
      direction = state.layoutDirection,
    )
  }
}
