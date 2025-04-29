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
import androidx.compose.ui.unit.dp
import kotlinconfapp.ui_components.generated.resources.Res
import kotlinconfapp.ui_components.generated.resources.now
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.jetbrains.kotlinconf.ui.theme.KotlinConfTheme
import org.jetbrains.kotlinconf.ui.theme.PreviewHelper


@Composable
fun NowLabel(modifier: Modifier = Modifier) {
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
            text = stringResource(Res.string.now),
            color = KotlinConfTheme.colors.accentText,
            style = KotlinConfTheme.typography.text2,
            maxLines = 1,
        )
    }
}

@Composable
@Preview
internal fun NowLabelPreview() {
    PreviewHelper {
        NowLabel()
    }
}
