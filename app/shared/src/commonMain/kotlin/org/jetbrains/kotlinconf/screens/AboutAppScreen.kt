package org.jetbrains.kotlinconf.screens


import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import dev.zacsweers.metrox.viewmodel.metroViewModel
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource
import org.jetbrains.kotlinconf.LocalAppGraph
import org.jetbrains.kotlinconf.ScreenWithTitle
import org.jetbrains.kotlinconf.URLs
import org.jetbrains.kotlinconf.flags.Flags
import org.jetbrains.kotlinconf.flags.LocalFlags
import org.jetbrains.kotlinconf.generated.resources.Res
import org.jetbrains.kotlinconf.generated.resources.about_app_description
import org.jetbrains.kotlinconf.generated.resources.about_app_link_github
import org.jetbrains.kotlinconf.generated.resources.about_app_link_licenses
import org.jetbrains.kotlinconf.generated.resources.about_app_link_privacy_notice
import org.jetbrains.kotlinconf.generated.resources.about_app_link_rate
import org.jetbrains.kotlinconf.generated.resources.about_app_link_terms_of_use
import org.jetbrains.kotlinconf.generated.resources.about_app_made_with_junie
import org.jetbrains.kotlinconf.generated.resources.about_app_title
import org.jetbrains.kotlinconf.generated.resources.app_version
import org.jetbrains.kotlinconf.generated.resources.arrow_up_right_24
import org.jetbrains.kotlinconf.generated.resources.made_with_junie
import org.jetbrains.kotlinconf.ui.components.Button
import org.jetbrains.kotlinconf.ui.components.PageMenuItem
import org.jetbrains.kotlinconf.ui.components.Text
import org.jetbrains.kotlinconf.ui.theme.KotlinConfTheme
import org.jetbrains.kotlinconf.utils.getStoreUrl
import org.jetbrains.kotlinconf.utils.topInsetPadding

@Composable
fun AboutAppScreen(
    onBack: () -> Unit,
    onGitHubRepo: () -> Unit,
    onRateApp: () -> Unit,
    onPrivacyNotice: () -> Unit,
    onTermsOfUse: () -> Unit,
    onLicenses: () -> Unit,
    onJunie: () -> Unit,
    onDeveloperMenu: (skipWarningDelay: Boolean) -> Unit,
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
                    .clip(KotlinConfTheme.shapes.roundedCornerMd)
                    .clickable {
                        clipboardManager.setText(AnnotatedString(appVersion))

                        tapCount++
                        if (tapCount >= 5) {
                            tapCount = 0
                            onDeveloperMenu(false)
                        }
                    }
                    .padding(16.dp)
            )

            DebugInfo(
                onDeveloperMenu = { onDeveloperMenu(true) },
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )

            Image(
                imageVector = vectorResource(Res.drawable.made_with_junie),
                contentDescription = stringResource(Res.string.about_app_made_with_junie),
                modifier = Modifier.align(Alignment.CenterHorizontally)
                    .clip(KotlinConfTheme.shapes.roundedCornerMd)
                    .clickable { onJunie() }
                    .padding(8.dp)
            )
        }
    }
}

@Composable
private fun DebugInfo(
    onDeveloperMenu: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val baseUrl = LocalAppGraph.current.baseUrl
    val flagsManager = LocalAppGraph.current.flagsManager
    val currentFlags = LocalFlags.current
    val platformFlags = flagsManager.platformFlags
    if (baseUrl != URLs.PRODUCTION_URL || currentFlags != platformFlags) {
        Column(
            modifier = modifier.widthIn(max = 500.dp)
                .clip(KotlinConfTheme.shapes.roundedCornerMd)
                .border(
                    1.dp,
                    KotlinConfTheme.colors.strokeHalf,
                    KotlinConfTheme.shapes.roundedCornerMd,
                )
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            val highlightColor = KotlinConfTheme.colors.orangeText

            val label = when (baseUrl) {
                URLs.PRODUCTION_URL -> "Prod"
                URLs.STAGING_URL -> "Staging"
                URLs.LOCAL_URL -> "Local"
                URLs.ANDROID_LOCAL_URL -> "Local (Android)"
                else -> "Unknown"
            }
            Text(
                text = buildAnnotatedString {
                    append("$label ")
                    withStyle(SpanStyle(color = highlightColor)) {
                        append(baseUrl)
                    }
                },
            )
            FlagsTable(currentFlags, platformFlags, highlightColor)
            Row {
                Button(
                    label = "Reset Flags",
                    onClick = { flagsManager.resetFlags() },
                    primary = true,
                    primaryBackground = highlightColor,
                )
                Spacer(Modifier.width(12.dp))
                Button(
                    label = "Open Dev Menu",
                    onClick = onDeveloperMenu,
                    primary = true,
                    primaryBackground = highlightColor,
                )
            }
        }
    }
}

@Composable
private fun FlagsTable(current: Flags, platform: Flags, highlightColor: Color) {
    val currentEntries = current.toString().substringAfter('(').substringBefore(')').split(", ")
    val platformEntries = platform.toString().substringAfter('(').substringBefore(')').split(", ")
    val pairs = currentEntries.zip(platformEntries).map { (cur, plat) ->
        val eqIndex = cur.indexOf('=')
        Triple(cur.substring(0, eqIndex), cur.substring(eqIndex + 1), cur != plat)
    }
    Column {
        for ((key, value, changed) in pairs) {
            Row {
                Text(
                    text = key,
                    modifier = Modifier.weight(1f),
                )
                Text(
                    text = value,
                    color = if (changed) highlightColor else KotlinConfTheme.colors.primaryText,
                    style = if (changed) {
                        KotlinConfTheme.typography.text1.copy(fontWeight = FontWeight.Bold)
                    } else {
                        KotlinConfTheme.typography.text1
                    },
                )
            }
        }
    }
}
