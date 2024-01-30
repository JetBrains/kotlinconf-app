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
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.unit.dp
import kotlinconfapp.shared.generated.resources.Res
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.kotlinconf.theme.grey5Black
import org.jetbrains.kotlinconf.theme.greyWhite
import org.jetbrains.kotlinconf.theme.orange
import org.jetbrains.kotlinconf.theme.t2
import org.jetbrains.kotlinconf.theme.violet
import org.jetbrains.kotlinconf.ui.painter

@Composable
fun Tag(
    icon: Painter,
    text: String,
    dimmed: Boolean = false,
    iconColor: Color = orange,
) {
    Row(
        modifier = Modifier
            .background(
                MaterialTheme.colors.grey5Black,
                shape = RoundedCornerShape(4.dp),
            )
            .alpha(if (dimmed) 0.5f else 1f),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            painter = icon, contentDescription = null, tint = iconColor,
            modifier = Modifier
                .padding(start = 4.dp)
                .size(16.dp)
        )
        Text(
            text = text,
            modifier = Modifier
                .padding(start = 4.dp, end = 4.dp, top = 2.dp, bottom = 2.dp),
            style = MaterialTheme.typography.t2,
            color = MaterialTheme.colors.greyWhite
        )
    }
}

@OptIn(ExperimentalResourceApi::class)
@Composable
fun LightningTalk(title: String, dimmed: Boolean = false) {
    Tag(Res.drawable.light.painter(), title, dimmed)
}

@OptIn(ExperimentalResourceApi::class)
@Composable
fun CodeLab(dimmed: Boolean = false) {
    Tag(Res.drawable.aws_lab.painter(), "Big Nerd Ranch lab", dimmed, violet)
}

@OptIn(ExperimentalResourceApi::class)
@Composable
fun AWSLab(dimmed: Boolean = false) {
    Tag(Res.drawable.aws_lab.painter(), "AWS lab", dimmed, violet)
}
