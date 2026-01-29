package org.jetbrains.kotlinconf.ui.components

import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.StringResource

data class MainNavDestination<T : Any>(
    val label: StringResource,
    val icon: DrawableResource,
    val route: T,
    val iconSelected: DrawableResource = icon,
)
