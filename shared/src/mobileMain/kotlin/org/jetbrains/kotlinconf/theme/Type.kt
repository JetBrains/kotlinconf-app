package org.jetbrains.kotlinconf.theme

import androidx.compose.material.Typography
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import kotlinconfapp.shared.generated.resources.Res
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.Font

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

@OptIn(ExperimentalResourceApi::class)
val JetBrainsMono: FontFamily
    @Composable
    get() {
        return FontFamily(
            Font(
                Res.font.jetbrainsmono_regular,
                FontWeight.Normal,
                FontStyle.Normal
            ),
            Font(
                Res.font.jetbrainsmono_extrabold,
                FontWeight.ExtraBold,
                FontStyle.Normal
            ),
        )
    }

val Typography.bannerText: TextStyle
    @Composable
    get() = TextStyle(
        fontFamily = JetBrainsSansBold,
        fontWeight = FontWeight.Bold,
        fontSize = 28.sp,
        lineHeight = 29.sp
    )

@OptIn(ExperimentalResourceApi::class)
val JetBrainsSansBold: FontFamily
    @Composable
    get() = FontFamily(
        Font(Res.font.jetbrainssans_bold, FontWeight.Bold, FontStyle.Normal),
    )
