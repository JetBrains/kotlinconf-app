package org.jetbrains.kotlinconf.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.kotlinconf.PARTNERS
import org.jetbrains.kotlinconf.PartnerId
import org.jetbrains.kotlinconf.ScreenWithTitle
import org.jetbrains.kotlinconf.ui.components.StyledText
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
        title = partner.name,
        onBack = onBack,
    ) {
        Image(
            painter = painterResource(partner.logo(KotlinConfTheme.colors.isDark)),
            contentDescription = partner.name,
            modifier = Modifier.height(120.dp).align(Alignment.CenterHorizontally)
        )
        StyledText(partner.description)

        // TODO: add map when it is ready
    }
}
