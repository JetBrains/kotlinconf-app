package org.jetbrains.kotlinconf.theme

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.painter.Painter

object Drawables {
    val PARTY: Painter
        @Composable
        get() = drawableResource("party.xml")

    val PRIVACY: Painter
        @Composable
        get() = drawableResource("privacy.xml")

    val NOTIFICATIONS: Painter
        @Composable
        get() = drawableResource("notifications.xml")

    val ABOUT: Painter
        @Composable
        get() = drawableResource("about.xml")

    val MENU_LOGO: Painter
        @Composable
        get() = drawableResource("menu_logo.xml")

    val CLOSING_PANEL: Painter
        @Composable
        get() = drawableResource("closing.xml")
}
