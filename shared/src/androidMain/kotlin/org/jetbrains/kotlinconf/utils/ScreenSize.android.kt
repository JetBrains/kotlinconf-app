package org.jetbrains.kotlinconf.utils

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

actual object Screen {
    actual val width: Dp
        @Composable get() = LocalConfiguration.current.screenWidthDp.dp
    
    actual val height: Dp
        @Composable get() = LocalConfiguration.current.screenHeightDp.dp
}