package org.jetbrains.kotlinconf

import androidx.compose.foundation.interaction.DragInteraction
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.map

@Composable
fun HideKeyboardOnDragHandler(listState: LazyListState) {
    if (LocalFlags.current.hideKeyboardOnDrag) {
        val keyboard = LocalSoftwareKeyboardController.current
        LaunchedEffect(listState) {
            listState.interactionSource.interactions
                .distinctUntilChanged()
                .filterIsInstance<DragInteraction>()
                .map { dragInteraction -> dragInteraction is DragInteraction.Start }
                .collect { keyboard?.hide() }
        }
    }
}
