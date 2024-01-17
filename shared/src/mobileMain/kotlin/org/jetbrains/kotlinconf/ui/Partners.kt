package org.jetbrains.kotlinconf.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyGridScope
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.unit.dp
import org.jetbrains.kotlinconf.theme.drawableResource
import org.jetbrains.kotlinconf.theme.grey20Grey80
import org.jetbrains.kotlinconf.theme.grey5Black
import org.jetbrains.kotlinconf.theme.greyGrey5
import org.jetbrains.kotlinconf.theme.whiteGrey

private val PARTNER_LOGOS: Map<String, String> = mapOf(
    "android" to "andorid",
    "google" to "google",
    "xebia" to "xebia",
    "adyen" to "adyen",
    "kodein" to "kodein_koders",
    "lunatech" to "lunatech",
    "gradle" to "gradle",
    "source" to "source_technology",
    "sentry" to "sentry",
    "aws" to "aws",
    "jetbrains" to "jetbrains"
)

@Composable
fun partnerLogo(name: String): Painter {
    val resourceName = PARTNER_LOGOS[name] ?: "jetbrains"
    return drawableResource("$resourceName.xml")
}

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
fun TextTitle(value: String) {
    Row(
        Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colors.grey5Black)
    ) {
        Text(
            value.uppercase(),
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
    val logo = partnerLogo(name)

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
                painter = logo,
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
