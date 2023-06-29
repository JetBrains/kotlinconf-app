package org.jetbrains.kotlinconf.android.ui

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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import org.jetbrains.kotlinconf.android.AppController
import org.jetbrains.kotlinconf.android.theme.grey5Black
import org.jetbrains.kotlinconf.android.theme.greyGrey20
import org.jetbrains.kotlinconf.android.theme.greyGrey5
import org.jetbrains.kotlinconf.android.theme.t2
import org.jetbrains.kotlinconf.android.theme.whiteGrey

@Composable
fun Partner(controller: AppController, name: String, description: String) {
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
            Logo(name)

            Column(Modifier.padding(16.dp)) {
                Text(
                    name.uppercase(), style = MaterialTheme.typography.t2.copy(
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colors.greyGrey5
                    )
                )
                Text(
                    description,
                    style = MaterialTheme.typography.t2.copy(
                        color = MaterialTheme.colors.greyGrey20
                    ),
                    modifier = Modifier.padding(top = 24.dp)
                )

                LocationRow(location = "Exhibition", Modifier.padding(top = 24.dp))
            }
        }
    }
}

@Composable
private fun Logo(name: String) {
    Box(
        modifier = Modifier
            .background(MaterialTheme.colors.grey5Black)
            .fillMaxWidth()
            .height(176.dp)
    ) {
        Image(
            painter = painterResource(id = LogoForName(name)),
            contentDescription = null,
            modifier = Modifier
                .align(Alignment.Center)
                .padding(16.dp)
        )
    }
}
