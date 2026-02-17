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

private val FilterTagShape = RoundedCornerShape(size = 6.dp)

internal val TagSelectedStateKey = StyleStateKey<Boolean>(false)

internal var MutableStyleState.isTagSelected: Boolean
    get() = this[TagSelectedStateKey]
    set(value) { this[TagSelectedStateKey] = value }

fun StyleScope.tagSelected(style: Style) {
    state(TagSelectedStateKey, style) { key, state -> state[key] }
}

val DefaultFilterTagStyle = Style {
    shape(FilterTagShape)
    border(1.dp, LocalColors.currentValue.strokeFull)
    background(LocalColors.currentValue.mainBackground)
    contentColor(LocalColors.currentValue.primaryText)
    contentPaddingHorizontal(12.dp)
    contentPaddingVertical(10.dp)

    tagSelected {
        animate {
            borderColor(Color.Transparent)
            background(LocalColors.currentValue.primaryBackground)
            contentColor(LocalColors.currentValue.primaryTextWhiteFixed)
        }
    }
}
