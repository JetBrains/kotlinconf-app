package org.jetbrains.kotlinconf.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
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
    modifier: Modifier = Modifier,
    day2: String = "",
) {
    Row(
        modifier = modifier
            .background(colorGradient)
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp)
            .semantics(mergeDescendants = true) {
                heading()
            },
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy((-10).dp)
            ) {
                Text(
                    month,
                    style = KotlinConfTheme.typography.text2,
                    color = white60,
                )
                Text(
                    day,
                    style = DayDateStyle,
                    color = KotlinConfTheme.colors.primaryTextInverted,
                    modifier = Modifier.widthIn(min = 72.dp),
                )
                Text(
                    "",
                    style = KotlinConfTheme.typography.text2,
                    color = Color.Transparent,
                )
            }
            if (day2.isNotEmpty()) {
                Text(
                    "-$day2",
                    style = DayDateStyle,
                    color = KotlinConfTheme.colors.primaryTextInverted,
                    modifier = Modifier.widthIn(min = 72.dp),
                )
            }
        }
        Column {
            Text(
                line1,
                style = DayHeaderStyle,
                color = KotlinConfTheme.colors.primaryTextInverted,
            )
            Text(
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
        DayHeader("MAY", "22", "Code", "Labs", day2 = "23")
    }
}
