package org.jetbrains.kotlinconf.ui.components

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.jetbrains.kotlinconf.ui.theme.Brand.colorGradient
import org.jetbrains.kotlinconf.ui.theme.JetBrainsSans
import org.jetbrains.kotlinconf.ui.theme.KotlinConfTheme
import org.jetbrains.kotlinconf.ui.theme.PreviewHelper
import org.jetbrains.kotlinconf.ui.theme.UI.white60
import org.jetbrains.kotlinconf.ui.utils.PreviewLightDark

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
    fullWidth: Boolean,
    modifier: Modifier = Modifier,
    day2: String = "",
) {
    val cornerRadius by animateDpAsState(if (fullWidth) 0.dp else 16.dp)
    Row(
        modifier = modifier
            .graphicsLayer {
                shape = RoundedCornerShape(cornerRadius)
                clip = true
            }
            .background(colorGradient)
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp)
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
                    color = KotlinConfTheme.colors.primaryTextWhiteFixed,
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
                    color = KotlinConfTheme.colors.primaryTextWhiteFixed,
                    modifier = Modifier.widthIn(min = 72.dp),
                )
            }
        }
        Column {
            Text(
                line1,
                style = DayHeaderStyle,
                color = KotlinConfTheme.colors.primaryTextWhiteFixed,
            )
            Text(
                line2,
                style = DayHeaderStyle,
                color = KotlinConfTheme.colors.primaryTextWhiteFixed,
            )
        }
    }
}

private data class DayHeaderPreviewParams(
    val fullWidth: Boolean,
    val multiDay: Boolean,
)

private class DayHeaderPreviewParamsProvider : PreviewParameterProvider<DayHeaderPreviewParams> {
    override val values = sequenceOf(true, false)
        .flatMap { fullWidth ->
            listOf(false, true).map { multiDay ->
                DayHeaderPreviewParams(fullWidth, multiDay)
            }
        }

    override fun getDisplayName(index: Int): String {
        val params = values.elementAt(index)
        val width = if (params.fullWidth) "full width" else "rounded"
        val days = if (params.multiDay) "multi-day" else "single day"
        return "$width, $days"
    }
}

@PreviewLightDark
@Composable
private fun DayHeaderPreview(
    @PreviewParameter(DayHeaderPreviewParamsProvider::class) params: DayHeaderPreviewParams,
) = PreviewHelper(paddingEnabled = !params.fullWidth) {
    if (params.multiDay) {
        DayHeader("MAY", "22", "Code", "Labs", fullWidth = params.fullWidth, day2 = "23")
    } else {
        DayHeader("MAY", "21", "Workshop", "Day", fullWidth = params.fullWidth)
    }
}
