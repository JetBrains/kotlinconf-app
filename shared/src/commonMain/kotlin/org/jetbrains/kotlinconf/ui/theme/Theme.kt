package org.jetbrains.kotlinconf.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.Colors
import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorPalette = darkColors(
    primary = white,
    primaryVariant = grey20,
    secondary = grey50,
    surface = grey80,
    background = black,
)

val Colors.agendaHeaderColor: Color
    get() = if (isLight) grey5 else black

val Colors.title: Color
    get() = if (isLight) grey else grey5

val Colors.subtitle: Color
    get() = if (isLight) grey else grey20

val Colors.divider: Color
    get() = if (isLight) grey20 else grey80

val Colors.whiteGrey: Color
    get() = if (isLight) white else grey

val Colors.greyWhite: Color
    get() = if (isLight) grey else white

val Colors.grey5Black: Color
    get() = if (isLight) grey5 else black

val Colors.menuSelected: Color
    get() = if (isLight) grey15 else greySelected

val Colors.grey50Grey20: Color
    get() = if (isLight) grey50 else grey20

val Colors.grey20Grey80: Color
    get() = if (isLight) grey20 else grey80

val Colors.grey80Grey20: Color
    get() = if (isLight) grey80 else grey20

val Colors.greyGrey20: Color
    get() = if (isLight) grey else grey20

val Colors.greyGrey5: Color
    get() = if (isLight) grey else grey5

val Colors.greyGrey50: Color
    get() = if (isLight) grey else grey50

val Colors.greyGrey80: Color
    get() = if (isLight) grey else grey80

val Colors.blackGrey5: Color
    get() = if (isLight) black else grey5

val Colors.blackWhite: Color
    get() = if (isLight) black else white

val Colors.tagColor: Color
    get() = if (isLight) tagGrey else tagDarkGrey

val Colors.mapColor: Color
    get() = if (isLight) grey5 else black

val Colors.text3: Color
    get() = if (isLight) text3Light else text3Dark

private val LightColorPalette = lightColors(
    primary = grey,
    primaryVariant = grey,
    secondary = grey50,
    background = white,
    surface = grey5,
    onSurface = white
)

@Composable
fun KotlinConfTheme(
    darkTheme: Boolean = isSystemInDarkTheme(), content: @Composable () -> Unit
) {
    val colors = if (darkTheme) {
        DarkColorPalette
    } else {
        LightColorPalette
    }

    MaterialTheme(
        colors = colors, typography = Typography, shapes = Shapes, content = content
    )
}