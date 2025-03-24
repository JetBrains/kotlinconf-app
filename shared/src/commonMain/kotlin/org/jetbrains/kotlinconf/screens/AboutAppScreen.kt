package org.jetbrains.kotlinconf.screens


import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinconfapp.shared.generated.resources.Res
import kotlinconfapp.shared.generated.resources.about_app_description
import kotlinconfapp.shared.generated.resources.about_app_link_github
import kotlinconfapp.shared.generated.resources.about_app_link_licenses
import kotlinconfapp.shared.generated.resources.about_app_link_privacy_policy
import kotlinconfapp.shared.generated.resources.about_app_link_rate
import kotlinconfapp.shared.generated.resources.about_app_link_settings
import kotlinconfapp.shared.generated.resources.about_app_link_terms_of_use
import kotlinconfapp.shared.generated.resources.about_app_title
import kotlinconfapp.shared.generated.resources.app_version
import kotlinconfapp.shared.generated.resources.arrow_up_right_24
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.kotlinconf.ScreenWithTitle
import org.jetbrains.kotlinconf.ui.components.PageMenuItem
import org.jetbrains.kotlinconf.ui.components.StyledText
import org.jetbrains.kotlinconf.ui.theme.KotlinConfTheme
import org.jetbrains.kotlinconf.utils.getStoreUrl

@Composable
fun AboutAppScreen(
    onBack: () -> Unit,
    onGitHubRepo: () -> Unit,
    onRateApp: () -> Unit,
    onSettings: () -> Unit,
    onPrivacyPolicy: () -> Unit,
    onTermsOfUse: () -> Unit,
    onLicenses: () -> Unit,
) {
    ScreenWithTitle(
        title = stringResource(Res.string.about_app_title),
        onBack = onBack,
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
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

            val storeUrlAvailable = remember { getStoreUrl() != null }
            if (storeUrlAvailable) {
                PageMenuItem(
                    stringResource(Res.string.about_app_link_rate),
                    drawableResource = Res.drawable.arrow_up_right_24,
                    onClick = onRateApp,
                )
            }

            PageMenuItem(stringResource(Res.string.about_app_link_settings), onClick = onSettings)
            PageMenuItem(stringResource(Res.string.about_app_link_privacy_policy), onClick = onPrivacyPolicy)
            PageMenuItem(stringResource(Res.string.about_app_link_terms_of_use), onClick = onTermsOfUse)
            PageMenuItem(stringResource(Res.string.about_app_link_licenses), onClick = onLicenses)

            StyledText(
                text = stringResource(resource = Res.string.app_version),
                style = KotlinConfTheme.typography.text2,
                color = KotlinConfTheme.colors.secondaryText,
                modifier = Modifier.align(Alignment.CenterHorizontally).padding(top = 16.dp)
            )
        }
    }
}
