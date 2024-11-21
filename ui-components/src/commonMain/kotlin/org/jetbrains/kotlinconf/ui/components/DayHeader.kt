package org.jetbrains.kotlinconf.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.jetbrains.kotlinconf.ui.theme.Brand.colorGradient
import org.jetbrains.kotlinconf.ui.theme.JetBrainsSans
import org.jetbrains.kotlinconf.ui.theme.KotlinConfTheme
import org.jetbrains.kotlinconf.ui.theme.PreviewHelper
import org.jetbrains.kotlinconf.ui.theme.UI.white60

private val DayDateStyle
    @Composable
    get() = TextStyle(
        fontFamily = JetBrainsSans,
        fontWeight = FontWeight.Bold,
        fontSize = 58.sp,
        textAlign = TextAlign.Center,
    )

private val DayHeaderStyle
    @Composable
    get() = TextStyle(
        fontFamily = JetBrainsSans,
        fontWeight = FontWeight.SemiBold,
        fontSize = 22.sp,
    )

@Composable
fun DayHeader(
    month: String,
    day: String,
    line1: String,
    line2: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .background(colorGradient)
            .width(300.dp)
            .padding(horizontal = 16.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy((-10).dp)
        ) {
            StyledText(
                month,
                style = KotlinConfTheme.typography.text2,
                color = white60,
            )
            StyledText(
                day,
                style = DayDateStyle,
                color = KotlinConfTheme.colors.primaryTextInverted,
                modifier = Modifier.width(70.dp),
            )
            StyledText(
                "",
                style = KotlinConfTheme.typography.text2,
                color = Color.Transparent,
            )
        }
        Column {
            StyledText(
                line1,
                style = DayHeaderStyle,
                color = KotlinConfTheme.colors.primaryTextInverted,
            )
            StyledText(
                line2,
                style = DayHeaderStyle,
                color = KotlinConfTheme.colors.primaryTextInverted,
            )
        }
    }
}

@Preview
@Composable
internal fun DayHeaderPreview() {
    PreviewHelper {
        DayHeader("TEST", "1", "Test", "Data")
        DayHeader("MAY", "21", "Workshop", "Day")
        DayHeader("MAY", "23", "Conference", "Day 2")
    }
}