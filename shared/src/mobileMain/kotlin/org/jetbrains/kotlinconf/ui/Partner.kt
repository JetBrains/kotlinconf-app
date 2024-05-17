package org.jetbrains.kotlinconf.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.kotlinconf.AppController
import org.jetbrains.kotlinconf.ui.theme.grey5Black
import org.jetbrains.kotlinconf.ui.theme.greyGrey20
import org.jetbrains.kotlinconf.ui.theme.greyGrey5
import org.jetbrains.kotlinconf.ui.theme.whiteGrey
import org.jetbrains.kotlinconf.ui.components.NavigationBar
import org.jetbrains.kotlinconf.ui.components.Room
import org.jetbrains.kotlinconf.ui.components.RoomMap
import org.jetbrains.kotlinconf.ui.theme.greyWhite

@OptIn(ExperimentalResourceApi::class)
@Composable
fun Partner(controller: AppController, partner: Partner) {
    Column {
        NavigationBar(
            title = "",
            onLeftClick = { controller.back() },
            onRightClick = {},
            isRightVisible = false,
            isLeftVisible = true
        )
        Column(
            Modifier
                .background(MaterialTheme.colors.whiteGrey)
                .verticalScroll(rememberScrollState())
                .fillMaxWidth()
        ) {
            AboutConfTopBanner(partner)
            HDivider()

            Column(Modifier.padding(16.dp).fillMaxWidth()) {
                Text(
                    stringResource(partner.title), style = MaterialTheme.typography.h2.copy(
                        color = MaterialTheme.colors.greyWhite
                    )
                )
                Text(
                    stringResource(partner.description),
                    style = MaterialTheme.typography.body2.copy(
                        color = MaterialTheme.colors.greyGrey20
                    ),
                    modifier = Modifier.padding(top = 24.dp)
                )

                LocationRow(location = "Exhibition", Modifier.padding(top = 24.dp, bottom = 8.dp))
                RoomMap(Room.EXHIBITION)
            }
        }
    }
}

@OptIn(ExperimentalResourceApi::class)
@Composable
private fun AboutConfTopBanner(partner: Partner) {
    Box(
        modifier = Modifier
            .background(MaterialTheme.colors.grey5Black)
            .fillMaxWidth()
            .height(176.dp)
    ) {
        Image(
            painter = partner.logo.painter(),
            contentDescription = null,
            modifier = Modifier
                .align(Alignment.Center)
                .padding(16.dp)
        )
    }
}
