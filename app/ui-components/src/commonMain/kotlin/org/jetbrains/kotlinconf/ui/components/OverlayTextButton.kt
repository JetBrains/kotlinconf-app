package org.jetbrains.kotlinconf.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.kotlinconf.ui.generated.resources.UiRes
import org.jetbrains.kotlinconf.ui.generated.resources.arrow_up_right_24
import org.jetbrains.kotlinconf.ui.theme.KotlinConfTheme
import org.jetbrains.kotlinconf.ui.theme.PreviewHelper

private val ActionButtonShape = RoundedCornerShape(100)

@Composable
fun OverlayTextButton(
    label: String,
    icon: DrawableResource,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier
            .heightIn(min = 48.dp)
            .clip(ActionButtonShape)
            .border(1.dp, KotlinConfTheme.colors.strokeHalf, ActionButtonShape)
            .clickable(onClick = onClick, role = Role.Button)
            .background(KotlinConfTheme.colors.mainBackground)
            .padding(vertical = 6.dp)
            .padding(start = 24.dp, end = 16.dp),
        contentAlignment = Alignment.Center,
    ) {
        Action(
            label = label,
            icon = icon,
            size = ActionSize.Large,
            onClick = null,
        )
    }
}

@Preview
@Composable
internal fun ActionButtonPreview() {
    PreviewHelper {
        OverlayTextButton(
            label = "How to find the venue",
            icon = UiRes.drawable.arrow_up_right_24,
            onClick = {},
        )
    }
}
