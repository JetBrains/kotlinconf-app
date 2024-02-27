package org.jetbrains.kotlinconf.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import kotlinconfapp.shared.generated.resources.Res
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.kotlinconf.ui.theme.blackWhite
import org.jetbrains.kotlinconf.ui.theme.grey5Black
import org.jetbrains.kotlinconf.ui.theme.greyGrey20
import org.jetbrains.kotlinconf.ui.theme.greyGrey5
import org.jetbrains.kotlinconf.ui.theme.greyWhite
import org.jetbrains.kotlinconf.ui.theme.whiteGrey
import org.jetbrains.kotlinconf.ui.components.TextContent
import org.jetbrains.kotlinconf.ui.components.TextTitle

@OptIn(ExperimentalResourceApi::class)
@Composable
fun PrivacyPolicyScreen(onClose: () -> Unit) {
    Box(Modifier.fillMaxSize()) {
        Column(
            Modifier
                .fillMaxSize()
                .background(color = MaterialTheme.colors.whiteGrey)
        ) {
            Column(Modifier.background(color = MaterialTheme.colors.grey5Black)) {
                Text(
                    text = stringResource(Res.string.detailed_privacy_policy_title),
                    style = MaterialTheme.typography.h2.copy(color = MaterialTheme.colors.greyWhite),
                    modifier = Modifier.padding(top = 24.dp, start = 16.dp, end = 16.dp)
                )

                Text(
                    stringResource(Res.string.detailed_privacy_version),
                    style = MaterialTheme.typography.body2.copy(color = MaterialTheme.colors.greyGrey20),
                    modifier = Modifier.padding(all = 16.dp)
                )
            }
            HDivider()

            MarkdownPrivacyContent()

            Spacer(Modifier.height(50.dp))
        }
        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End
        ) {
            IconButton(onClick = onClose, Modifier.size(48.dp, 48.dp)) {
                Icon(
                    painter = Res.drawable.close.painter(),
                    "Right",
                    tint = MaterialTheme.colors.greyGrey5
                )
            }
        }
    }
}

@OptIn(ExperimentalResourceApi::class)
@Composable
fun MarkdownPrivacyContent() {
    val fullText = stringResource(Res.string.full_privacy_policy)
    val content = markdownString(
        fullText, style = MarkdownStyle(
            linkStyle = SpanStyle(
                color = MaterialTheme.colors.blackWhite,
                textDecoration = TextDecoration.Underline
            ),
            h2 = SpanStyle(
                fontFamily = MaterialTheme.typography.h2.fontFamily,
                fontWeight = MaterialTheme.typography.h2.fontWeight,
                fontSize = MaterialTheme.typography.h2.fontSize,
            ),
            h4 = SpanStyle(
                fontFamily = MaterialTheme.typography.h4.fontFamily,
                fontWeight = MaterialTheme.typography.h4.fontWeight,
                fontSize = MaterialTheme.typography.h4.fontSize,
            )
        )
    )

    TextContent(content)
}
