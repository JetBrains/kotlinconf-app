package org.jetbrains.kotlinconf.theme

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.painter.Painter

object Vector {
    val PARTY_SECTION_BIRD: Painter
        @Composable
        get() = drawableVector("schedule_party_section_bird.xml")

    val PRIVACY_POLICY_BIRD: Painter
        @Composable
        get() = drawableVector("privacy_policy_bird.xml")

    val NOTIFICATIONS_BIRD: Painter
        @Composable
        get() = drawableVector("notifications_bird.xml")

    val ABOUT_TOP_BANNER: Painter
        @Composable
        get() = drawableVector("about_conf_top_banner.xml")

    val ABOUT_BOTTOM_BANNER: Painter
        @Composable
        get() = drawableVector("about_conf_bottom_banner.xml")

    val MENU_BANNER: Painter
        @Composable
        get() = drawableVector("menu_banner.xml")

    val SCHEDULE_BANNERS: List<Painter>
        @Composable
        get() = listOf(
            drawableVector("schedule_day_1_banner.xml"),
            drawableVector("schedule_day_2_banner.xml"),
            drawableVector("schedule_day_3_banner.xml")
        )
}
