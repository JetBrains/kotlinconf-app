package org.jetbrains.kotlinconf.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.jetbrains.kotlinconf.ui.components.MarkdownFileView
import org.jetbrains.kotlinconf.ui.components.NavigationBar
import org.jetbrains.kotlinconf.ui.theme.grey50
import org.jetbrains.kotlinconf.ui.theme.greyWhite
import org.jetbrains.kotlinconf.ui.theme.whiteGrey

@OptIn(ExperimentalResourceApi::class)
@Preview
@Composable
fun AboutAppScreen(
    showAppPrivacyPolicy: () -> Unit,
    showAppTerms: () -> Unit,
    back: () -> Unit
) {
    Column(
        modifier = Modifier
            .background(MaterialTheme.colors.whiteGrey)
            .fillMaxHeight()
            .fillMaxWidth()
    ) {
        NavigationBar(
            title = "’24 mobile app",
            isLeftVisible = true,
            onLeftClick = back,
            isRightVisible = false
        )
        Column(
            Modifier
                .verticalScroll(rememberScrollState())
        ) {
            MarkdownFileView("files/mobile-app-description.md")
            AboutAppFooter(showAppPrivacyPolicy, showAppTerms)
        }


    }
}

@Composable
private fun AboutAppFooter(
    showAppPrivacyPolicy: () -> Unit,
    showAppTerms: () -> Unit
) {
    LocalUriHandler.current
    Column(
        Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colors.whiteGrey)
    ) {
        Text(
            buildAnnotatedString {
                withStyle(SpanStyle(textDecoration = TextDecoration.Underline)) {
                    append("Application Privacy Policy")
                }
            },
            style = MaterialTheme.typography.body2.copy(color = MaterialTheme.colors.greyWhite),
            modifier = Modifier
                .padding(start = 16.dp, end = 16.dp)
                .clickable {
                    showAppPrivacyPolicy()
                }
        )
        Text(
            buildAnnotatedString {
                withStyle(SpanStyle(textDecoration = TextDecoration.Underline)) {
                    append("Application terms and conditions")
                }
            },
            style = MaterialTheme.typography.body2.copy(color = MaterialTheme.colors.greyWhite),
            modifier = Modifier
                .padding(start = 16.dp, top = 24.dp, end = 16.dp, bottom = 40.dp)
                .clickable {
                    showAppTerms()
                }
        )
    }
}
