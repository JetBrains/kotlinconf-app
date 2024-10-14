package org.jetbrains.kotlinconf.ui.theme

import androidx.compose.material.Typography
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import kotlinconfapp.shared.generated.resources.Res
import kotlinconfapp.shared.generated.resources.jetbrains_sans_bold
import kotlinconfapp.shared.generated.resources.jetbrains_sans_regular
import kotlinconfapp.shared.generated.resources.jetbrains_sans_semibold
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.Font

val Typography: Typography
    @Composable
    get() {
        return Typography(
            h2 = TextStyle(
                fontFamily = JetBrainsSans,
                fontWeight = FontWeight.SemiBold,
                fontSize = 35.sp,
                lineHeight = 42.sp
            ),
            h3 = TextStyle(
                fontFamily = JetBrainsSans,
                fontWeight = FontWeight.SemiBold,
                fontSize = 20.sp,
                lineHeight =28.sp
            ),
            h4 = TextStyle(
                fontFamily = JetBrainsSans,
                fontWeight = FontWeight.SemiBold,
                fontSize = 16.sp,
                lineHeight = 24.sp
            ),
            body1 = TextStyle(
                fontFamily = JetBrainsSans,
                fontWeight = FontWeight.Normal,
                fontSize = 20.sp,
                lineHeight = 28.sp
            ),
            body2 = TextStyle(
                fontFamily = JetBrainsSans,
                fontWeight = FontWeight.Normal,
                fontSize = 16.sp,
                lineHeight = 24.sp
            )
        )
    }

val text2: TextStyle
    @Composable
    get() = TextStyle(
        fontFamily = JetBrainsSans,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp
    )

val Typography.bannerText: TextStyle
    @Composable
    get() = TextStyle(
        fontFamily = JetBrainsSans,
        fontWeight = FontWeight.Bold,
        fontSize = 28.sp,
        lineHeight = 29.sp
    )

@OptIn(ExperimentalResourceApi::class)
val JetBrainsSans: FontFamily
    @Composable
    get() = FontFamily(
        Font(Res.font.jetbrains_sans_bold, FontWeight.Bold, FontStyle.Normal),
        Font(Res.font.jetbrains_sans_semibold, FontWeight.SemiBold, FontStyle.Normal),
        Font(Res.font.jetbrains_sans_regular, FontWeight.Normal, FontStyle.Normal),
    )
