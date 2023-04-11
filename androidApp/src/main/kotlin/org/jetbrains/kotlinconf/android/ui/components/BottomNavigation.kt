package org.jetbrains.kotlinconf.android.ui.components

import androidx.annotation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.res.*
import androidx.compose.ui.tooling.preview.*
import androidx.navigation.*
import androidx.navigation.compose.*
import com.jetbrains.kotlinconf.R
import org.jetbrains.kotlinconf.android.theme.*

class TabItem(
    val name: String,
    @DrawableRes val icon: Int,
    @DrawableRes val selectedIcon: Int,
    val view: @Composable () -> Unit
)

@Composable
fun TabsView(controller: NavHostController, vararg items: TabItem) {
    Scaffold(bottomBar = {
        BottomNavigation(
            backgroundColor = MaterialTheme.colors.whiteBlack,
            contentColor = MaterialTheme.colors.blackWhite,
        ) {
            val route = controller.currentBackStackEntryAsState()
                .value?.destination?.route

            items.forEach {
                BottomButton(
                    controller = controller,
                    tab = it,
                    isSelected = route == it.name
                )
            }
        }
    }) {
        NavHost(
            navController = controller,
            startDestination = items[0].name,
            modifier = Modifier.padding(it)
        ) {
            items.forEach { tab ->
                composable(tab.name) {
                    tab.view()
                }
            }
        }
    }
}

@Composable
internal fun RowScope.BottomButton(
    controller: NavController,
    tab: TabItem,
    isSelected: Boolean
) {
    val background =
        if (isSelected) MaterialTheme.colors.grey5Grey else MaterialTheme.colors.whiteBlack
    BottomNavigationItem(
        modifier = Modifier.background(background),
        selected = isSelected,
        onClick = onClick@{
            if (isSelected) return@onClick
            controller.backQueue.clear()
            controller.navigate(tab.name)
        },
        icon = {
            Icon(
                painterResource(id = if (isSelected) tab.selectedIcon else tab.icon),
                tab.name,
                tint = if (isSelected) MaterialTheme.colors.blackWhite else grey50
            )
        },
    )
}

@Preview
@Composable
internal fun TabsPreview() {
    KotlinConfTheme {
        TabsView(
            rememberNavController(),
            TabItem("agenda", R.drawable.time, R.drawable.time_active) {
                Text("Agenda")
            },
            TabItem("speakers", R.drawable.speakers, R.drawable.speakers_active) {
                Text("Speakers")
            },
            TabItem("Bookmarks", R.drawable.mytalks, R.drawable.mytalks_active) {
                Text("Bookmarks")
            },
            TabItem("Map", R.drawable.location, R.drawable.location_active) {
                Text("Map")
            }
        )
    }
}