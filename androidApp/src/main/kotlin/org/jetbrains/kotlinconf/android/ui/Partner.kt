package org.jetbrains.kotlinconf.android.ui

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.res.*
import androidx.compose.ui.text.font.*
import androidx.compose.ui.unit.*
import org.jetbrains.kotlinconf.android.*
import org.jetbrains.kotlinconf.android.theme.*
import org.jetbrains.kotlinconf.android.ui.components.*

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

            MapBoxMap(
                mapByLocation("Exhibition"),
                roomByLocation("EXHIBITION"),
                modifier = Modifier.height(300.dp)
                    .padding(bottom = 24.dp)
            )
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
