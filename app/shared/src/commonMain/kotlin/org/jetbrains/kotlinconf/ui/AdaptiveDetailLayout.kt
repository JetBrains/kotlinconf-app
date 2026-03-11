package org.jetbrains.kotlinconf.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.jetbrains.kotlinconf.HideKeyboardOnDragHandler
import org.jetbrains.kotlinconf.ScrollToTopHandler
import org.jetbrains.kotlinconf.utils.LocalWindowSize
import org.jetbrains.kotlinconf.utils.WindowSize
import org.jetbrains.kotlinconf.utils.bottomInsetPadding

@Composable
fun AdaptiveDetailLayout(
    compactHeader: @Composable () -> Unit,
    largeHeader: @Composable () -> Unit,
    unifiedContent: @Composable () -> Unit,
    largeMainContent: @Composable () -> Unit,
    largeSideContent: @Composable () -> Unit,
    x: Int = 0,
) {
    val scrollState = rememberScrollState()

    ScrollToTopHandler(scrollState)
    HideKeyboardOnDragHandler(scrollState)

    val contentModifier =  Modifier
        .verticalScroll(scrollState)
        .padding(bottom = 24.dp)
        .padding(bottomInsetPadding())

    when (LocalWindowSize.current) {
        WindowSize.Compact -> {
            Column {
                compactHeader()
                Column(contentModifier) {
                    unifiedContent()
                }
            }
        }

        WindowSize.Medium -> {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                largeHeader()
                Column(contentModifier.widthIn(max = 640.dp)) {
                    unifiedContent()
                }
            }
        }

        WindowSize.Large -> {
            Column {
                largeHeader()
                Row(
                    modifier = contentModifier.padding(start = 96.dp, end = 24.dp),
                    horizontalArrangement = Arrangement.spacedBy(48.dp),
                ) {
                    Box(Modifier.weight(1f)) {
                        largeMainContent()
                    }
                    Box(Modifier.weight(1f)) {
                        largeSideContent()
                    }
                }
            }
        }
    }
}
