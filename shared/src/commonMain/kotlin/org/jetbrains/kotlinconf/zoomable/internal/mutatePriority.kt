package org.jetbrains.kotlinconf.zoomable.internal

import androidx.compose.foundation.MutatePriority

internal object MutatePriorities {
  // Used to ensure that any existing fling animations are
  // cancelled but double-tap zoom animations are continued.
  val FlingAnimation get() = MutatePriority.Default
}
