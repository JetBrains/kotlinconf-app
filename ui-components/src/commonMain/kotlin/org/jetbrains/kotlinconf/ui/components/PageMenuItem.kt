package org.jetbrains.kotlinconf.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import kotlinconfapp.ui_components.generated.resources.Res
import kotlinconfapp.ui_components.generated.resources.arrow_right_24
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.jetbrains.kotlinconf.ui.theme.KotlinConfTheme
import org.jetbrains.kotlinconf.ui.theme.PreviewHelper

private val PageMenuItemShape = RoundedCornerShape(8.dp)

@Composable
fun PageMenuItem(
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    drawableResource: DrawableResource = Res.drawable.arrow_right_24,
) {
    Row(
        modifier
            .fillMaxWidth()
            .clip(PageMenuItemShape)
            .clickable(onClick = onClick)
            .background(KotlinConfTheme.colors.tileBackground)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        StyledText(
            text = label,
            style = KotlinConfTheme.typography.h3,
        )
        Icon(
            // TODO review icon sizing later, https://github.com/JetBrains/kotlinconf-app/issues/175
            modifier = modifier
                .size(24.dp),
            painter = painterResource(drawableResource),
            contentDescription = null,
            tint = KotlinConfTheme.colors.primaryText,
        )
    }
}


@Preview
@Composable
internal fun PageMenuItemPreview() {
    PreviewHelper {
        PageMenuItem("Name", onClick = {})
    }
}
