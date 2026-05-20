package org.jetbrains.kotlinconf

import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import org.jetbrains.kotlinconf.ui.components.MainHeaderContainerState

/**
 * It handles Ctrl/Cmd + F key events when the component is focused.
 */
@Composable
expect fun Modifier.searchShortcut(onTriggered: () -> Unit): Modifier