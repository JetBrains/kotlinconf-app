package org.jetbrains.kotlinconf

import androidx.compose.foundation.interaction.DragInteraction
import androidx.compose.foundation.interaction.InteractionSource
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.map

@Composable
fun HideKeyboardOnDragHandler(listState: LazyListState) {
    HideKeyboardOnDragHandlerImpl(listState.interactionSource)
}

@Composable
fun HideKeyboardOnDragHandler(gridState: LazyGridState) {
   HideKeyboardOnDragHandlerImpl(gridState.interactionSource)
}

@Composable
private fun HideKeyboardOnDragHandlerImpl(interactionSource: InteractionSource) {
    if (LocalFlags.current.hideKeyboardOnDrag) {
        val keyboard = LocalSoftwareKeyboardController.current
        val focusManager = LocalFocusManager.current
        LaunchedEffect(interactionSource) {
            interactionSource.interactions
                .distinctUntilChanged()
                .filterIsInstance<DragInteraction>()
                .map { dragInteraction -> dragInteraction is DragInteraction.Start }
                .collect {
                    keyboard?.hide()
                    focusManager.clearFocus()
                }
        }
    }
}
