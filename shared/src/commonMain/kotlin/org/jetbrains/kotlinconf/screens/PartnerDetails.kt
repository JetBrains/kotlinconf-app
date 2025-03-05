package org.jetbrains.kotlinconf.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.kotlinconf.ScreenWithTitle
import org.jetbrains.kotlinconf.ui.components.StyledText

@Composable
fun PartnerDetails(
    name: String,
    logo: DrawableResource,
    description: String,
    onBack: () -> Unit,
) {
    ScreenWithTitle(
        title = name,
        onBack = onBack,
    ) {
        Image(
            painter = painterResource(logo),
            contentDescription = name,
            modifier = Modifier.height(120.dp).align(Alignment.CenterHorizontally)
        )
        StyledText(description)

        // TODO: add map when it is ready
    }
}
