package org.jetbrains.kotlinconf.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource
import org.jetbrains.kotlinconf.generated.resources.Res
import org.jetbrains.kotlinconf.generated.resources.arrow_up_right_24
import org.jetbrains.kotlinconf.generated.resources.bluesky
import org.jetbrains.kotlinconf.generated.resources.info_link_about_app
import org.jetbrains.kotlinconf.generated.resources.info_link_about_conf
import org.jetbrains.kotlinconf.generated.resources.info_link_code_of_conduct
import org.jetbrains.kotlinconf.generated.resources.info_link_description_bluesky
import org.jetbrains.kotlinconf.generated.resources.info_link_description_slack
import org.jetbrains.kotlinconf.generated.resources.info_link_description_twitter
import org.jetbrains.kotlinconf.generated.resources.info_link_how_to_find_venue
import org.jetbrains.kotlinconf.generated.resources.info_link_partners
import org.jetbrains.kotlinconf.generated.resources.info_link_settings
import org.jetbrains.kotlinconf.generated.resources.info_title
import org.jetbrains.kotlinconf.generated.resources.slack
import org.jetbrains.kotlinconf.generated.resources.twitter
import org.jetbrains.kotlinconf.ui.components.HorizontalDivider
import org.jetbrains.kotlinconf.ui.components.MainHeaderTitleBar
import org.jetbrains.kotlinconf.ui.components.PageMenuItem
import org.jetbrains.kotlinconf.ui.theme.KotlinConfTheme
import org.jetbrains.kotlinconf.utils.bottomInsetPadding
import org.jetbrains.kotlinconf.utils.plus
import org.jetbrains.kotlinconf.utils.topInsetPadding
import org.koin.compose.viewmodel.koinViewModel


@Composable
fun InfoScreen(
    onAboutConf: () -> Unit,
    onHowToFindVenue: (String) -> Unit,
    onAboutApp: () -> Unit,
    onOurPartners: () -> Unit,
    onCodeOfConduct: () -> Unit,
    onTwitter: () -> Unit,
    onSlack: () -> Unit,
    onBluesky: () -> Unit,
    onSettings: () -> Unit,
    viewModel: InfoViewModel = koinViewModel(),
) {
    val venueAddress = viewModel.venueAddress.collectAsStateWithLifecycle().value
    val isDark = KotlinConfTheme.colors.isDark
    val images = viewModel.images.collectAsStateWithLifecycle().value
    val logoUrl = if (isDark) images?.kotlinConfDark else images?.kotlinConfLight
    Column(
        Modifier.fillMaxSize()
            .background(color = KotlinConfTheme.colors.mainBackground)
            .padding(topInsetPadding())
    ) {
        MainHeaderTitleBar(stringResource(Res.string.info_title))
        HorizontalDivider(1.dp, KotlinConfTheme.colors.strokePale)

        Column(
            Modifier
                .verticalScroll(rememberScrollState())
                .fillMaxSize()
                .padding(PaddingValues(12.dp) + bottomInsetPadding()),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            if (logoUrl != null) {
                AsyncImage(
                    model = logoUrl,
                    contentDescription = null,
                    modifier = Modifier
                        .padding(24.dp)
                        .widthIn(max = 360.dp)
                )
            }
            PageMenuItem(stringResource(Res.string.info_link_about_conf), onClick = onAboutConf)
            if (venueAddress != null) {
                PageMenuItem(
                    stringResource(Res.string.info_link_how_to_find_venue),
                    onClick = { onHowToFindVenue(venueAddress) },
                    drawableEnd = Res.drawable.arrow_up_right_24,
                )
            }
            PageMenuItem(stringResource(Res.string.info_link_about_app), onClick = onAboutApp)
            PageMenuItem(stringResource(Res.string.info_link_settings), onClick = onSettings)
            PageMenuItem(stringResource(Res.string.info_link_partners), onClick = onOurPartners)
            PageMenuItem(stringResource(Res.string.info_link_code_of_conduct), onClick = onCodeOfConduct)
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                SocialSquare(
                    image = vectorResource(Res.drawable.twitter),
                    description = stringResource(Res.string.info_link_description_twitter),
                    modifier = Modifier.weight(1f),
                    onClick = onTwitter,
                )
                SocialSquare(
                    image = vectorResource(Res.drawable.slack),
                    description = stringResource(Res.string.info_link_description_slack),
                    modifier = Modifier.weight(1f),
                    onClick = onSlack,
                )
                SocialSquare(
                    image = vectorResource(Res.drawable.bluesky),
                    description = stringResource(Res.string.info_link_description_bluesky),
                    modifier = Modifier.weight(1f),
                    onClick = onBluesky,
                )
            }
        }
    }
}

@Composable
private fun SocialSquare(
    image: ImageVector,
    description: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Image(
        imageVector = image,
        contentDescription = description,
        modifier = modifier
            .clip(KotlinConfTheme.shapes.roundedCornerMd)
            .clickable(onClick = onClick)
            .background(KotlinConfTheme.colors.tileBackground)
            .padding(vertical = 32.dp)
            .size(64.dp),
        colorFilter = ColorFilter.tint(KotlinConfTheme.colors.primaryText),
    )
}
