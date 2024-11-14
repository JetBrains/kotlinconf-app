package org.jetbrains.kotlinconf.ui.components

import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import org.jetbrains.kotlinconf.ui.theme.KotlinConfTheme

/**
 * A simple text component that uses defaults from [KotlinConfTheme],
 * and accepts a simple [color] parameter to set the text color.
 *
 * TODO rename to Text once we don't depend on Material in the code consuming this component
 */
@Composable
fun StyledText(
    text: String,
    modifier: Modifier = Modifier,
    color: Color = KotlinConfTheme.colors.primaryText,
    style: TextStyle = KotlinConfTheme.typography.text1,
) {
    BasicText(
        text = text,
        modifier = modifier,
        style = style,
        color = { color }
    )
}
