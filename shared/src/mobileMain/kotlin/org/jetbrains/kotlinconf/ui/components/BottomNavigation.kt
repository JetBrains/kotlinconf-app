package org.jetbrains.kotlinconf.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.padding
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import moe.tlaster.precompose.navigation.NavHost
import moe.tlaster.precompose.navigation.Navigator
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.kotlinconf.ui.theme.DEFAULT_TRANSITION
import org.jetbrains.kotlinconf.ui.theme.blackWhite
import org.jetbrains.kotlinconf.ui.theme.grey50
import org.jetbrains.kotlinconf.ui.theme.grey5Grey
import org.jetbrains.kotlinconf.ui.theme.whiteBlack
import org.jetbrains.kotlinconf.ui.painter

class TabItem @OptIn(ExperimentalResourceApi::class) constructor(
    val name: String,
    val icon: DrawableResource,
    val selectedIcon: DrawableResource,
    val view: @Composable () -> Unit
)

@Composable
fun TabsView(navigator: Navigator, vararg items: TabItem) {
    val current by navigator.currentEntry.collectAsState(null)
    val route = current?.route?.route

    Scaffold(bottomBar = {
        BottomNavigation(
            backgroundColor = MaterialTheme.colors.whiteBlack,
            contentColor = MaterialTheme.colors.blackWhite,
        ) {
            items.forEach {
                BottomButton(navigator, tab = it, isSelected = it.name == route)
            }
        }
    }) {
        NavHost(
            navigator = navigator,
            initialRoute = items[0].name,
            modifier = Modifier.padding(it),
            navTransition = DEFAULT_TRANSITION
        ) {
            items.forEach { tab ->
                scene(tab.name) {
                    tab.view()
                }
            }
        }
    }
}

@OptIn(ExperimentalResourceApi::class)
@Composable
internal fun RowScope.BottomButton(
    navigator: Navigator,
    tab: TabItem,
    isSelected: Boolean
) {
    val background =
        if (isSelected) {
            MaterialTheme.colors.grey5Grey
        } else {
            MaterialTheme.colors.whiteBlack
        }
    BottomNavigationItem(
        modifier = Modifier.background(background),
        selected = isSelected,
        onClick = onClick@{
            navigator.navigate(tab.name)
        },
        icon = {
            val drawableResource = if (isSelected) tab.selectedIcon else tab.icon
            Icon(
                drawableResource.painter(),
                tab.name,
                tint = if (isSelected) MaterialTheme.colors.blackWhite else grey50
            )
        },
    )
}
