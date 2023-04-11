package org.jetbrains.kotlinconf.android.ui

import androidx.annotation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.platform.*
import androidx.compose.ui.res.*
import androidx.compose.ui.tooling.preview.*
import androidx.compose.ui.unit.*
import com.jetbrains.kotlinconf.R
import org.jetbrains.kotlinconf.android.*
import org.jetbrains.kotlinconf.android.theme.*

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
                    MenuItem(text = "Search", icon = R.drawable.search) {
                        controller.showSearch()
                    }
                    HDivider()
                    MenuItem(text = "KotlinConf`23", icon = R.drawable.arrow_right) {
                        controller.showAboutTheConf()
                    }
                    HDivider()
                    MenuItem(text = "the app", icon = R.drawable.arrow_right) {
                        controller.showAppInfo()
                    }
                    HDivider()
                    MenuItem(text = "EXHIBITION", icon = R.drawable.arrow_right) {
                        controller.showPartners()
                    }
                    HDivider()
                    MenuItem(text = "code of conduct", icon = R.drawable.arrow_right) {
                        controller.showCodeOfConduct()
                    }
                    HDivider()
                    MenuItem(text = "Privacy policy", icon = R.drawable.arrow_right) {
                        controller.showPrivacyPolicy()
                    }
                    HDivider()
                    MenuItem(text = "TERMS OF USE", icon = R.drawable.arrow_right) {
                        controller.showTerms()
                    }
                }
            }

            item {
                BigItem("Twitter", "#KOTLINCONF23", R.drawable.twitter) {
                    uriHandler.openUri("https://twitter.com/kotlinconf")
                }
            }
            item {
                BigItem("Slack Channel", "", R.drawable.slack) {
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
            painter = painterResource(id = R.drawable.menu_logo),
            contentDescription = "logo",
            modifier = Modifier.padding(16.dp)
        )
    }
}

@Composable
@Preview(showSystemUi = true)
fun MenuLogoPreview() {
    KotlinConfTheme {
        MenuLogo()
    }
}

@Composable
private fun BigItem(
    title: String,
    subtitle: String,
    @DrawableRes icon: Int,
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
                painter = painterResource(id = icon),
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
private fun MenuItem(text: String, @DrawableRes icon: Int, onClick: () -> Unit = {}) {
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
            painter = painterResource(id = icon),
            contentDescription = "icon",
            tint = MaterialTheme.colors.greyGrey5,
            modifier = Modifier.padding(16.dp)
        )
    }
}
