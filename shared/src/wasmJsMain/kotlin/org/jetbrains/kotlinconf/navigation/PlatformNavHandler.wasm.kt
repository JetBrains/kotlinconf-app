package org.jetbrains.kotlinconf.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.navigation.ExperimentalBrowserHistoryApi
import androidx.navigation.NavHostController
import androidx.navigation.bindToBrowserNavigation

@OptIn(ExperimentalBrowserHistoryApi::class)
@Composable
internal actual fun PlatformNavHandler(navController: NavHostController) {
    LaunchedEffect(Unit) {
        navController.bindToBrowserNavigation()
    }
}
