package org.jetbrains.kotlinconf.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.ClickableText
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.kotlinconf.theme.t2
import org.jetbrains.kotlinconf.theme.grey5Black
import org.jetbrains.kotlinconf.theme.greyGrey20
import org.jetbrains.kotlinconf.theme.whiteGrey


@OptIn(ExperimentalResourceApi::class)
@Composable
fun TextScreen(
    title: String,
    titleBar: String?,
    description: AnnotatedString,
    onBackClick: () -> Unit = {}
) {
    val uriHandler = LocalUriHandler.current

    Column(
        modifier = Modifier
            .background(MaterialTheme.colors.whiteGrey)
            .fillMaxHeight()
            .fillMaxWidth()
    ) {
        NavigationBar(
            title = title, isLeftVisible = true, onLeftClick = onBackClick, isRightVisible = false
        )
        if (titleBar != null) {
            Column(
                Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colors.grey5Black)
            ) {
                Text(
                    titleBar.uppercase(),
                    style = MaterialTheme.typography.h2,
                    modifier = Modifier.padding(16.dp)
                )
                HDivider()
            }
        }
        Column(
            Modifier
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            ClickableText(
                description,
                style = MaterialTheme.typography.t2.copy(
                    color = MaterialTheme.colors.greyGrey20
                ),
                onClick = {
                    description.getStringAnnotations("link", it, it).firstOrNull()?.let {
                        uriHandler.openUri(it.item)
                    }
                }
            )
        }
    }
}