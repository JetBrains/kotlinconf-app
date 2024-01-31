package org.jetbrains.kotlinconf.ui.components

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import org.jetbrains.kotlinconf.theme.greyGrey20
import org.jetbrains.kotlinconf.theme.greyWhite

@Composable
fun TextContent(value: AnnotatedString) {
    val uriHandler = LocalUriHandler.current

    ClickableText(
        value,
        style = MaterialTheme.typography.body2.copy(color = MaterialTheme.colors.greyGrey20),
        modifier = Modifier.padding(top = 16.dp, start = 16.dp, end = 16.dp),
        onClick = {
            value.getStringAnnotations("link", it, it).firstOrNull()?.let {
                uriHandler.openUri(it.item)
            }
        }
    )
}


@Composable
fun TextTitle(value: String) {
    Text(
        value,
        style = MaterialTheme.typography.body2.copy(
            color = MaterialTheme.colors.greyWhite,
            fontWeight = FontWeight.Bold
        ),
        modifier = Modifier.padding(top = 24.dp, start = 16.dp, end = 16.dp)
    )
}



@Composable
fun TextContent(value: String) {
    Text(
        value,
        style = MaterialTheme.typography.body2.copy(color = MaterialTheme.colors.greyGrey20),
        modifier = Modifier.padding(top = 16.dp, start = 16.dp, end = 16.dp)
    )
}