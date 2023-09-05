package org.jetbrains.kotlinconf.ui

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.platform.*
import androidx.compose.ui.unit.*
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.kotlinconf.*
import org.jetbrains.kotlinconf.org.jetbrains.kotlinconf.AppController
import org.jetbrains.kotlinconf.theme.*

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
                    MenuItem(text = "Search", icon = "search") {
                        controller.showSearch()
                    }
                    HDivider()
                    MenuItem(text = "KotlinConf`23", icon = "arrow_right") {
                        controller.showAboutTheConf()
                    }
                    HDivider()
                    MenuItem(text = "the app", icon = "arrow_right") {
                        controller.showAppInfo()
                    }
                    HDivider()
                    MenuItem(text = "EXHIBITION", icon = "arrow_right") {
                        controller.showPartners()
                    }
                    HDivider()
                    MenuItem(text = "code of conduct", icon = "arrow_right") {
                        controller.showCodeOfConduct()
                    }
                    HDivider()
                    MenuItem(text = "Privacy policy", icon = "arrow_right") {
                        controller.showPrivacyPolicy()
                    }
                    HDivider()
                    MenuItem(text = "TERMS OF USE", icon = "arrow_right") {
                        controller.showTerms()
                    }
                }
            }

            item {
                BigItem("Twitter", "#KOTLINCONF23", "twitter") {
                    uriHandler.openUri("https://twitter.com/kotlinconf")
                }
            }
            item {
                BigItem("Slack Channel", "", "slack") {
                    uriHandler.openUri("https://kotlinlang.slack.com/messages/kotlinconf/")
                }
            }
        }
    }
}

@OptIn(ExperimentalResourceApi::class)
@Composable
private fun MenuLogo() {
    Column(
        Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colors.grey5Black)
    ) {
        Image(
            painter = painterResource("menu_logo.xml"),
            contentDescription = "logo",
            modifier = Modifier.padding(16.dp)
        )
    }
}

@OptIn(ExperimentalResourceApi::class)
@Composable
private fun BigItem(
    title: String,
    subtitle: String,
    icon: String,
    onClick: () -> Unit = {}
) {
    val iconName = "$icon.xml"
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
                painter = painterResource(iconName),
                contentDescription = title,
                tint = grey50,
                modifier = Modifier
                    .padding(16.dp)
                    .size(48.dp)
            )
        }
    }
}

@OptIn(ExperimentalResourceApi::class)
@Composable
private fun MenuItem(text: String, icon: String, onClick: () -> Unit = {}) {
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
            painter = painterResource("$icon.xml"),
            contentDescription = "icon",
            tint = MaterialTheme.colors.greyGrey5,
            modifier = Modifier.padding(16.dp)
        )
    }
}
