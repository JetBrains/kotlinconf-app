package org.jetbrains.kotlinconf.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.jetbrains.kotlinconf.ui.theme.KotlinConfTheme
import org.jetbrains.kotlinconf.ui.theme.PreviewHelper


private val FilterTagShape = RoundedCornerShape(size = 6.dp)

@Composable
fun FilterTag(
    label: String,
    selected: Boolean,
    onSelect: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    val backgroundColor by animateColorAsState(
        if (selected) KotlinConfTheme.colors.primaryBackground
        else KotlinConfTheme.colors.mainBackground,
        ColorSpringSpec,
    )
    val textColor by animateColorAsState(
        if (selected) KotlinConfTheme.colors.primaryTextInverted
        else KotlinConfTheme.colors.primaryText,
        ColorSpringSpec,
    )
    val strokeColor by animateColorAsState(
        if (selected) Color.Transparent
        else KotlinConfTheme.colors.strokeFull,
        ColorSpringSpec,
    )

    Box(
        modifier = modifier
            .clip(FilterTagShape)
            .border(
                width = 1.dp,
                color = strokeColor,
                shape = FilterTagShape,
            )
            .selectable(
                selected = selected,
                onClick = { onSelect(!selected) },
                role = Role.RadioButton
            )
            .background(backgroundColor)
            .padding(horizontal = 12.dp, vertical = 10.dp),
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
