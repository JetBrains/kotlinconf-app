package org.jetbrains.kotlinconf.ui


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
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.unit.dp
import kotlinconfapp.shared.generated.resources.Res
import kotlinconfapp.shared.generated.resources.arrow_right
import kotlinconfapp.shared.generated.resources.search
import kotlinconfapp.shared.generated.resources.slack
import kotlinconfapp.shared.generated.resources.x
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.kotlinconf.AppController
import org.jetbrains.kotlinconf.ui.theme.blackGrey5
import org.jetbrains.kotlinconf.ui.theme.grey20Grey80
import org.jetbrains.kotlinconf.ui.theme.grey50
import org.jetbrains.kotlinconf.ui.theme.greyWhite
import org.jetbrains.kotlinconf.ui.theme.whiteGrey
import org.jetbrains.kotlinconf.ui.components.BigMenuItem
import org.jetbrains.kotlinconf.ui.components.MenuItem
import org.jetbrains.kotlinconf.ui.components.MenuLogo
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
                    MenuItem(text = "About conference", icon = Res.drawable.arrow_right) {
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
                BigMenuItem("X", "#KOTLINCONF24", Res.drawable.x) {
                    uriHandler.openUri("https://twitter.com/kotlinconf")
                }
            }
            item {
                BigMenuItem("Slack channel", "", Res.drawable.slack) {
                    uriHandler.openUri("https://kotlinlang.slack.com/messages/kotlinconf/")
                }
            }
        }
    }
}
