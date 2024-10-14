package org.jetbrains.kotlinconf.ui.components.zoomable

import androidx.compose.runtime.Immutable

@Immutable
data class ZoomSpec(
  /**
   * The maximum zoom level as a percentage of the content size before rubber banding kicks in.
   *
   * For example, a value of `3.0` indicates that the content can be zoomed in up to 300%
   * of its original size. Setting this value to `1.0` or less will disable zooming.
   */
  val maxZoomFactor: Float = 5f,

  /**
   * Whether to apply rubber banding to zoom gestures when content is over or under zoomed
   * as a form of visual feedback that the content can't be zoomed any further. When set to false,
   * content will keep zooming in a free-form manner even when it goes beyond its boundaries
   * (until the gesture is released).
   */
  val preventOverOrUnderZoom: Boolean = true,
) {
  internal val range = ZoomRange(maxZoomAsRatioOfSize = maxZoomFactor)
}
