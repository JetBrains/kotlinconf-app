package org.jetbrains.kotlinconf.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController

/**
 * Used for web target to synchronize the URL and browser history
 */
@Composable
internal expect fun PlatformNavHandler(navController: NavHostController)
