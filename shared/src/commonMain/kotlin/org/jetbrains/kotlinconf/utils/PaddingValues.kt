package org.jetbrains.kotlinconf.utils

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalLayoutDirection

@Composable
fun bottomInsetPadding() = WindowInsets.safeDrawing.only(WindowInsetsSides.Bottom).asPaddingValues()

@Composable
fun topInsetPadding() = WindowInsets.safeDrawing.only(WindowInsetsSides.Top).asPaddingValues()

@Composable
operator fun PaddingValues.plus(other: PaddingValues): PaddingValues {
    val layoutDir = LocalLayoutDirection.current
    return PaddingValues(
        start = calculateStartPadding(layoutDir) + other.calculateStartPadding(layoutDir),
        top = calculateTopPadding() + other.calculateTopPadding(),
        end = calculateEndPadding(layoutDir) + other.calculateEndPadding(layoutDir),
        bottom = calculateBottomPadding() + other.calculateBottomPadding()
    )
}
