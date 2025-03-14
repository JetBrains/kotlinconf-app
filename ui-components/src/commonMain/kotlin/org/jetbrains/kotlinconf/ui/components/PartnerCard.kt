package org.jetbrains.kotlinconf.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import kotlinconfapp.ui_components.generated.resources.Res
import kotlinconfapp.ui_components.generated.resources.kodee_large_positive_light
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.jetbrains.kotlinconf.ui.theme.KotlinConfTheme
import org.jetbrains.kotlinconf.ui.theme.PreviewHelper

@Composable
fun PartnerCard(
    name: String,
    logo: DrawableResource,
    onClick: () -> Unit,
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(16.dp))
            .background(KotlinConfTheme.colors.tileBackground)
            .clickable(onClick = onClick)
            .fillMaxWidth()
            .height(180.dp)
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(logo),
            contentDescription = name,
        )
    }
}

@Preview
@Composable
internal fun PartnerCardPreview() {
    PreviewHelper {
        PartnerCard("Kodee", Res.drawable.kodee_large_positive_light, {})
    }
}
