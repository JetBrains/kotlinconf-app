package org.jetbrains.kotlinconf.ui.components

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.jetbrains.kotlinconf.ui.theme.KotlinConfTheme
import org.jetbrains.kotlinconf.ui.theme.PreviewHelper
import kotlin.time.Duration.Companion.milliseconds

private val ScrollIndicatorShape = RoundedCornerShape(percent = 50)

@Composable
fun ScrollIndicator(
    pageCount: Int,
    selectedPage: Int,
    modifier: Modifier = Modifier,
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = modifier,
    ) {
        repeat(pageCount) { index ->
            val width by animateDpAsState(
                if (index == selectedPage) 24.dp else 8.dp
            )
            Box(
                Modifier
                    .clip(ScrollIndicatorShape)
                    .height(8.dp)
                    .width(width)
                    .background(KotlinConfTheme.colors.scrollIndicatorFill)
            )
        }
    }
}

@Preview
@Composable
internal fun ScrollIndicatorPreview() {
    PreviewHelper {
        var selectedPage by remember { mutableIntStateOf(0) }
        LaunchedEffect(Unit) {
            while (true) {
                delay(1000.milliseconds)
                selectedPage = (selectedPage + 1) % 10
            }
        }
        ScrollIndicator(
            pageCount = 10,
            selectedPage = selectedPage,
        )
    }
}
