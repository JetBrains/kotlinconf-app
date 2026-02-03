package org.jetbrains.kotlinconf.screens

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.kotlinconf.ConferenceService
import org.jetbrains.kotlinconf.PartnerId
import org.jetbrains.kotlinconf.ScreenWithTitle
import org.jetbrains.kotlinconf.Theme
import org.jetbrains.kotlinconf.generated.resources.Res
import org.jetbrains.kotlinconf.generated.resources.partner_detail_title
import org.jetbrains.kotlinconf.ui.components.NetworkImage
import org.jetbrains.kotlinconf.ui.components.Text
import org.jetbrains.kotlinconf.ui.theme.KotlinConfTheme
import org.koin.compose.koinInject

@Composable
fun PartnerDetailScreen(
    partnerId: PartnerId,
    onBack: () -> Unit,
    service: ConferenceService = koinInject(),
) {
    val conferenceInfo by service.conferenceInfo.collectAsState()
    val partner = remember(partnerId, conferenceInfo) {
        conferenceInfo?.partners?.flatMap { it.partners }?.firstOrNull { it.id == partnerId }
    }

    val theme by service.getTheme().collectAsState(Theme.SYSTEM)
    val isDark = when (theme) {
        Theme.SYSTEM -> isSystemInDarkTheme()
        Theme.LIGHT -> false
        Theme.DARK -> true
    }

    ScreenWithTitle(
        title = stringResource(Res.string.partner_detail_title),
        onBack = onBack,
    ) {
        if (partner != null) {
            val logoUrl = if (isDark) partner.logoUrlDark else partner.logoUrlLight
            NetworkImage(
                photoUrl = logoUrl,
                contentDescription = partner.name,
                modifier = Modifier.fillMaxWidth()
                    .height(180.dp)
                    .padding(horizontal = 32.dp, vertical = 16.dp)
                    .align(Alignment.CenterHorizontally)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = partner.name,
                style = KotlinConfTheme.typography.h1,
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = partner.description,
                color = KotlinConfTheme.colors.longText,
            )

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}
