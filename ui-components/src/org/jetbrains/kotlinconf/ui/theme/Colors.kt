package org.jetbrains.kotlinconf.ui.theme

import androidx.compose.ui.graphics.Color
import org.jetbrains.kotlinconf.ui.theme.Brand.magenta100
import org.jetbrains.kotlinconf.ui.theme.Brand.magenta20
import org.jetbrains.kotlinconf.ui.theme.Brand.magenta50
import org.jetbrains.kotlinconf.ui.theme.Brand.magentaTextDark
import org.jetbrains.kotlinconf.ui.theme.Brand.orange
import org.jetbrains.kotlinconf.ui.theme.Brand.purple100
import org.jetbrains.kotlinconf.ui.theme.Brand.purpleTextDark
import org.jetbrains.kotlinconf.ui.theme.UI.black05
import org.jetbrains.kotlinconf.ui.theme.UI.black100
import org.jetbrains.kotlinconf.ui.theme.UI.black15
import org.jetbrains.kotlinconf.ui.theme.UI.black20
import org.jetbrains.kotlinconf.ui.theme.UI.black40
import org.jetbrains.kotlinconf.ui.theme.UI.black60
import org.jetbrains.kotlinconf.ui.theme.UI.black70
import org.jetbrains.kotlinconf.ui.theme.UI.black80
import org.jetbrains.kotlinconf.ui.theme.UI.greyDark
import org.jetbrains.kotlinconf.ui.theme.UI.greyLight
import org.jetbrains.kotlinconf.ui.theme.UI.white05
import org.jetbrains.kotlinconf.ui.theme.UI.white10
import org.jetbrains.kotlinconf.ui.theme.UI.white100
import org.jetbrains.kotlinconf.ui.theme.UI.white20
import org.jetbrains.kotlinconf.ui.theme.UI.white30
import org.jetbrains.kotlinconf.ui.theme.UI.white50
import org.jetbrains.kotlinconf.ui.theme.UI.white70
import org.jetbrains.kotlinconf.ui.theme.UI.white80

class Colors(
    val isDark: Boolean,

    val activeBackground: Color,
    val mainBackground: Color,
    val primaryBackground: Color,
    val tileBackground: Color,

    val cardBackgroundPast: Color,
    val scrollIndicatorFill: Color,

    val strokeFull: Color,
    val strokeInputFocus: Color,
    val strokeHalf: Color,
    val strokePale: Color,

    val accentText: Color,
    val longText: Color,
    val noteText: Color,
    val orangeText: Color,
    val placeholderText: Color,
    val primaryText: Color,
    val primaryTextInverted: Color,
    val purpleText: Color,
    val secondaryText: Color,

    val toggleOn: Color,
    val toggleOff: Color,
)

val KotlinConfLightColors = Colors(
    isDark = false,

    activeBackground = magenta20,
    mainBackground = white100,
    primaryBackground = magenta100,
    tileBackground = black05,

    cardBackgroundPast = black05,
    scrollIndicatorFill = black15,

    strokeFull = black100,
    strokeInputFocus = black80,
    strokeHalf = black40,
    strokePale = black15,

    accentText = magenta100,
    longText = black70,
    noteText = black40,
    orangeText = orange,
    placeholderText = black20,
    primaryText = black100,
    primaryTextInverted = white100,
    purpleText = purple100,
    secondaryText = black60,

    toggleOff = greyLight,
    toggleOn = magenta100,
)

val KotlinConfDarkColors = Colors(
    isDark = true,

    activeBackground = magenta50,
    mainBackground = black100,
    primaryBackground = magenta100,
    tileBackground = white10,

    cardBackgroundPast = white05,
    scrollIndicatorFill = white30,

    strokeFull = white100,
    strokeInputFocus = white80,
    strokeHalf = white50,
    strokePale = white20,

    accentText = magentaTextDark,
    longText = white70,
    noteText = white50,
    orangeText = orange,
    placeholderText = white30,
    primaryText = white100,
    primaryTextInverted = white100,
    purpleText = purpleTextDark,
    secondaryText = white70,

    toggleOff = greyDark,
    toggleOn = magentaTextDark,
)
