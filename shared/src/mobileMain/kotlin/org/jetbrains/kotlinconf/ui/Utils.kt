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

internal fun LogoForName(name: String): String = when (name) {
    "android" -> "android_google_big"
    "google" -> "android_google_big"
    "xebia" -> "xebia_big"
    "kodein" -> "kodein_koders_big"
    "lunatech" -> "lunatech_big"
    "gradle" -> "gradle_big"
    "source" -> "source_technology_big"
    "aws" -> "aws_big"
    "sentry" -> "sentry_big"
    "adyen" -> "adyen_big"
    "jetbrains" -> "jetbrains_big"
    "grote zaal" -> "grote_zaal"
    "beursfoyer" -> "beursfoyer"
    "store" -> "merchandise_store"
    "graanbeurszaal" -> "graanbeurszaal"
    "effectenbeurszaal" -> "effectenbeurszaal"
    "loundge" -> "lounge_2"
    "administratiezaal" -> "administratiezaal"
    "veilingzaal" -> "veilingzaal"
    "berlage zaal" -> "berlage_zaal"
    "mendes da costa" -> "mendes_da_costa"
    "verwey kamer" -> "verwey_kamer"
    else -> "time"
} + ".xml"