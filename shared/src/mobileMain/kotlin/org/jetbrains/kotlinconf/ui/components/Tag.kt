package org.jetbrains.kotlinconf.ui.components

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.unit.*
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.kotlinconf.theme.*

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

@Composable
fun LightningTalk(title: String, dimmed: Boolean = false) {
    Tag(Icons.LIGHT, title, dimmed)
}

@Composable
fun CodeLab(dimmed: Boolean = false) {
    Tag(Icons.AWS_LAB, "Big Nerd Ranch lab", dimmed, violet)
}

@Composable
fun AWSLab(dimmed: Boolean = false) {
    Tag(Icons.AWS_LAB, "AWS lab", dimmed, violet)
}
