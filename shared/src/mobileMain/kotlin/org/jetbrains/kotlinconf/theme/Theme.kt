package org.jetbrains.kotlinconf.theme

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

@get:Composable
val Colors.agendaHeaderColor: Color
    get() =  if (isLight) grey5 else black

@get:Composable
val Colors.title: Color
    get() =  if (isLight) grey else grey5

@get:Composable
val Colors.subtitle: Color
    get() =  if (isLight) grey else grey20

@get:Composable
val Colors.divider: Color
    get() =  if (isLight) grey20 else grey80

@get:Composable
val Colors.whiteGrey: Color
    get() =  if (isLight) white else grey

@get:Composable
val Colors.whiteBlack: Color
    get() =  if (isLight) white else black

@get:Composable
val Colors.greyWhite: Color
    get() =  if (isLight) grey else white

@get:Composable
val Colors.grey5Black: Color
    get() =  if (isLight) grey5 else black

@get:Composable
val Colors.grey5Grey: Color
    get() =  if (isLight) grey5 else grey

@get:Composable
val Colors.grey5Grey90: Color
    get() =  if (isLight) grey5 else grey90

@get:Composable
val Colors.grey50Grey20: Color
    get() =  if (isLight) grey50 else grey20

@get:Composable
val Colors.grey20Grey80: Color
    get() =  if (isLight) grey20 else grey80

@get:Composable
val Colors.grey80Grey20: Color
    get() =  if (isLight) grey80 else grey20

@get:Composable
val Colors.greyGrey20: Color
    get() =  if (isLight) grey else grey20

@get:Composable
val Colors.greyGrey5: Color
    get() =  if (isLight) grey else grey5

@get:Composable
val Colors.greyGrey50: Color
    get() =  if (isLight) grey else grey50

@get:Composable
val Colors.greyGrey80: Color
    get() =  if (isLight) grey else grey80

@get:Composable
val Colors.blackGrey5: Color
    get() =  if (isLight) black else grey5

@get:Composable
val Colors.blackWhite: Color
    get() =  if (isLight) black else white

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
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colors = if (darkTheme) {
        DarkColorPalette
    } else {
        LightColorPalette
    }

    MaterialTheme(
        colors = colors,
        typography = Typography,
        shapes = Shapes,
        content = content
    )
}