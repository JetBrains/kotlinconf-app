package org.jetbrains.kotlinconf.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import kotlinconfapp.shared.generated.resources.Res
import kotlinconfapp.shared.generated.resources.about_conf_top_banner
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.kotlinconf.ui.painter

@OptIn(ExperimentalResourceApi::class)
@Composable
fun AboutConfTopBanner() {
    Image(
        painter = Res.drawable.about_conf_top_banner.painter(),
        contentDescription = null,
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 40.dp, bottom = 30.dp, start = 30.dp, end = 30.dp),
        contentScale = ContentScale.FillWidth
    )
}
