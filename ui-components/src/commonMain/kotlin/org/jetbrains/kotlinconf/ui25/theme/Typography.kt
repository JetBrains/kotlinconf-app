package org.jetbrains.kotlinconf.ui25.theme

import androidx.compose.runtime.Composable
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import kotlinconfapp.ui_components.generated.resources.Res
import kotlinconfapp.ui_components.generated.resources.jetbrains_sans_bold
import kotlinconfapp.ui_components.generated.resources.jetbrains_sans_regular
import kotlinconfapp.ui_components.generated.resources.jetbrains_sans_semibold
import org.jetbrains.compose.resources.Font

class Typography(
    val h1: TextStyle,
    val h2: TextStyle,
    val h3: TextStyle,
    val h4: TextStyle,
    val text1: TextStyle,
    val text2: TextStyle
)

internal val KotlinConfTypography: Typography
    @Composable
    get() {
        return Typography(
            h1 = TextStyle(
                fontFamily = JetBrainsSans,
                fontWeight = FontWeight.SemiBold,
                fontSize = 30.sp,
            ),
            h2 = TextStyle(
                fontFamily = JetBrainsSans,
                fontWeight = FontWeight.SemiBold,
                fontSize = 22.sp,
            ),
            h3 = TextStyle(
                fontWeight = FontWeight.SemiBold,
                fontFamily = JetBrainsSans,
                fontSize = 16.sp,
            ),
            h4 = TextStyle(
                fontWeight = FontWeight.SemiBold,
                fontFamily = JetBrainsSans,
                fontSize = 13.sp,
            ),
            text1 = TextStyle(
                fontFamily = JetBrainsSans,
                fontSize = 16.sp,
            ),
            text2 = TextStyle(
                fontFamily = JetBrainsSans,
                fontSize = 13.sp,
            ),
        )
    }

internal val JetBrainsSans: FontFamily
    @Composable
    get() = FontFamily(
        Font(Res.font.jetbrains_sans_bold, FontWeight.Bold, FontStyle.Normal),
        Font(Res.font.jetbrains_sans_semibold, FontWeight.SemiBold, FontStyle.Normal),
        Font(Res.font.jetbrains_sans_regular, FontWeight.Normal, FontStyle.Normal),
    )
