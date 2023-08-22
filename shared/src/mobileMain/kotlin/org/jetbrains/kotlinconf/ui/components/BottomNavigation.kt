package org.jetbrains.kotlinconf.ui.components

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import moe.tlaster.precompose.navigation.NavHost
import moe.tlaster.precompose.navigation.Navigator
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.kotlinconf.theme.*

class TabItem(
    val name: String,
    val icon: String,
    val selectedIcon: String,
    val view: @Composable () -> Unit
)

@Composable
fun TabsView(navigator: Navigator, vararg items: TabItem) {
    Scaffold(bottomBar = {
        BottomNavigation(
            backgroundColor = MaterialTheme.colors.whiteBlack,
            contentColor = MaterialTheme.colors.blackWhite,
        ) {
            items.forEach {
                BottomButton(navigator, tab = it, isSelected = false) // todo
            }
        }
    }) {
        NavHost(
            navigator = navigator,
            initialRoute = items[0].name,
            modifier = Modifier.padding(it)
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
            if (isSelected) return@onClick
            navigator.navigate(tab.name)
        },
        icon = {
            Icon(
                painterResource(if (isSelected) tab.selectedIcon else tab.icon),
                tab.name,
                tint = if (isSelected) MaterialTheme.colors.blackWhite else grey50
            )
        },
    )
}
