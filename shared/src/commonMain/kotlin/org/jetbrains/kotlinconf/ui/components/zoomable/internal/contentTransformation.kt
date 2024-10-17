package org.jetbrains.kotlinconf.ui.components.zoomable.internal

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.layout.ScaleFactor
import org.jetbrains.kotlinconf.ui.components.zoomable.ZoomableContentTransformation

internal data class RealZoomableContentTransformation(
  override val isSpecified: Boolean,
  override val scale: ScaleFactor,
  override val scaleMetadata: ScaleMetadata,
  override val offset: Offset,
  override val centroid: Offset?,
  override val contentSize: Size = Size.Unspecified,
  override val rotationZ: Float = 0f,
) : ZoomableContentTransformation {

  data class ScaleMetadata(
    override val initialScale: ScaleFactor,
    override val userZoom: Float,
  ) : ZoomableContentTransformation.ScaleMetadata
}
