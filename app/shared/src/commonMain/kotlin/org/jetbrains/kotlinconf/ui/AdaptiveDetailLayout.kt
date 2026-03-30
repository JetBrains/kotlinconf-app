package org.jetbrains.kotlinconf.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.plus
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.kotlinconf.HideKeyboardOnDragHandler
import org.jetbrains.kotlinconf.ScrollToTopHandler
import org.jetbrains.kotlinconf.navigation.LocalUseNativeNavigation
import org.jetbrains.kotlinconf.ui.components.TopMenuButton
import org.jetbrains.kotlinconf.ui.generated.resources.UiRes
import org.jetbrains.kotlinconf.ui.generated.resources.arrow_left_24
import org.jetbrains.kotlinconf.ui.generated.resources.main_header_back
import org.jetbrains.kotlinconf.utils.LocalWindowSize
import org.jetbrains.kotlinconf.utils.WindowSize
import org.jetbrains.kotlinconf.utils.bottomInsetPadding
import org.jetbrains.kotlinconf.utils.topInsetPadding

@Composable
fun AdaptiveDetailLayout(
    compactHeader: @Composable () -> Unit,
    compactContentHeader: @Composable ColumnScope.() -> Unit,
    largeContentHeader: @Composable ColumnScope.() -> Unit,
    unifiedContent: @Composable ColumnScope.() -> Unit,
    largeMainContent: @Composable ColumnScope.() -> Unit,
    largeSideContent: @Composable ColumnScope.() -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val scrollState = rememberScrollState()

    ScrollToTopHandler(scrollState)
    HideKeyboardOnDragHandler(scrollState)

    val useNativeNavigation = LocalUseNativeNavigation.current
    val contentModifier = Modifier
        .verticalScroll(scrollState)
        .padding(bottom = 24.dp)
        .padding((if (useNativeNavigation) topInsetPadding() else PaddingValues(0.dp)) + bottomInsetPadding())

    when (LocalWindowSize.current) {
        WindowSize.Compact -> {
            Column(modifier) {
                compactHeader()
                Column(contentModifier.padding(horizontal = 12.dp)) {
                    compactContentHeader()
                    unifiedContent()
                }
            }
        }

        WindowSize.Medium -> {
            Box(modifier) {
                Row(
                    contentModifier
                        .padding(start = if (useNativeNavigation) 24.dp else 56.dp, top = 24.dp)
                        .padding(horizontal = 24.dp)
                ) {
                    Spacer(Modifier.weight(1f))
                    Column(
                        Modifier.widthIn(max = 640.dp)
                    ) {
                        largeContentHeader()
                        unifiedContent()
                    }
                    Spacer(Modifier.weight(1f))
                }
                if (!useNativeNavigation) {
                    Row {
                        Spacer(Modifier.weight(1f))
                        TopMenuButton(
                            icon = UiRes.drawable.arrow_left_24,
                            contentDescription = stringResource(UiRes.string.main_header_back),
                            onClick = onBack,
                            large = true,
                            modifier = Modifier
                                .padding(start = 24.dp, top = 24.dp)
                        )
                        Spacer(
                            Modifier
                                .padding(horizontal = 24.dp)
                                .widthIn(max = 640.dp)
                                .fillMaxWidth()
                        )
                        Spacer(Modifier.weight(1f))
                    }
                }
            }
        }

        WindowSize.Large -> {
            Box(modifier) {
                Column(
                    contentModifier
                        .padding(start = if (useNativeNavigation) 24.dp else 56.dp, top = 24.dp)
                        .padding(horizontal = 24.dp)
                ) {
                    largeContentHeader()
                    Row {
                        Column(Modifier.weight(1f)) {
                            largeMainContent()
                        }
                        Spacer(Modifier.width(48.dp))
                        Column(Modifier.weight(1f)) {
                            largeSideContent()
                        }
                    }
                }
                if (!useNativeNavigation) {
                    TopMenuButton(
                        icon = UiRes.drawable.arrow_left_24,
                        contentDescription = stringResource(UiRes.string.main_header_back),
                        onClick = onBack,
                        large = true,
                        modifier = Modifier
                            .padding(start = 24.dp, top = 24.dp)
                    )
                }
            }
        }
    }
}
