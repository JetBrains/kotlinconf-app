package org.jetbrains.kotlinconf.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import kotlinconfapp.shared.generated.resources.hej_its_kotlinconf
import kotlinconfapp.shared.generated.resources.info_link_about_app
import kotlinconfapp.shared.generated.resources.info_link_about_conf
import kotlinconfapp.shared.generated.resources.info_link_code_of_conduct
import kotlinconfapp.shared.generated.resources.info_link_partners
import kotlinconfapp.shared.generated.resources.info_link_slack
import kotlinconfapp.shared.generated.resources.info_link_twitter
import kotlinconfapp.shared.generated.resources.info_title
import kotlinconfapp.shared.generated.resources.slack
import kotlinconfapp.shared.generated.resources.twitter
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource
import org.jetbrains.kotlinconf.ui.components.Divider
import org.jetbrains.kotlinconf.ui.components.MainHeaderTitleBar
import org.jetbrains.kotlinconf.ui.components.PageMenuItem
import org.jetbrains.kotlinconf.ui.components.StyledText
import org.jetbrains.kotlinconf.ui.theme.KotlinConfTheme


@Composable
fun InfoScreen(
    onAboutConf: () -> Unit,
    onAboutApp: () -> Unit,
    onOurPartners: () -> Unit,
    onCodeOfConduct: () -> Unit,
    onTwitter: () -> Unit,
    onSlack: () -> Unit,
) {
    Column(Modifier.fillMaxSize()) {
        MainHeaderTitleBar(stringResource(Res.string.info_title))
        Divider(1.dp, KotlinConfTheme.colors.strokePale)

        Column(
            Modifier
                .verticalScroll(rememberScrollState())
                .fillMaxSize()
                .padding(horizontal = 12.dp)
                .padding(bottom = 12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Image(
                imageVector = vectorResource(Res.drawable.hej_its_kotlinconf),
                contentDescription = null,
                modifier = Modifier
                    .padding(24.dp)
                    .fillMaxWidth()
                    .height(124.dp)
            )
            PageMenuItem(stringResource(Res.string.info_link_about_conf), onClick = onAboutConf)
            PageMenuItem(stringResource(Res.string.info_link_about_app), onClick = onAboutApp)
            PageMenuItem(stringResource(Res.string.info_link_partners), onClick = onOurPartners)
            PageMenuItem(stringResource(Res.string.info_link_code_of_conduct), onClick = onCodeOfConduct)
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                SocialSquare(
                    image = vectorResource(Res.drawable.twitter),
                    text = stringResource(Res.string.info_link_twitter),
                    modifier = Modifier.weight(1f),
                    onClick = onTwitter,
                )
                SocialSquare(
                    image = vectorResource(Res.drawable.slack),
                    text = stringResource(Res.string.info_link_slack),
                    modifier = Modifier.weight(1f),
                    onClick = onSlack,
                )
            }
        }
    }
}

@Composable
private fun SocialSquare(
    image: ImageVector,
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .clickable { onClick() }
            .background(KotlinConfTheme.colors.tileBackground)
            .padding(vertical = 32.dp)
    ) {
        Image(
            imageVector = image,
            contentDescription = null,
            modifier = Modifier
                .padding(vertical = 8.dp)
                .size(48.dp),
            colorFilter = ColorFilter.tint(
                KotlinConfTheme.colors.primaryText,
            )
        )
        Spacer(Modifier.height(8.dp))
        StyledText(text)
    }
}
