package org.jetbrains.kotlinconf.ui.components

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.scaleIn
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.graphics.painter.Painter
import moe.tlaster.precompose.navigation.NavHost
import moe.tlaster.precompose.navigation.Navigator
import moe.tlaster.precompose.navigation.transition.NavTransition
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.kotlinconf.theme.*

class TabItem(
    val name: String,
    val icon: Painter,
    val selectedIcon: Painter,
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
                BottomButton(navigator, tab = it, isSelected = false)
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
                if (isSelected) tab.selectedIcon else tab.icon,
                tab.name,
                tint = if (isSelected) MaterialTheme.colors.blackWhite else grey50
            )
        },
    )
}
