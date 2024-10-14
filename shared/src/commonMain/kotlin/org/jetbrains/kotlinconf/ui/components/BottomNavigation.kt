package org.jetbrains.kotlinconf.ui.components

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.kotlinconf.AppController
import org.jetbrains.kotlinconf.ui.HDivider
import org.jetbrains.kotlinconf.ui.painter
import org.jetbrains.kotlinconf.ui.theme.blackWhite
import org.jetbrains.kotlinconf.ui.theme.grey50
import org.jetbrains.kotlinconf.ui.theme.grey5Black
import org.jetbrains.kotlinconf.ui.theme.menuSelected

class TabItem @OptIn(ExperimentalResourceApi::class) constructor(
    val name: String,
    val icon: DrawableResource,
    val selectedIcon: DrawableResource,
    val view: @Composable () -> Unit
)

@Composable
fun TabsView(controller: AppController, navigator: NavHostController, vararg items: TabItem) {
    val current by navigator.currentBackStackEntryAsState()
    val route = current?.destination?.route

    Scaffold(bottomBar = {
        Column(Modifier.fillMaxWidth()) {
            HDivider()
            BottomNavigation(
                backgroundColor = MaterialTheme.colors.grey5Black,
                contentColor = MaterialTheme.colors.blackWhite,
            ) {
                items.forEach {
                    BottomButton(navigator, tab = it, isSelected = it.name == route)
                }
            }
        }
    }) {
        NavHost(
            navController = navigator,
            startDestination = items[0].name,
            modifier = Modifier.padding(it),
            enterTransition = { EnterTransition.None },
            exitTransition = { ExitTransition.None },
            popEnterTransition = { EnterTransition.None },
            popExitTransition = { ExitTransition.None },
        ) {
            items.forEach { tab ->
                composable(tab.name) {
                    controller.routeTo(tab.name)
                    val last: (@Composable (AppController) -> Unit)? by controller.last.collectAsState()
                    last?.let { it(controller) } ?: tab.view()
                }
            }
        }
    }
}

@OptIn(ExperimentalResourceApi::class)
@Composable
internal fun RowScope.BottomButton(
    navigator: NavHostController,
    tab: TabItem,
    isSelected: Boolean
) {
    val background = if (isSelected) {
        MaterialTheme.colors.menuSelected
    } else {
        MaterialTheme.colors.grey5Black
    }

    BottomNavigationItem(
        modifier = Modifier.background(background).padding(bottom = 18.dp),
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
