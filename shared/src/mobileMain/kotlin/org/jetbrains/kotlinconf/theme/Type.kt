package org.jetbrains.kotlinconf.theme

import androidx.compose.material.Typography
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import org.jetbrains.kotlinconf.utils.font

val Typography: Typography
    @Composable
    get() {
        return Typography(
            h2 = TextStyle(
                fontFamily = JetBrainsMono,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 32.sp,
                lineHeight = 36.sp
            ),
            h4 = TextStyle(
                fontFamily = JetBrainsMono,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 16.sp,
                lineHeight = 24.sp
            ),
            body2 = TextStyle(
                fontFamily = JetBrainsMono,
                fontWeight = FontWeight.Normal,
                fontSize = 16.sp,
                lineHeight = 24.sp
            )
        )
    }


val Typography.t2: TextStyle
    @Composable
    get() = TextStyle(
        fontFamily = JetBrainsMono,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp
    )

val JetBrainsMono: FontFamily
    @Composable
    get() {
        return FontFamily(
            font(
                name = "JetBrains Mono",
                res = "jetbrainsmono_regular",
                weight = FontWeight.Normal,
                style = FontStyle.Normal
            ),
            font(
                "JetBrains Mono",
                "jetbrainsmono_extrabold",
                FontWeight.ExtraBold,
                FontStyle.Normal
            ),
        )
    }

val Typography.bannerText: TextStyle
    @Composable
    get() = TextStyle(
        fontFamily = JetBrainsSans,
        fontWeight = FontWeight.Bold,
        fontSize = 28.sp,
        lineHeight = 29.sp
    )

val JetBrainsSans: FontFamily
    @Composable
    get() {
        return FontFamily(
            font(
                name = "JetBrains Sans",
                res = "jetbrainssans_bold",
                weight = FontWeight.Bold,
                style = FontStyle.Normal
            ),
        )
    }
