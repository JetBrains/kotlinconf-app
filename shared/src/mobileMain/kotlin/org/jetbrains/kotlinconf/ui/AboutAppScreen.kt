package org.jetbrains.kotlinconf.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
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
import kotlinconfapp.shared.generated.resources.Res
import kotlinconfapp.shared.generated.resources.app_privacy_policy
import kotlinconfapp.shared.generated.resources.app_terms
import kotlinconfapp.shared.generated.resources.app_terms_of_use
import kotlinconfapp.shared.generated.resources.arrow_right
import kotlinconfapp.shared.generated.resources.mobile_app
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.jetbrains.kotlinconf.ui.components.MarkdownFileView
import org.jetbrains.kotlinconf.ui.components.MenuItem
import org.jetbrains.kotlinconf.ui.components.NavigationBar
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
            title= stringResource(Res.string.mobile_app),
            isLeftVisible = true,
            onLeftClick = back,
            isRightVisible = false
        )
        Column(
            Modifier
                .verticalScroll(rememberScrollState())
        ) {
            MarkdownFileView("files/app-description.md")
            AboutAppFooter(showAppPrivacyPolicy, showAppTerms)
        }
    }
}

@OptIn(ExperimentalResourceApi::class)
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
        HDivider()
        MenuItem(
            text = stringResource(Res.string.app_privacy_policy),
            icon = Res.drawable.arrow_right,
            dimmed = true,
            onClick = showAppPrivacyPolicy
        )
        HDivider()
        MenuItem(
            text = stringResource(Res.string.app_terms_of_use),
            icon = Res.drawable.arrow_right,
            dimmed = true,
            onClick = showAppTerms
        )
        HDivider()
    }
}
