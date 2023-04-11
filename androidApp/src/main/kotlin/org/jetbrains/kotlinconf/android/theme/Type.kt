package org.jetbrains.kotlinconf.android.theme

import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.text.*
import androidx.compose.ui.text.font.*
import androidx.compose.ui.unit.*
import com.jetbrains.kotlinconf.R

private val jetBrainsMono = FontFamily(
    Font(R.font.jetbrains_mono_regular),
    Font(R.font.jetbrains_mono_extrabold)
)

val KonfTypography = Typography(
    h2 = TextStyle(
        fontFamily = jetBrainsMono,
        fontWeight = FontWeight.ExtraBold,
        fontSize = 32.sp,
        lineHeight = 36.sp
    ),
    h4 = TextStyle(
        fontFamily = jetBrainsMono,
        fontWeight = FontWeight.ExtraBold,
        fontSize = 16.sp,
        lineHeight = 24.sp
    ),
    body2 = TextStyle(
        fontFamily = jetBrainsMono,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp
    )
)

@get:Composable
val Typography.t2: TextStyle
    get() = TextStyle(
        fontFamily = jetBrainsMono,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp
    )

