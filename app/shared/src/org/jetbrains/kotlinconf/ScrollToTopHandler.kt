package org.jetbrains.kotlinconf

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.runtime.Composable

@Composable
expect fun ScrollToTopHandler(scrollState: ScrollState)

@Composable
expect fun ScrollToTopHandler(listState: LazyListState)

@Composable
expect fun ScrollToTopHandler(gridState: LazyGridState)
