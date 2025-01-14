package org.jetbrains.kotlinconf.screens


import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinconfapp.shared.generated.resources.Res
import kotlinconfapp.shared.generated.resources.about_app_description
import kotlinconfapp.shared.generated.resources.about_app_link_github
import kotlinconfapp.shared.generated.resources.about_app_link_privacy_policy
import kotlinconfapp.shared.generated.resources.about_app_link_rate
import kotlinconfapp.shared.generated.resources.about_app_link_settings
import kotlinconfapp.shared.generated.resources.about_app_link_terms_of_use
import kotlinconfapp.shared.generated.resources.about_app_title
import kotlinconfapp.shared.generated.resources.arrow_left_24
import kotlinconfapp.shared.generated.resources.arrow_up_right_24
import kotlinconfapp.shared.generated.resources.navigate_back
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.kotlinconf.ui.components.Divider
import org.jetbrains.kotlinconf.ui.components.MainHeaderTitleBar
import org.jetbrains.kotlinconf.ui.components.PageMenuItem
import org.jetbrains.kotlinconf.ui.components.StyledText
import org.jetbrains.kotlinconf.ui.components.TopMenuButton
import org.jetbrains.kotlinconf.ui.theme.KotlinConfTheme

@Composable
fun AboutAppScreen(
    onBack: () -> Unit,
    onGitHubRepo: () -> Unit,
    onRateApp: () -> Unit,
    onSettings: () -> Unit,
    onPrivacyPolicy: () -> Unit,
    onTermsOfUse: () -> Unit,
) {
    Column(Modifier.fillMaxSize()) {
        MainHeaderTitleBar(
            title = stringResource(Res.string.about_app_title),
            startContent = {
                TopMenuButton(
                    icon = Res.drawable.arrow_left_24,
                    contentDescription = stringResource(Res.string.navigate_back),
                    onClick = onBack,
                )
            }
        )
        Divider(1.dp, KotlinConfTheme.colors.strokePale)
        Column(
            Modifier
                .verticalScroll(rememberScrollState())
                .fillMaxSize()
                .padding(horizontal = 12.dp)
                .padding(bottom = 12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            StyledText(
                stringResource(Res.string.about_app_description),
                color = KotlinConfTheme.colors.longText,
                modifier = Modifier.padding(vertical = 24.dp),
            )

            PageMenuItem(
                stringResource(Res.string.about_app_link_github),
                drawableResource = Res.drawable.arrow_up_right_24,
                onClick = onGitHubRepo,
            )
            PageMenuItem(
                stringResource(Res.string.about_app_link_rate),
                drawableResource = Res.drawable.arrow_up_right_24,
                onClick = onRateApp,
            )
            PageMenuItem(stringResource(Res.string.about_app_link_settings), onClick = onSettings)
            PageMenuItem(stringResource(Res.string.about_app_link_privacy_policy), onClick = onPrivacyPolicy)
            PageMenuItem(stringResource(Res.string.about_app_link_terms_of_use), onClick = onTermsOfUse)
        }
    }
}
