package org.jetbrains.kotlinconf.screens


import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.unit.dp
import kotlinconfapp.shared.generated.resources.Res
import kotlinconfapp.shared.generated.resources.about_app_description
import kotlinconfapp.shared.generated.resources.about_app_link_github
import kotlinconfapp.shared.generated.resources.about_app_link_licenses
import kotlinconfapp.shared.generated.resources.about_app_link_privacy_notice
import kotlinconfapp.shared.generated.resources.about_app_link_rate
import kotlinconfapp.shared.generated.resources.about_app_link_settings
import kotlinconfapp.shared.generated.resources.about_app_link_terms_of_use
import kotlinconfapp.shared.generated.resources.about_app_made_with_junie
import kotlinconfapp.shared.generated.resources.about_app_title
import kotlinconfapp.shared.generated.resources.app_version
import kotlinconfapp.shared.generated.resources.arrow_up_right_24
import kotlinconfapp.shared.generated.resources.kodee_privacy
import kotlinconfapp.shared.generated.resources.made_with_junie
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource
import org.jetbrains.kotlinconf.ScreenWithTitle
import org.jetbrains.kotlinconf.ui.components.PageMenuItem
import org.jetbrains.kotlinconf.ui.components.Text
import org.jetbrains.kotlinconf.ui.theme.KotlinConfTheme
import org.jetbrains.kotlinconf.utils.getStoreUrl

@Composable
fun AboutAppScreen(
    onBack: () -> Unit,
    onGitHubRepo: () -> Unit,
    onRateApp: () -> Unit,
    onSettings: () -> Unit,
    onPrivacyNotice: () -> Unit,
    onTermsOfUse: () -> Unit,
    onLicenses: () -> Unit,
    onJunie: () -> Unit,
    onDeveloperMenu: () -> Unit = {},
) {
    ScreenWithTitle(
        title = stringResource(Res.string.about_app_title),
        onBack = onBack,
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(
                stringResource(Res.string.about_app_description),
                color = KotlinConfTheme.colors.longText,
                modifier = Modifier.padding(vertical = 24.dp),
            )

            PageMenuItem(
                stringResource(Res.string.about_app_link_github),
                drawableEnd = Res.drawable.arrow_up_right_24,
                onClick = onGitHubRepo,
            )

            val storeUrlAvailable = remember { getStoreUrl() != null }
            if (storeUrlAvailable) {
                PageMenuItem(
                    stringResource(Res.string.about_app_link_rate),
                    drawableEnd = Res.drawable.arrow_up_right_24,
                    onClick = onRateApp,
                )
            }

            PageMenuItem(stringResource(Res.string.about_app_link_settings), onClick = onSettings)
            PageMenuItem(stringResource(Res.string.about_app_link_privacy_notice), onClick = onPrivacyNotice)
            PageMenuItem(stringResource(Res.string.about_app_link_terms_of_use), onClick = onTermsOfUse)
            PageMenuItem(stringResource(Res.string.about_app_link_licenses), onClick = onLicenses)

            Spacer(Modifier.height(8.dp))

            val clipboardManager = LocalClipboardManager.current
            val appVersion = stringResource(resource = Res.string.app_version)
            var tapCount by remember { mutableStateOf(0) }
            Text(
                text = appVersion,
                style = KotlinConfTheme.typography.text2,
                color = KotlinConfTheme.colors.primaryText,
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .clip(RoundedCornerShape(8.dp))
                    .clickable {
                        clipboardManager.setText(AnnotatedString(appVersion))

                        tapCount++
                        if (tapCount >= 5) {
                            tapCount = 0
                            onDeveloperMenu()
                        }
                    }
                    .padding(16.dp)
            )

            Image(
                imageVector = vectorResource(Res.drawable.made_with_junie),
                contentDescription = stringResource(Res.string.about_app_made_with_junie),
                modifier = Modifier.align(Alignment.CenterHorizontally)
                    .clip(RoundedCornerShape(8.dp))
                    .clickable { onJunie() }
                    .padding(8.dp)
            )
        }
    }
}
