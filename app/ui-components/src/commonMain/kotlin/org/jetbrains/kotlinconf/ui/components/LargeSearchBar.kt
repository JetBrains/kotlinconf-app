package org.jetbrains.kotlinconf.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.kotlinconf.ui.generated.resources.UiRes
import org.jetbrains.kotlinconf.ui.generated.resources.close_24
import org.jetbrains.kotlinconf.ui.generated.resources.main_header_search_clear
import org.jetbrains.kotlinconf.ui.generated.resources.main_header_search_hint
import org.jetbrains.kotlinconf.ui.generated.resources.search_24
import org.jetbrains.kotlinconf.ui.theme.KotlinConfTheme
import org.jetbrains.kotlinconf.ui.theme.PreviewHelper
import org.jetbrains.kotlinconf.ui.utils.PreviewInteractionSource
import org.jetbrains.kotlinconf.ui.utils.PreviewLightDark

@Composable
fun LargeSearchBar(
    searchValue: String,
    onSearchValueChange: (String) -> Unit,
    onClear: () -> Unit,
    modifier: Modifier = Modifier,
    hasAdditionalInputs: Boolean = false,
    hint: String = stringResource(UiRes.string.main_header_search_hint),
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
) {
    val focused by interactionSource.collectIsFocusedAsState()
    val hovered by interactionSource.collectIsHoveredAsState()

    val strokeColor by animateColorAsState(
        when {
            focused -> KotlinConfTheme.colors.strokeInputFocus
            hovered -> KotlinConfTheme.colors.strokeHalf
            else -> KotlinConfTheme.colors.strokePale
        }
    )

    Row(
        modifier = modifier
            .border(
                width = 1.dp,
                color = strokeColor,
                shape = KotlinConfTheme.shapes.roundedCornerSm,
            )
            .clip(KotlinConfTheme.shapes.roundedCornerSm)
            .heightIn(min = 40.dp)
            .background(KotlinConfTheme.colors.mainBackground),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            modifier = Modifier
                .padding(start = 8.dp)
                .size(24.dp),
            painter = painterResource(UiRes.drawable.search_24),
            contentDescription = null,
            tint = KotlinConfTheme.colors.primaryText,
        )

        var focusRequested by rememberSaveable { mutableStateOf(false) }
        val focusRequester = remember { FocusRequester() }
        if (!focusRequested) {
            LaunchedEffect(Unit) {
                focusRequester.requestFocus()
                focusRequested = true
            }
        }

        SearchInput(
            searchValue = searchValue,
            onSearchValueChange = onSearchValueChange,
            hint = hint,
            focusRequester = focusRequester,
            modifier = Modifier.weight(1f),
            interactionSource = interactionSource,
        )

        AnimatedVisibility(
            visible = searchValue.isNotEmpty() || hasAdditionalInputs,
            enter = fadeIn(tween(200)),
            exit = fadeOut(tween(100)),
        ) {
            TopMenuButton(
                icon = UiRes.drawable.close_24,
                onClick = {
                    onSearchValueChange("")
                    onClear()
                    focusRequester.requestFocus()
                },
                contentDescription = stringResource(UiRes.string.main_header_search_clear),
                large = true,
            )
        }
    }
}

private class SearchValueProvider : PreviewParameterProvider<String> {
    override val values = sequenceOf("", "Kotlin")
    override fun getDisplayName(index: Int) = if (index == 0) "empty" else "with input"
}

@PreviewLightDark
@Composable
private fun LargeSearchBarPreview(
    @PreviewParameter(SearchValueProvider::class) searchValue: String,
) = PreviewHelper {
    LargeSearchBar(
        searchValue = searchValue,
        onSearchValueChange = {},
        onClear = {},
    )
    LargeSearchBar(
        searchValue = searchValue,
        onSearchValueChange = {},
        onClear = {},
        interactionSource = PreviewInteractionSource.Hovered,
    )
    LargeSearchBar(
        searchValue = searchValue,
        onSearchValueChange = {},
        onClear = {},
        interactionSource = PreviewInteractionSource.Focused,
    )
}
