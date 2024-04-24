package org.jetbrains.kotlinconf.ui


import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.unit.dp
import kotlinconfapp.shared.generated.resources.Res
import kotlinconfapp.shared.generated.resources.X
import kotlinconfapp.shared.generated.resources.about_conference
import kotlinconfapp.shared.generated.resources.arrow_right
import kotlinconfapp.shared.generated.resources.code_of_conduct
import kotlinconfapp.shared.generated.resources.hashtag
import kotlinconfapp.shared.generated.resources.menu
import kotlinconfapp.shared.generated.resources.mobile_app
import kotlinconfapp.shared.generated.resources.partners
import kotlinconfapp.shared.generated.resources.search
import kotlinconfapp.shared.generated.resources.slack
import kotlinconfapp.shared.generated.resources.x
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.kotlinconf.AppController
import org.jetbrains.kotlinconf.ui.components.BigMenuItem
import org.jetbrains.kotlinconf.ui.components.MenuItem
import org.jetbrains.kotlinconf.ui.components.MenuLogo
import org.jetbrains.kotlinconf.ui.components.NavigationBar
import org.jetbrains.kotlinconf.ui.theme.grey20Grey80

@OptIn(ExperimentalResourceApi::class)
@Composable
fun MenuScreen(controller: AppController) {
    val uriHandler = LocalUriHandler.current
    Column(Modifier.fillMaxWidth()) {
        NavigationBar(
            title = stringResource(Res.string.menu),
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
                    MenuItem(text = stringResource(Res.string.search), icon = Res.drawable.search) {
                        controller.showSearch()
                    }
                    HDivider()
                    MenuItem(
                        text = stringResource(Res.string.about_conference),
                        icon = Res.drawable.arrow_right
                    ) {
                        controller.showAboutTheConf()
                    }
                    HDivider()
                    MenuItem(
                        text = stringResource(Res.string.mobile_app),
                        icon = Res.drawable.arrow_right
                    ) {
                        controller.showAppInfo()
                    }
                    HDivider()
                    MenuItem(
                        text = stringResource(Res.string.partners),
                        icon = Res.drawable.arrow_right
                    ) {
                        controller.showPartners()
                    }
                    HDivider()
                    MenuItem(
                        text = stringResource(Res.string.code_of_conduct),
                        icon = Res.drawable.arrow_right
                    ) {
                        controller.showCodeOfConduct()
                    }
                }
            }

            item {
                BigMenuItem(
                    stringResource(Res.string.X),
                    stringResource(Res.string.hashtag),
                    Res.drawable.x
                ) {
                    uriHandler.openUri("https://twitter.com/hashtag/KotlinConf")
                }
            }
            item {
                BigMenuItem(stringResource(Res.string.slack), "", Res.drawable.slack) {
                    uriHandler.openUri("https://kotlinlang.slack.com/messages/kotlinconf/")
                }
            }
            item(span = { GridItemSpan(2) }) {
                // last divider
            }
        }
    }
}
