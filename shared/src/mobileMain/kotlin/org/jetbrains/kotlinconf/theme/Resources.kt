package org.jetbrains.kotlinconf.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.painter.Painter
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.painterResource

object Drawables {

    val SMILE_HAPPY_ACTIVE: Painter
        @Composable
        get() = drawableResource("smilehappy_active.xml")

    val SMILE_SAD_ACTIVE: Painter
        @Composable
        get() = drawableResource("smilesad_active.xml")

    val SMILE_NEUTRAL_ACTIVE: Painter
        @Composable
        get() = drawableResource("smileneutral_active.xml")

    val SMILE_HAPPY: Painter
        @Composable
        get() = drawableResource("smilehappy.xml")

    val SMILE_SAD: Painter
        @Composable
        get() = drawableResource("smilesad.xml")

    val SMILE_NEUTRAL: Painter
        @Composable
        get() = drawableResource("smileneutral.xml")


    val TWITTER: Painter
        @Composable
        get() = drawableResource("twitter.xml")

    val SLACK: Painter
        @Composable
        get() = drawableResource("slack.xml")

    val ARROW_RIGHT_ICON: Painter
        @Composable
        get() = drawableResource("arrow_right.xml")

    val SEARCH_ICON: Painter
        @Composable
        get() = drawableResource("search.xml")

    val LUNCH_ICON: Painter
        @Composable
        get() = drawableResource("lunch.xml")

    val LUNCH_ACTIVE_ICON: Painter
        @Composable
        get() = drawableResource("lunch_active.xml")

    val PARTY: Painter
        @Composable
        get() = drawableResource("party.xml")

    val BACK_ICON: Painter
        @Composable
        get() = drawableResource("back.xml")

    val CUP_ICON: Painter
        @Composable
        get() = drawableResource("cup.xml")

    val CUP_ACTIVE_ICON: Painter
        @Composable
        get() = drawableResource("cup_active.xml")

    val PRIVACY: Painter
        @Composable
        get() = drawableResource("privacy.xml")

    val NOTIFICATIONS: Painter
        @Composable
        get() = drawableResource("notifications.xml")

    val BOOKMARK_ACTIVE_ICON: Painter
        @Composable
        get() = drawableResource("bookmark_active.xml")

    val BOOKMARK_ICON: Painter
        @Composable
        get() = drawableResource("bookmark.xml")

    val ABOUT: Painter
        @Composable
        get() = drawableResource("about.xml")

    val AWS_LAB_ICON: Painter
        @Composable
        get() = drawableResource("aws_lab.xml")

    val LIGHT_ICON: Painter
        @Composable
        get() = drawableResource("light.xml")

    val CLOSE_ICON: Painter
        @Composable
        get() = drawableResource("close.xml")

    val MENU_LOGO: Painter
        @Composable
        get() = drawableResource("menu_logo.xml")

    val MY_TALKS_ACTIVE_ICON: Painter
        @Composable
        get() = drawableResource("mytalks_active.xml")

    val SPEAKERS_ACTIVE_ICON: Painter
        @Composable
        get() = drawableResource("speakers_active.xml")

    val TIME_ACTIVE_ICON: Painter
        @Composable
        get() = drawableResource("time_active.xml")

    val MENU_ACTIVE_ICON: Painter
        @Composable
        get() = drawableResource("menu_active.xml")

    val MY_TALKS_ICON: Painter
        @Composable
        get() = drawableResource("mytalks.xml")

    val SPEAKERS_ICON: Painter
        @Composable
        get() = drawableResource("speakers.xml")

    val TIME_ICON: Painter
        @Composable
        get() = drawableResource("time.xml")

    val MENU_ICON: Painter
        @Composable
        get() = drawableResource("menu.xml")

    val CLOSING_PANEL: Painter
        @Composable
        get() = drawableResource("closing.xml")
}


@OptIn(ExperimentalResourceApi::class)
@Composable
private fun drawableResource(name: String): Painter {
    val folder = if (isSystemInDarkTheme()) {
        "drawable-night"
    } else {
        "drawable"
    }

    return painterResource("$folder/$name")
}
