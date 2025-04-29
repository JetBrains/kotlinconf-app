package org.jetbrains.kotlinconf.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import kotlinconfapp.shared.generated.resources.Res
import kotlinconfapp.shared.generated.resources.bluesky
import kotlinconfapp.shared.generated.resources.info_link_about_app
import kotlinconfapp.shared.generated.resources.info_link_about_conf
import kotlinconfapp.shared.generated.resources.info_link_code_of_conduct
import kotlinconfapp.shared.generated.resources.info_link_description_bluesky
import kotlinconfapp.shared.generated.resources.info_link_description_slack
import kotlinconfapp.shared.generated.resources.info_link_description_twitter
import kotlinconfapp.shared.generated.resources.info_link_news_feed
import kotlinconfapp.shared.generated.resources.info_link_partners
import kotlinconfapp.shared.generated.resources.info_title
import kotlinconfapp.shared.generated.resources.kotlinconf_by_jetbrains
import kotlinconfapp.shared.generated.resources.slack
import kotlinconfapp.shared.generated.resources.twitter
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource
import org.jetbrains.kotlinconf.ui.components.Divider
import org.jetbrains.kotlinconf.ui.components.MainHeaderTitleBar
import org.jetbrains.kotlinconf.ui.components.PageMenuItem
import org.jetbrains.kotlinconf.ui.theme.KotlinConfTheme


@Composable
fun InfoScreen(
    onAboutConf: () -> Unit,
    onAboutApp: () -> Unit,
    onNewsFeed: () -> Unit,
    onOurPartners: () -> Unit,
    onCodeOfConduct: () -> Unit,
    onTwitter: () -> Unit,
    onSlack: () -> Unit,
    onBluesky: () -> Unit,
) {
    Column(Modifier.fillMaxSize().background(color = KotlinConfTheme.colors.mainBackground)) {
        MainHeaderTitleBar(stringResource(Res.string.info_title))
        Divider(1.dp, KotlinConfTheme.colors.strokePale)

        Column(
            Modifier
                .verticalScroll(rememberScrollState())
                .fillMaxSize()
                .padding(horizontal = 12.dp)
                .padding(bottom = 12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Image(
                imageVector = vectorResource(Res.drawable.kotlinconf_by_jetbrains),
                contentDescription = null,
                modifier = Modifier
                    .padding(24.dp)
                    .widthIn(max = 360.dp)
            )
            PageMenuItem(stringResource(Res.string.info_link_about_conf), onClick = onAboutConf)
            PageMenuItem(stringResource(Res.string.info_link_about_app), onClick = onAboutApp)
            PageMenuItem(stringResource(Res.string.info_link_news_feed), onClick = onNewsFeed)
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
            .clip(RoundedCornerShape(8.dp))
            .clickable(onClick = onClick)
            .background(KotlinConfTheme.colors.tileBackground)
            .padding(vertical = 32.dp)
            .size(64.dp),
        colorFilter = ColorFilter.tint(KotlinConfTheme.colors.primaryText),
    )
}
