package org.jetbrains.kotlinconf.android.ui

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.res.*
import androidx.compose.ui.tooling.preview.*
import androidx.compose.ui.unit.*
import com.jetbrains.kotlinconf.R
import org.jetbrains.kotlinconf.android.theme.*

private val PARTNER_LOGOS = mapOf(
    "android" to R.drawable.andorid,
    "google" to R.drawable.google,
    "xebia" to R.drawable.xebia,
    "adyen" to R.drawable.adyen,
    "kodein" to R.drawable.kodein_koders,
    "lunatech" to R.drawable.lunatech,
    "gradle" to R.drawable.gradle,
    "source" to R.drawable.source_technology,
    "sentry" to R.drawable.sentry,
    "aws" to R.drawable.aws,
    "jetbrains" to R.drawable.jetbrains
)

@Composable
fun Partners(showPartner: (String) -> Unit, back: () -> Unit) {
    fun LazyGridScope.Block(name: String) {
        item { PartnerCard(name) { showPartner(name) } }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colors.grey5Black)
    ) {
        NavigationBar(
            title = "EXHIBITION",
            isLeftVisible = true,
            onLeftClick = { back() },
            isRightVisible = false
        )
        PartnerGrid {
            Block("jetbrains")
            Block("google")
            Block("xebia")
            Block("adyen")
            Block("kodein")
            Block("lunatech")
            Block("sentry")
            Block("gradle")
            Block("source")
            Block("aws")
        }
    }
}

@Composable
private fun TextTitle(name: String) {
    Row(
        Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colors.grey5Black)
    ) {
        Text(
            name.uppercase(),
            style = MaterialTheme.typography.h2.copy(
                color = MaterialTheme.colors.greyGrey5
            ),
            modifier = Modifier.padding(16.dp)
        )
    }
}

@Composable
private fun PartnerCard(
    name: String,
    onClick: () -> Unit = {}
) {
    val logo = PARTNER_LOGOS[name] ?: R.drawable.time
    Box(
        Modifier
            .background(MaterialTheme.colors.whiteGrey)
            .clickable { onClick() }
            .height(188.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .width(135.dp)
                .height(56.dp)
        ) {
            Image(
                painter = painterResource(id = logo),
                contentDescription = "image"
            )
            Spacer(modifier = Modifier.weight(1f))
        }
    }
}

@Composable
private fun PartnerCard() {
    Box(
        Modifier
            .background(MaterialTheme.colors.whiteGrey)
            .height(188.dp)
    )
}

@Composable
private fun PartnerGrid(block: LazyGridScope.() -> Unit) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        verticalArrangement = Arrangement.spacedBy(1.dp),
        horizontalArrangement = Arrangement.spacedBy(1.dp),
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colors.grey20Grey80)
    ) {
        block()
    }
}

@Composable
@Preview(showSystemUi = true)
private fun PartnersPreview() {
    KotlinConfTheme {
        Partners(showPartner = {}) {
        }
    }
}
