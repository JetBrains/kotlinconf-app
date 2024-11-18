package org.jetbrains.kotlinconf.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.jetbrains.kotlinconf.ui.theme.KotlinConfTheme
import org.jetbrains.kotlinconf.ui.theme.PreviewHelper

private val CardTagShape = RoundedCornerShape(size = 4.dp)

@Composable
fun CardTag(
    label: String,
    selected: Boolean,
    onSelect: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    val backgroundColor by animateColorAsState(
        if (selected) KotlinConfTheme.colors.primaryBackground
        else KotlinConfTheme.colors.tileBackground
    )
    val textColor by animateColorAsState(
        if (selected) KotlinConfTheme.colors.primaryTextInverted
        else KotlinConfTheme.colors.secondaryText
    )

    Box(
        modifier = modifier
            .heightIn(min = 20.dp)
            .clip(CardTagShape)
            .clickable(onClick = { onSelect(!selected) })
            .background(backgroundColor)
            .padding(horizontal = 4.dp),
        contentAlignment = Alignment.Center,
    ) {
        StyledText(
            label,
            style = KotlinConfTheme.typography.text2,
            color = textColor,
        )
    }
}

@Preview
@Composable
internal fun CardTagPreview() {
    PreviewHelper {
        var state1 by remember { mutableStateOf(false) }
        CardTag("Label", state1, { state1 = it })

        var state2 by remember { mutableStateOf(true) }
        CardTag("Label", state2, { state2 = it })
    }
}
