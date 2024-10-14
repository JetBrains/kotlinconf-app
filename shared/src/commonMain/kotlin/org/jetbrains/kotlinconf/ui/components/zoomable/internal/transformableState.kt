package org.jetbrains.kotlinconf.ui.components.zoomable.internal

/*
 * Copyright 2021 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import androidx.compose.foundation.MutatePriority
import androidx.compose.foundation.MutatorMutex
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.geometry.Offset
import kotlinx.coroutines.coroutineScope

/**
 * State of [transformable]. Allows for a granular control of how different gesture
 * transformations are consumed by the user as well as to write custom transformation methods
 * using [transform] suspend function.
 */
internal interface TransformableState {
  /**
   * Call this function to take control of transformations and gain the ability to send transform
   * events via [TransformScope.transformBy]. All actions that change zoom, pan or rotation
   * values must be performed within a [transform] block (even if they don't call any other
   * methods on this object) in order to guarantee that mutual exclusion is enforced.
   *
   * If [transform] is called from elsewhere with the [transformPriority] higher or equal to
   * ongoing transform, ongoing transform will be canceled.
   */
  suspend fun transform(
    transformPriority: MutatePriority = MutatePriority.Default,
    block: suspend TransformScope.() -> Unit
  )

  /**
   * Whether this [TransformableState] is currently transforming by gesture or programmatically or
   * not.
   */
  val isTransformInProgress: Boolean
}

/**
 * Scope used for suspending transformation operations
 */
internal interface TransformScope {
  /**
   * Attempts to transform by [zoomChange] in relative multiplied value, by [panChange] in
   * pixels and by [rotationChange] in degrees.
   *
   * @param zoomChange scale factor multiplier change for zoom
   * @param panChange panning offset change, in [Offset] pixels
   * @param rotationChange change of the rotation in degrees
   */
  fun transformBy(
    zoomChange: Float = 1f,
    panChange: Offset = Offset.Zero,
    rotationChange: Float = 0f,
    centroid: Offset = Offset.Zero,
  )
}

/**
 * Default implementation of [TransformableState] interface that contains necessary information
 * about the ongoing transformations and provides smooth transformation capabilities.
 *
 * This is the simplest way to set up a [transformable] modifier. When constructing this
 * [TransformableState], you must provide a [onTransformation] lambda, which will be invoked
 * whenever pan, zoom or rotation happens (by gesture input or any [TransformableState.transform]
 * call) with the deltas from the previous event.
 *
 * @param onTransformation callback invoked when transformation occurs. The callback receives the
 * change from the previous event. It's relative scale multiplier for zoom, [Offset] in pixels
 * for pan and degrees for rotation. Callers should update their state in this lambda.
 */
internal fun TransformableState(
  onTransformation: (zoomChange: Float, panChange: Offset, rotationChange: Float, centroid: Offset) -> Unit
): TransformableState = DefaultTransformableState(onTransformation)

/**
 * Stop and suspend until any ongoing [TransformableState.transform] with priority
 * [terminationPriority] or lower is terminated.
 *
 * @param terminationPriority transformation that runs with this priority or lower will be stopped
 */
internal suspend fun TransformableState.stopTransformation(
  terminationPriority: MutatePriority = MutatePriority.Default
) {
  this.transform(terminationPriority) {
    // do nothing, just lock the mutex so other scroll actors are cancelled
  }
}

private class DefaultTransformableState(
  val onTransformation: (zoomChange: Float, panChange: Offset, rotationChange: Float, centroid: Offset) -> Unit
) : TransformableState {

  private val transformScope: TransformScope = object : TransformScope {
    override fun transformBy(zoomChange: Float, panChange: Offset, rotationChange: Float, centroid: Offset) =
      onTransformation(zoomChange, panChange, rotationChange, centroid)
  }

  private val transformMutex = MutatorMutex()

  private val isTransformingState = mutableStateOf(false)

  override suspend fun transform(
    transformPriority: MutatePriority,
    block: suspend TransformScope.() -> Unit
  ): Unit = coroutineScope {
    transformMutex.mutateWith(transformScope, transformPriority) {
      isTransformingState.value = true
      try {
        block()
      } finally {
        isTransformingState.value = false
      }
    }
  }

  override val isTransformInProgress: Boolean
    get() = isTransformingState.value
}
