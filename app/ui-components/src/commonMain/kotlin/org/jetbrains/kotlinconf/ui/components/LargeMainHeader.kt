package org.jetbrains.kotlinconf.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.jetbrains.kotlinconf.ui.generated.resources.UiRes
import org.jetbrains.kotlinconf.ui.generated.resources.bookmark_24
import org.jetbrains.kotlinconf.ui.generated.resources.view_grid_24
import org.jetbrains.kotlinconf.ui.generated.resources.view_list_24
import org.jetbrains.kotlinconf.ui.theme.KotlinConfTheme
import org.jetbrains.kotlinconf.ui.theme.PreviewHelper
import org.jetbrains.kotlinconf.ui.utils.WidePreviewLightDark

@Composable
fun LargeMainHeader(
    title: String,
    endContent: @Composable () -> Unit = {},
    modifier: Modifier = Modifier,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .heightIn(min = 56.dp)
            .padding(horizontal = 24.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Text(
            text = title,
            style = KotlinConfTheme.typography.h1,
            modifier = Modifier.weight(1f)
        )

        endContent()
    }
}

@Composable
@WidePreviewLightDark
private fun LargeMainHeaderPreview() = PreviewHelper {
    LargeMainHeader(
        "Main",
        endContent = {
            TopMenuButton(
                icon = UiRes.drawable.bookmark_24,
                contentDescription = "Bookmarked",
                selected = false,
                large = true,
                onToggle = { }
            )

            var searchValue by remember { mutableStateOf("") }
            LargeSearchBar(
                searchValue = searchValue,
                onSearchValueChange = {
                    searchValue = it
                },
                onClear = {
                    searchValue = ""
                },
                modifier = Modifier.width(370.dp),
            )

            val options = listOf(
                HeaderToggleOption(UiRes.drawable.view_list_24, "List view"),
                HeaderToggleOption(UiRes.drawable.view_grid_24, "Grid view"),
            )
            var selectedIndex by remember { mutableStateOf(1) }
            HeaderToggleButton(
                options = options,
                selectedIndex = selectedIndex,
                onSelect = { selectedIndex = it },
            )
        }
    )
}
