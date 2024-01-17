package org.jetbrains.kotlinconf.ui


import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.unit.dp
import org.jetbrains.kotlinconf.AppController
import org.jetbrains.kotlinconf.theme.Drawables
import org.jetbrains.kotlinconf.theme.Icons
import org.jetbrains.kotlinconf.theme.grey20Grey80
import org.jetbrains.kotlinconf.theme.grey50
import org.jetbrains.kotlinconf.theme.grey5Black
import org.jetbrains.kotlinconf.theme.greyGrey5
import org.jetbrains.kotlinconf.theme.greyWhite
import org.jetbrains.kotlinconf.theme.t2
import org.jetbrains.kotlinconf.theme.whiteGrey

@Composable
fun Menu(controller: AppController) {
    val uriHandler = LocalUriHandler.current
    Column(Modifier.fillMaxWidth()) {
        NavigationBar(
            title = "MENU",
            isLeftVisible = false,
            isRightVisible = false
        )

        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            verticalArrangement = Arrangement.spacedBy(1.dp),
            horizontalArrangement = Arrangement.spacedBy(1.dp),
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colors.grey20Grey80)
        ) {
            item(span = { GridItemSpan(2) }) {
                Column {
                    MenuLogo()
                    HDivider()
                    MenuItem(text = "Search", icon = Icons.SEARCH) {
                        controller.showSearch()
                    }
                    HDivider()
                    MenuItem(text = "KotlinConf`23", icon = Icons.ARROW_RIGHT) {
                        controller.showAboutTheConf()
                    }
                    HDivider()
                    MenuItem(text = "the app", icon = Icons.ARROW_RIGHT) {
                        controller.showAppInfo()
                    }
                    HDivider()
                    MenuItem(text = "EXHIBITION", icon = Icons.ARROW_RIGHT) {
                        controller.showPartners()
                    }
                    HDivider()
                    MenuItem(text = "code of conduct", icon = Icons.ARROW_RIGHT) {
                        controller.showCodeOfConduct()
                    }
                    HDivider()
                    MenuItem(text = "Privacy policy", icon = Icons.ARROW_RIGHT) {
                        controller.showPrivacyPolicy()
                    }
                    HDivider()
                    MenuItem(text = "TERMS OF USE", icon = Icons.ARROW_RIGHT) {
                        controller.showTerms()
                    }
                }
            }

            item {
                BigItem("Twitter", "#KOTLINCONF23", Icons.TWITTER) {
                    uriHandler.openUri("https://twitter.com/kotlinconf")
                }
            }
            item {
                BigItem("Slack Channel", "", Icons.SLACK) {
                    uriHandler.openUri("https://kotlinlang.slack.com/messages/kotlinconf/")
                }
            }
        }
    }
}

@Composable
private fun MenuLogo() {
    Column(
        Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colors.grey5Black)
    ) {
        Image(
            painter = Drawables.MENU_LOGO,
            contentDescription = "logo",
            modifier = Modifier.padding(16.dp)
        )
    }
}

@Composable
private fun BigItem(
    title: String,
    subtitle: String,
    icon: Painter,
    onClick: () -> Unit = {}
) {
    Column(
        Modifier
            .background(MaterialTheme.colors.whiteGrey)
            .clickable { onClick() }
            .height(140.dp)
    ) {
        Text(
            title.uppercase(), style = MaterialTheme.typography.t2.copy(
                color = MaterialTheme.colors.greyWhite
            ),
            modifier = Modifier.padding(top = 16.dp, start = 16.dp)
        )
        Text(
            subtitle.uppercase(), style = MaterialTheme.typography.t2.copy(color = grey50),
            modifier = Modifier.padding(start = 16.dp)
        )
        Row(Modifier.fillMaxWidth()) {
            Spacer(modifier = Modifier.weight(1f))
            Icon(
                painter = icon,
                contentDescription = title,
                tint = grey50,
                modifier = Modifier
                    .padding(16.dp)
                    .size(48.dp)
            )
        }
    }
}

@Composable
private fun MenuItem(text: String, icon: Painter, onClick: () -> Unit = {}) {
    Row(
        Modifier
            .height(56.dp)
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .background(MaterialTheme.colors.whiteGrey)
    ) {
        Text(
            text.uppercase(), modifier = Modifier
                .padding(16.dp),
            style = MaterialTheme.typography.t2.copy(
                color = MaterialTheme.colors.greyWhite
            )
        )

        Spacer(modifier = Modifier.weight(1f))
        Icon(
            painter = icon,
            contentDescription = "icon",
            tint = MaterialTheme.colors.greyGrey5,
            modifier = Modifier.padding(16.dp)
        )
    }
}
