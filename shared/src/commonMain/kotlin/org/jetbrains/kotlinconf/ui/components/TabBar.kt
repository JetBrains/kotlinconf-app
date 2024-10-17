package org.jetbrains.kotlinconf.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.kotlinconf.ui.HDivider
import org.jetbrains.kotlinconf.ui.theme.whiteGrey

interface Tab {
    @OptIn(ExperimentalResourceApi::class)
    val title: StringResource
}

@Composable
fun <T : Tab> TabBar(
    tabs: List<T>,
    selected: T,
    onSelect: (item: T) -> Unit = {},
) {
    Column(
        Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colors.whiteGrey)
    ) {
        Row(
            Modifier
                .fillMaxWidth()
                .padding(top = 10.dp),
            horizontalArrangement = Arrangement.Center
        ) {
            tabs.forEach { tab ->
                val isActive = selected == tab
                TabButton(tab, isSelected = isActive) { onSelect(tab) }
            }
        }

        HDivider(Modifier.padding(top = 10.dp))
    }
}

