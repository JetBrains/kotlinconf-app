package org.jetbrains.kotlinconf.ui.components

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.unit.*
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.kotlinconf.theme.*

@OptIn(ExperimentalResourceApi::class)
@Composable
fun Tag(
    icon: String,
    text: String,
    dimmed: Boolean = false,
    iconColor: Color = orange,
) {
    val iconName = "$icon.xml"
    Row(
        modifier = Modifier
            .background(
                MaterialTheme.colors.grey5Black,
                shape = RoundedCornerShape(4.dp),
            )
            .alpha(if (dimmed) 0.5f else 1f),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(painter = painterResource(iconName), contentDescription = null, tint = iconColor,
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
    Tag("light", title, dimmed)
}

@Composable
fun CodeLab(dimmed: Boolean = false) {
    Tag("aws_labs", "Big Nerd Ranch lab", dimmed, violet)
}

@Composable
fun AWSLab(dimmed: Boolean = false) {
    Tag("aws_labs", "AWS lab", dimmed, violet)
}
