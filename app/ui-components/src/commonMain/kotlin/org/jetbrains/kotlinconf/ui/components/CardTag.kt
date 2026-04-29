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
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import org.jetbrains.kotlinconf.ui.theme.KotlinConfTheme
import org.jetbrains.kotlinconf.ui.theme.PreviewHelper
import androidx.compose.ui.tooling.preview.PreviewLightDark

private val CardTagShape = RoundedCornerShape(size = 4.dp)

enum class CardTagSize {
    Normal, Large,
}

@Composable
fun CardTag(
    label: String,
    selected: Boolean,
    modifier: Modifier = Modifier,
    size: CardTagSize = CardTagSize.Normal,
) {
    val backgroundColor by animateColorAsState(
        if (selected) KotlinConfTheme.colors.primaryBackground
        else KotlinConfTheme.colors.tileBackground,
        ColorSpringSpec,
    )
    val textColor by animateColorAsState(
        if (selected) KotlinConfTheme.colors.primaryTextWhiteFixed
        else KotlinConfTheme.colors.secondaryText,
        ColorSpringSpec,
    )

    Box(
        modifier = modifier
            .heightIn(
                min = when (size) {
                    CardTagSize.Normal -> 20.dp
                    CardTagSize.Large -> 24.dp
                }
            )
            .clip(CardTagShape)
            .background(backgroundColor)
            .padding(
                horizontal = when (size) {
                    CardTagSize.Normal -> 4.dp
                    CardTagSize.Large -> 8.dp
                }
            ),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            label,
            style = KotlinConfTheme.typography.text2,
            color = textColor,
        )
    }
}

private class CardTagSizeProvider : PreviewParameterProvider<CardTagSize> {
    override val values = sequenceOf(CardTagSize.Normal, CardTagSize.Large)
    override fun getDisplayName(index: Int) = values.elementAt(index).name
}

@PreviewLightDark
@Composable
private fun CardTagUnselectedPreview(
    @PreviewParameter(CardTagSizeProvider::class) size: CardTagSize,
) = PreviewHelper {
    var selected by remember { mutableStateOf(false) }
    CardTag("Label", selected = selected, Modifier.clickable { selected = !selected }, size = size)
}

@PreviewLightDark
@Composable
private fun CardTagSelectedPreview(
    @PreviewParameter(CardTagSizeProvider::class) size: CardTagSize,
) = PreviewHelper {
    var selected by remember { mutableStateOf(true) }
    CardTag("Label", selected = selected, Modifier.clickable { selected = !selected }, size = size)
}
