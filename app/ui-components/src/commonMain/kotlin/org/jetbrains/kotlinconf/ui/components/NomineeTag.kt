package org.jetbrains.kotlinconf.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import org.jetbrains.kotlinconf.ui.theme.KotlinConfTheme
import org.jetbrains.kotlinconf.ui.theme.PreviewHelper

@Composable
fun NomineeTag(
    label: String,
    winner: Boolean,
    modifier: Modifier = Modifier,
) {
    val backgroundColor by animateColorAsState(
        if (winner) KotlinConfTheme.colors.primaryBackground
        else KotlinConfTheme.colors.tileBackground,
    )
    val textColor by animateColorAsState(
        if (winner) KotlinConfTheme.colors.primaryTextWhiteFixed
        else KotlinConfTheme.colors.secondaryText,
    )

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(bottomStart = 4.dp, bottomEnd = 4.dp))
            .background(backgroundColor)
            .padding(horizontal = 8.dp, vertical = 2.dp)
            .heightIn(min = 20.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            label,
            style = KotlinConfTheme.typography.text2,
            color = textColor,
        )
    }
}

@Preview
@Composable
internal fun NomineeTagPreview() {
    PreviewHelper {
        var state1 by remember { mutableStateOf(false) }
        NomineeTag("Finalist", state1)

        var state2 by remember { mutableStateOf(true) }
        NomineeTag("Winner", state2)
    }
}
