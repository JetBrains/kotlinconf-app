package org.jetbrains.kotlinconf.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import kotlinconfapp.ui_components.generated.resources.Res
import kotlinconfapp.ui_components.generated.resources.bookmark_24
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.jetbrains.kotlinconf.ui.theme.KotlinConfTheme
import org.jetbrains.kotlinconf.ui.theme.PreviewHelper

@Composable
fun TopMenuButton(
    selected: Boolean,
    onSelect: (Boolean) -> Unit,
    contentDescription: String?,
    modifier: Modifier = Modifier,
) {
    val backgroundColor by animateColorAsState(
        if (selected) KotlinConfTheme.colors.primaryBackground
        else Color.Transparent
    )
    val iconColor by animateColorAsState(
        if (selected) KotlinConfTheme.colors.primaryTextInverted
        else KotlinConfTheme.colors.primaryText
    )

    Icon(
        // TODO review icon sizing later, https://github.com/JetBrains/kotlinconf-app/issues/175
        modifier = modifier
            .size(30.dp)
            .clip(CircleShape)
            .clickable(onClick = { onSelect(!selected) })
            .background(backgroundColor)
            .padding(4.dp),
        painter = painterResource(Res.drawable.bookmark_24),
        contentDescription = contentDescription,
        tint = iconColor,
    )
}

@Preview
@Composable
private fun TopMenuButtonPreview() {
    PreviewHelper {
        var state1 by remember { mutableStateOf(false) }
        TopMenuButton(state1, { state1 = it }, null)

        var state2 by remember { mutableStateOf(true) }
        TopMenuButton(state2, { state2 = it }, null)
    }
}
