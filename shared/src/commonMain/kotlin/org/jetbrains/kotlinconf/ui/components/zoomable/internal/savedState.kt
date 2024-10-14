package org.jetbrains.kotlinconf.ui.components.zoomable.internal

import org.jetbrains.kotlinconf.ui.components.zoomable.GestureState

@AndroidParcelize
internal data class ZoomableSavedState(
  private val offsetX: Float?,
  private val offsetY: Float?,
  private val userZoom: Float?
) : AndroidParcelable {

  constructor(transformation: GestureState?) : this(
    offsetX = transformation?.offset?.x,
    offsetY = transformation?.offset?.y,
    userZoom = transformation?.userZoomFactor?.value
  )

}
