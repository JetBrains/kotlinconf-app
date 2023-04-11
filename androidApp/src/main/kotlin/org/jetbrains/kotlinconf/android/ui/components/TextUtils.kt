package org.jetbrains.kotlinconf.android.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.platform.*
import androidx.compose.ui.text.*
import androidx.compose.ui.text.font.*
import androidx.compose.ui.unit.*
import org.jetbrains.kotlinconf.android.theme.*

@Composable
fun TextContent(value: AnnotatedString) {
    val uriHandler = LocalUriHandler.current

    ClickableText(
        value,
        style = MaterialTheme.typography.t2.copy(color = MaterialTheme.colors.greyGrey20),
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
        style = MaterialTheme.typography.t2.copy(
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
        style = MaterialTheme.typography.t2.copy(color = MaterialTheme.colors.greyGrey20),
        modifier = Modifier.padding(top = 16.dp, start = 16.dp, end = 16.dp)
    )
}