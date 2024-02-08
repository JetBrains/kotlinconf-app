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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.unit.dp
import kotlinconfapp.shared.generated.resources.Res
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.kotlinconf.AppController
import org.jetbrains.kotlinconf.theme.blackGrey5
import org.jetbrains.kotlinconf.theme.grey20Grey80
import org.jetbrains.kotlinconf.theme.grey50
import org.jetbrains.kotlinconf.theme.grey5Black
import org.jetbrains.kotlinconf.theme.greyWhite
import org.jetbrains.kotlinconf.theme.whiteGrey
import org.jetbrains.kotlinconf.ui.components.NavigationBar

@OptIn(ExperimentalResourceApi::class)
@Composable
fun MenuScreen(controller: AppController) {
    val uriHandler = LocalUriHandler.current
    Column(Modifier.fillMaxWidth()) {
        NavigationBar(
            title = "Menu",
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
                    MenuItem(text = "Search", icon = Res.drawable.search) {
                        controller.showSearch()
                    }
                    HDivider()
                    MenuItem(text = "About the conference", icon = Res.drawable.arrow_right) {
                        controller.showAboutTheConf()
                    }
                    HDivider()
                    MenuItem(text = "‘24 mobile app", icon = Res.drawable.arrow_right) {
                        controller.showAppInfo()
                    }
                    HDivider()
                    MenuItem(text = "Our partners", icon = Res.drawable.arrow_right) {
                        controller.showPartners()
                    }
                    HDivider()
                    MenuItem(text = "Code of conduct", icon = Res.drawable.arrow_right) {
                        controller.showCodeOfConduct()
                    }
                    HDivider()
                    MenuItem(text = "Privacy policy", icon = Res.drawable.arrow_right) {
                        controller.showPrivacyPolicy()
                    }
                }
            }

            item {
                BigItem("X", "#KOTLINCONF24", Res.drawable.x) {
                    uriHandler.openUri("https://twitter.com/kotlinconf")
                }
            }
            item {
                BigItem("Slack channel", "", Res.drawable.slack) {
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
            .background(MaterialTheme.colors.grey5Black),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            modifier = Modifier.padding(top = 48.dp, bottom = 48.dp),
            painter = Res.drawable.menu_banner.painter(),
            contentDescription = "logo",
        )
    }
}

@OptIn(ExperimentalResourceApi::class)
@Composable
private fun BigItem(
    title: String,
    subtitle: String,
    icon: DrawableResource,
    onClick: () -> Unit = {}
) {
    Column(
        Modifier
            .background(MaterialTheme.colors.whiteGrey)
            .clickable { onClick() }
            .height(160.dp)
    ) {
        Text(
            title, style = MaterialTheme.typography.body2.copy(
                color = MaterialTheme.colors.greyWhite
            ),
            modifier = Modifier.padding(top = 16.dp, start = 16.dp)
        )
        Text(
            subtitle.uppercase(), style = MaterialTheme.typography.body2.copy(color = grey50),
            modifier = Modifier.padding(start = 16.dp)
        )
        Spacer(modifier = Modifier.weight(1f))
        Row(Modifier.fillMaxWidth().padding(end = 16.dp, bottom = 16.dp)) {
            Spacer(modifier = Modifier.weight(1f))
            Icon(
                painter = icon.painter(),
                contentDescription = title,
                tint = MaterialTheme.colors.blackGrey5,
                modifier = Modifier
                    .size(48.dp)
            )
        }
    }
}

@OptIn(ExperimentalResourceApi::class)
@Composable
private fun MenuItem(text: String, icon: DrawableResource, onClick: () -> Unit = {}) {
    Row(
        Modifier
            .height(56.dp)
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .background(MaterialTheme.colors.whiteGrey)
    ) {
        Text(
            text, modifier = Modifier
                .padding(16.dp),
            style = MaterialTheme.typography.body1.copy(
                color = MaterialTheme.colors.greyWhite
            )
        )

        Spacer(modifier = Modifier.weight(1f))
        Icon(
            painter = icon.painter(),
            contentDescription = "icon",
            tint = MaterialTheme.colors.blackGrey5,
            modifier = Modifier.padding(16.dp)
        )
    }
}
