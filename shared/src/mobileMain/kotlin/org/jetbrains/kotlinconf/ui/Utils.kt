package org.jetbrains.kotlinconf.android.ui

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.*
import androidx.compose.ui.unit.*
import com.jetbrains.kotlinconf.R
import org.jetbrains.kotlinconf.android.theme.*

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

internal fun LogoForName(name: String): Int = when (name) {
    "android" -> R.drawable.android_google_big
    "google" -> R.drawable.android_google_big
    "xebia" -> R.drawable.xebia_big
    "kodein" -> R.drawable.kodein_koders_big
    "lunatech" -> R.drawable.lunatech_big
    "gradle" -> R.drawable.gradle_big
    "source" -> R.drawable.source_technology_big
    "aws" -> R.drawable.aws_big
    "sentry" -> R.drawable.sentry_big
    "adyen" -> R.drawable.adyen_big
    "jetbrains" -> R.drawable.jetbrains_big
    "grote zaal" -> R.drawable.grote_zaal
    "beursfoyer" -> R.drawable.beursfoyer
    "store" -> R.drawable.merchandise_store
    "graanbeurszaal" -> R.drawable.graanbeurszaal
    "effectenbeurszaal" -> R.drawable.effectenbeurszaal
    "loundge" -> R.drawable.lounge_2
    "administratiezaal" -> R.drawable.administratiezaal
    "veilingzaal" -> R.drawable.veilingzaal
    "berlage zaal" -> R.drawable.berlage_zaal
    "mendes da costa" -> R.drawable.mendes_da_costa
    "verwey kamer" -> R.drawable.verwey_kamer
    else -> R.drawable.time
}