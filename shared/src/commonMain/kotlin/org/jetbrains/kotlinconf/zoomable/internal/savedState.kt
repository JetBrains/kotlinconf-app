package org.jetbrains.kotlinconf.zoomable.internal

import org.jetbrains.kotlinconf.zoomable.GestureState

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
