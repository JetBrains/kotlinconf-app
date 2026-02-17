@file:OptIn(ExperimentalFoundationStyleApi::class)

package org.jetbrains.kotlinconf.ui.components

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.style.ExperimentalFoundationStyleApi
import androidx.compose.foundation.style.MutableStyleState
import androidx.compose.foundation.style.Style
import androidx.compose.foundation.style.styleable
import androidx.compose.foundation.style.then
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.tooling.preview.Preview
import org.jetbrains.kotlinconf.ui.theme.KotlinConfTheme
import org.jetbrains.kotlinconf.ui.theme.PreviewHelper
import org.jetbrains.kotlinconf.ui.theme.styles.isTagSelected

@Composable
fun FilterTag(
    label: String,
    selected: Boolean,
    onSelect: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    style: Style = Style,
) {
    val interactionSource = remember { MutableInteractionSource() }
    val styleState = remember(interactionSource) { MutableStyleState(interactionSource) }
    styleState.isTagSelected = selected

    val textColor = if (selected) KotlinConfTheme.colors.primaryTextWhiteFixed
        else KotlinConfTheme.colors.primaryText

    Box(
        modifier = modifier
            .selectable(
                selected = selected,
                interactionSource = interactionSource,
                indication = null,
                onClick = { onSelect(!selected) },
                role = Role.RadioButton,
            )
            .styleable(styleState, KotlinConfTheme.styles.filterTag then style),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            label,
            style = KotlinConfTheme.typography.text1,
            color = textColor,
        )
    }
}

@Preview
@Composable
internal fun FilterTagPreview() {
    PreviewHelper {
        var state1 by remember { mutableStateOf(false) }
        FilterTag("Label", state1, { state1 = it })

        var state2 by remember { mutableStateOf(true) }
        FilterTag("Label", state2, { state2 = it })
    }
}
