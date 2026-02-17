@file:OptIn(ExperimentalFoundationStyleApi::class)

package org.jetbrains.kotlinconf.ui.theme

import androidx.compose.foundation.style.ExperimentalFoundationStyleApi
import androidx.compose.foundation.style.Style
import org.jetbrains.kotlinconf.ui.theme.styles.DefaultButtonStyle
import org.jetbrains.kotlinconf.ui.theme.styles.DefaultFilterTagStyle

class Styles(
    val button: Style = DefaultButtonStyle,
    val filterTag: Style = DefaultFilterTagStyle,
)

internal val KotlinConfStyles = Styles()
