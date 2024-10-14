package org.jetbrains.kotlinconf.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.kotlinconf.ui.painter
import org.jetbrains.kotlinconf.ui.theme.greyWhite
import org.jetbrains.kotlinconf.ui.theme.orange
import org.jetbrains.kotlinconf.ui.theme.tagColor

@OptIn(ExperimentalResourceApi::class)
@Composable
fun Tag(
    icon: DrawableResource?,
    text: String,
    dimmed: Boolean = false,
    iconColor: Color = orange,
    isActive: Boolean = false,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .background(
                if (isActive) orange else MaterialTheme.colors.tagColor,
                shape = RoundedCornerShape(4.dp),
            )
            .alpha(if (dimmed) 0.5f else 1f),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (icon != null) {
            Icon(
                painter = icon.painter(), contentDescription = null, tint = iconColor,
                modifier = Modifier
                    .padding(start = 4.dp)
                    .size(16.dp)
            )
        }
        Text(
            text = text,
            modifier = Modifier
                .padding(start = 4.dp, end = 4.dp, top = 2.dp, bottom = 2.dp),
            style = MaterialTheme.typography.body2,
            color = MaterialTheme.colors.greyWhite
        )
    }
}

@OptIn(ExperimentalResourceApi::class)
@Composable
fun LightningTalk(title: String, dimmed: Boolean = false) {
    Tag(null, title, dimmed)
}

