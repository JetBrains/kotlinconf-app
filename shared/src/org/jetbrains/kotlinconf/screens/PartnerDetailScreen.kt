package org.jetbrains.kotlinconf.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinconfapp.shared.generated.resources.Res
import kotlinconfapp.shared.generated.resources.partner_detail_title
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.kotlinconf.PARTNERS
import org.jetbrains.kotlinconf.PartnerId
import org.jetbrains.kotlinconf.ScreenWithTitle
import org.jetbrains.kotlinconf.ui.components.Text
import org.jetbrains.kotlinconf.ui.theme.KotlinConfTheme

@Composable
fun PartnerDetailScreen(
    partnerId: PartnerId,
    onBack: () -> Unit,
) {
    val partner = remember(partnerId) {
        PARTNERS.values.flatten().first { it.id == partnerId }
    }

    ScreenWithTitle(
        title = stringResource(Res.string.partner_detail_title),
        onBack = onBack,
    ) {
        Image(
            painter = painterResource(partner.icon),
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
