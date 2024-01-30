package org.jetbrains.kotlinconf.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Divider
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import kotlinconfapp.shared.generated.resources.Res
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.kotlinconf.theme.divider
import org.jetbrains.kotlinconf.theme.grey50
import org.jetbrains.kotlinconf.theme.greyWhite
import org.jetbrains.kotlinconf.theme.t2

@Composable
fun HDivider(modifier: Modifier = Modifier) {
    Divider(modifier.background(MaterialTheme.colors.divider))
}

@Composable
fun VDivider(modifier: Modifier = Modifier) {
    Divider(
        modifier
            .background(MaterialTheme.colors.divider)
            .width(1.dp)
    )
}

@Composable
fun LocationRow(location: String, modifier: Modifier = Modifier) {
    Row(modifier) {
        Text(
            location.uppercase(),
            style = MaterialTheme.typography.t2.copy(
                color = grey50
            ),
        )
    }
}

@Composable
fun ColumnScope.SheetBar() {
    Row(
        Modifier
            .width(96.dp)
            .height(4.dp)
            .background(MaterialTheme.colors.greyWhite)
            .clip(RoundedCornerShape(4.dp))
            .align(Alignment.CenterHorizontally)
    ) {}
}

@OptIn(ExperimentalResourceApi::class)
@Composable
internal fun LogoForName(name: String): DrawableResource = when (name) {
    "android" -> Res.drawable.android_google_big
    "google" -> Res.drawable.android_google_big
    "xebia" -> Res.drawable.xebia_big
    "kodein" -> Res.drawable.kodein_koders_big
    "lunatech" -> Res.drawable.lunatech_big
    "gradle" -> Res.drawable.gradle_big
    "source" -> Res.drawable.source_technology_big
    "aws" -> Res.drawable.aws_big
    "sentry" -> Res.drawable.sentry_big
    "adyen" -> Res.drawable.adyen_big
    "jetbrains" -> Res.drawable.jetbrains_big
    else -> Res.drawable.jetbrains_big
}