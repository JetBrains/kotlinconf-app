package org.jetbrains.kotlinconf.utils

import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

val MAX_WIDTH = 1500.dp

expect object Screen {
    @get:Composable
    val width: Dp
    
    @get:Composable
    val height: Dp
}

@Composable
fun Screen.isTooWide(): Boolean = width >= MAX_WIDTH