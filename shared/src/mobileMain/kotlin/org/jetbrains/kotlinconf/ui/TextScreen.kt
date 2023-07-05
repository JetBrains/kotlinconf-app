package org.jetbrains.kotlinconf.android.ui

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.platform.*
import androidx.compose.ui.text.*
import androidx.compose.ui.tooling.preview.*
import androidx.compose.ui.unit.*
import org.jetbrains.kotlinconf.android.theme.*


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

@Composable
@Preview(showSystemUi = true)
fun MobileAppDescriptionPreview() {
    KotlinConfTheme {
        TextScreen("Title", "FOOOO", buildAnnotatedString {
            append("Hello world".repeat(1000))
        })
    }
}
