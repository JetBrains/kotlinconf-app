@file:OptIn(ExperimentalFoundationStyleApi::class)

package org.jetbrains.kotlinconf.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.hoverable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.style.ExperimentalFoundationStyleApi
import androidx.compose.foundation.style.MutableStyleState
import androidx.compose.foundation.style.Style
import androidx.compose.foundation.style.styleable
import androidx.compose.foundation.style.then
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import org.jetbrains.kotlinconf.ui.theme.KotlinConfTheme
import org.jetbrains.kotlinconf.ui.theme.PreviewHelper
import org.jetbrains.kotlinconf.ui.theme.styles.isPrimary

// Strange: Even though we have a file-level opt in here, every usage of Button
// also requires the same opt in to be added
@Composable
fun Button(
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    primary: Boolean = false,
    enabled: Boolean = true,
    style: Style = Style,
) {
    val interactionSource = remember { MutableInteractionSource() }
    val styleState = remember(interactionSource) { MutableStyleState(interactionSource) }
    styleState.isPrimary = primary

    val textColor = if (primary) KotlinConfTheme.colors.primaryTextWhiteFixed
        else KotlinConfTheme.colors.primaryText

    Box(
        modifier = modifier
            .alpha(if (enabled) 1f else 0.5f)
            .heightIn(min = 56.dp)
            .hoverable(interactionSource)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                enabled = enabled,
                role = Role.Button,
                onClick = onClick,
            )
            .styleable(styleState, KotlinConfTheme.styles.button then style),
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
internal fun ButtonPreview() {
    PreviewHelper {
        Button("Primary", { }, primary = true)
        Button("Secondary", { }, primary = false)
    }
}
