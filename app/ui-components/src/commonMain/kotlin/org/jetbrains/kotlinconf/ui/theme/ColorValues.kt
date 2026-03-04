package org.jetbrains.kotlinconf.ui.theme

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

internal object Brand {
    val purple100 = Color(0xFF8F00E7)
    val purple90 = Color(0xE58F00E7)
    val purple80 = Color(0xCC8F00E7)
    val purple70 = Color(0xB28F00E7)
    val purple60 = Color(0x998F00E7)
    val purple50 = Color(0x808F00E7)
    val purple40 = Color(0x668F00E7)
    val purple30 = Color(0x4D8F00E7)
    val purple20 = Color(0x338F00E7)
    val purple10 = Color(0x1A8F00E7)
    val purple05 = Color(0x0D8F00E7)

    val magenta100 = Color(0xFFC202D7)
    val magenta90 = Color(0xE5C202D7)
    val magenta80 = Color(0xCCC202D7)
    val magenta70 = Color(0xB2C202D7)
    val magenta60 = Color(0x99C202D7)
    val magenta50 = Color(0x80C202D7)
    val magenta40 = Color(0x66C202D7)
    val magenta30 = Color(0x4DC202D7)
    val magenta20 = Color(0x33C202D7)
    val magenta10 = Color(0x1AC202D7)
    val magenta05 = Color(0x0DC202D7)

    val pink100 = Color(0xFFE00189)
    val pink90 = Color(0xE5E00189)
    val pink80 = Color(0xCCE00189)
    val pink70 = Color(0xB2E00189)
    val pink60 = Color(0x99E00189)
    val pink50 = Color(0x80E00189)
    val pink40 = Color(0x66E00189)
    val pink30 = Color(0x4DE00189)
    val pink20 = Color(0x33E00189)
    val pink10 = Color(0x1AE00189)
    val pink05 = Color(0x0DE00189)

    val orange = Color(0xFFFF5A13)

    val colorGradient = Brush.horizontalGradient(
        0f to Color(0xFF8F00E7),
        0.5f to Color(0xFFE00189),
        1f to Color(0xFFFF9100),
    )

    val purpleTextDark = Color(0xFFBF56FF)
    val magentaTextDark = Color(0xFFED44FF)
    val pinkTextDark = Color(0xFFFF66C3)
    val orangeTextDark = Color(0xFFFF9100)
}

internal object UI {
    val black100 = Color(0xFF19191C)
    val black90 = Color(0xE519191C)
    val black80 = Color(0xCC19191C)
    val black70 = Color(0xB219191C)
    val black60 = Color(0x9919191C)
    val black50 = Color(0x8019191C)
    val black40 = Color(0x6619191C)
    val black30 = Color(0x4D19191C)
    val black20 = Color(0x3319191C)
    val black15 = Color(0x2619191C)
    val black10 = Color(0x1A19191C)
    val black05 = Color(0x0D19191C)

    val white100 = Color(0xFFFFFFFF)
    val white90 = Color(0xE5FFFFFF)
    val white80 = Color(0xCCFFFFFF)
    val white70 = Color(0xB2FFFFFF)
    val white60 = Color(0x99FFFFFF)
    val white50 = Color(0x80FFFFFF)
    val white40 = Color(0x66FFFFFF)
    val white30 = Color(0x4DFFFFFF)
    val white20 = Color(0x33FFFFFF)
    val white15 = Color(0x26FFFFFF)
    val white10 = Color(0x1AFFFFFF)
    val white05 = Color(0x0DFFFFFF)

    val grey100 = Color(0xFFE8E8E8)
    val grey400 = Color(0xFFA3A3A4)
    val grey500 = Color(0xFF757577)
    val grey900 = Color(0xFF303033)
}
