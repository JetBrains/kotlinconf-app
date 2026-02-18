package org.jetbrains.kotlinconf.ui.components

import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.SolidColor
import org.jetbrains.kotlinconf.ui.theme.KotlinConfTheme

@Composable
internal fun SearchInput(
    searchValue: String,
    onSearchValueChange: (String) -> Unit,
    hint: String,
    focusRequester: FocusRequester,
    modifier: Modifier = Modifier,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.CenterStart,
    ) {
        BasicTextField(
            value = searchValue,
            onValueChange = { onSearchValueChange(it) },
            modifier = Modifier
                .fillMaxWidth()
                .focusRequester(focusRequester),
            interactionSource = interactionSource,
            singleLine = true,
            textStyle = KotlinConfTheme.typography.text1
                .copy(color = KotlinConfTheme.colors.primaryText),
            cursorBrush = SolidColor(KotlinConfTheme.colors.primaryText),
        )
        androidx.compose.animation.AnimatedVisibility(
            searchValue.isEmpty(),
            enter = fadeIn(tween(10)),
            exit = fadeOut(tween(10)),
        ) {
            Text(
                text = hint,
                style = KotlinConfTheme.typography.text1,
                color = KotlinConfTheme.colors.placeholderText
            )
        }
    }
}
