package org.jetbrains.kotlinconf.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.kotlinconf.ui.generated.resources.UiRes
import org.jetbrains.kotlinconf.ui.generated.resources.now
import org.jetbrains.kotlinconf.ui.theme.KotlinConfTheme
import org.jetbrains.kotlinconf.ui.theme.PreviewHelper
import org.jetbrains.kotlinconf.ui.utils.PreviewLightDark


@Composable
fun NowLabel(
    modifier: Modifier = Modifier,
    textStyle: TextStyle = KotlinConfTheme.typography.text2,
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            Modifier.size(10.dp)
                .clip(CircleShape)
                .background(KotlinConfTheme.colors.accentText)
        )
        Spacer(Modifier.size(4.dp))
        Text(
            text = stringResource(UiRes.string.now),
            color = KotlinConfTheme.colors.accentText,
            style = textStyle,
            maxLines = 1,
        )
    }
}

@Composable
@PreviewLightDark
private fun NowLabelPreview() = PreviewHelper {
    NowLabel()
}
