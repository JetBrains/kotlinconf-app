package org.jetbrains.kotlinconf.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import kotlinconfapp.shared.generated.resources.Res
import kotlinconfapp.shared.generated.resources.arrow_left_24
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.kotlinconf.LocalNavController
import org.jetbrains.kotlinconf.PARTNER_DESCRIPTIONS
import org.jetbrains.kotlinconf.PartnerDetailsScreen
import org.jetbrains.kotlinconf.ui.components.StyledText

@Composable
fun Partners() {
    val navController = LocalNavController.current
    Column {
        Image(painterResource(Res.drawable.arrow_left_24), "back", modifier = Modifier.clickable { navController.popBackStack() })
        StyledText("Partners")
        for (partner in PARTNER_DESCRIPTIONS) {
            StyledText(partner.key, modifier = Modifier.clickable { navController.navigate(PartnerDetailsScreen(partner.value)) })
        }
    }
}