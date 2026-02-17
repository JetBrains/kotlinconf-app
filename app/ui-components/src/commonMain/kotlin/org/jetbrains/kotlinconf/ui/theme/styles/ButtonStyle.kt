@file:OptIn(ExperimentalFoundationStyleApi::class)

package org.jetbrains.kotlinconf.ui.theme.styles

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.style.ExperimentalFoundationStyleApi
import androidx.compose.foundation.style.MutableStyleState
import androidx.compose.foundation.style.Style
import androidx.compose.foundation.style.StyleScope
import androidx.compose.foundation.style.StyleStateKey
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import org.jetbrains.kotlinconf.ui.theme.LocalColors

private val ButtonShape = RoundedCornerShape(percent = 100)

internal val PrimaryStateKey = StyleStateKey(false)

internal var MutableStyleState.isPrimary: Boolean
    get() = this[PrimaryStateKey]
    set(value) { this[PrimaryStateKey] = value }

fun StyleScope.primary(style: Style) {
    state(PrimaryStateKey, style) { key, state -> state[key] }
}

val DefaultButtonStyle = Style {
    shape(ButtonShape)
    border(1.dp, LocalColors.currentValue.strokeHalf)
    background(Color.Transparent)
    contentColor(LocalColors.currentValue.primaryText)
    contentPaddingHorizontal(32.dp)

    primary {
        animate {
            borderColor(Color.Transparent)
            background(LocalColors.currentValue.primaryBackground)
            contentColor(LocalColors.currentValue.primaryTextWhiteFixed)
        }
    }
}
