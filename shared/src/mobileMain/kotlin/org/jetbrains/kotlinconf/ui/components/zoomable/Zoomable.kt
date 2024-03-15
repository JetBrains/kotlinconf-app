package org.jetbrains.kotlinconf.ui.components.zoomable

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.node.CompositionLocalConsumerModifierNode
import androidx.compose.ui.node.DelegatingNode
import androidx.compose.ui.node.ModifierNodeElement
import androidx.compose.ui.node.requireDensity
import androidx.compose.ui.platform.InspectorInfo
import androidx.compose.ui.unit.Velocity
import androidx.compose.ui.unit.toSize
import kotlinx.coroutines.launch
import org.jetbrains.kotlinconf.ui.components.zoomable.internal.MutatePriorities
import org.jetbrains.kotlinconf.ui.components.zoomable.internal.TappableAndQuickZoomableElement
import org.jetbrains.kotlinconf.ui.components.zoomable.internal.TransformableElement
import org.jetbrains.kotlinconf.ui.components.zoomable.internal.stopTransformation

/**
 * A `Modifier` for handling pan & zoom gestures, designed to be shared across all your media
 * composables so that your users can use the same familiar gestures throughout your app. It offers,
 *
 * - Pinch to zoom and flings
 * - Double tap to zoom
 * - Single finger zoom (double tap and hold)
 * - Compatibility with nested scrolling
 * - Click listeners
 *
 * Because `Modifier.zoomable()` consumes all gestures including double-taps, [Modifier.clickable] and
 * [Modifier.combinedClickable] will not work on the composable this `Modifier.zoomable()` is applied to.
 * As an alternative, [onClick] and [onLongClick] parameters can be used instead.
 *
 * @param enabled whether or not gestures are enabled.
 *
 * @param clipToBounds defaults to true to act as a reminder that this layout should probably fill all
 * available space. Otherwise, gestures made outside the composable's layout bounds will not be registered.
 * */
fun Modifier.zoomable(state: ZoomableState): Modifier {
  check(state is RealZoomableState)
  return this
    .onSizeChanged { state.contentLayoutSize = it.toSize() }
    .then(ZoomableElement(state = state))
    .thenIf(state.autoApplyTransformations) {
      Modifier.applyTransformation(state.contentTransformation)
    }
}

private data class ZoomableElement(
  private val state: RealZoomableState,
) : ModifierNodeElement<ZoomableNode>() {

  override fun create(): ZoomableNode = ZoomableNode(state = state)

  override fun update(node: ZoomableNode) {
    node.update(state = state)
  }

  override fun InspectorInfo.inspectableProperties() {
    name = "zoomable"
    properties["state"] = state
  }
}

@OptIn(ExperimentalFoundationApi::class)
private class ZoomableNode(
  private var state: RealZoomableState,
) : DelegatingNode(), CompositionLocalConsumerModifierNode {

  val onPress: (Offset) -> Unit = {
    coroutineScope.launch {
      state.transformableState.stopTransformation(MutatePriorities.FlingAnimation)
    }
  }
  val onDoubleTap: (centroid: Offset) -> Unit = { centroid ->
    coroutineScope.launch {
      state.handleDoubleTapZoomTo(centroid = centroid)
    }
  }

  val onQuickZoomStopped = {
    if (state.isZoomOutsideRange()) {
      coroutineScope.launch {
        state.smoothlySettleZoomOnGestureEnd()
      }
    }
  }
  val onTransformStopped: (velocity: Velocity) -> Unit = { velocity ->
    coroutineScope.launch {
      if (state.isZoomOutsideRange()) {
        state.smoothlySettleZoomOnGestureEnd()
      } else {
        state.fling(velocity = velocity, density = requireDensity())
      }
    }
  }

  private val tappableAndQuickZoomableNode = TappableAndQuickZoomableElement(
    transformableState = state.transformableState,
    onPress = onPress,
    onDoubleTap = onDoubleTap,
    onQuickZoomStopped = onQuickZoomStopped,
  ).create()

  private val transformableNode = TransformableElement(
    state = state.transformableState,
    canPan = state::canConsumePanChange,
    onTransformStopped = onTransformStopped,
    lockRotationOnZoomPan = false,
  ).create()

  init {
    // Note to self: the order in which these nodes are delegated is important.
    delegate(tappableAndQuickZoomableNode)
    delegate(transformableNode)
  }

  fun update(
    state: RealZoomableState,
  ) {
    transformableNode.update(
      state = state.transformableState,
      canPan = state::canConsumePanChange,
      lockRotationOnZoomPan = false,
      onTransformStopped = onTransformStopped,
    )
    tappableAndQuickZoomableNode.update(
      onPress = onPress,
      onDoubleTap = onDoubleTap,
      onQuickZoomStopped = onQuickZoomStopped,
      transformableState = state.transformableState,
    )
  }
}

private inline fun Modifier.thenIf(predicate: Boolean, other: () -> Modifier): Modifier {
  return if (predicate) this.then(other()) else this
}
