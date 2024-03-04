package org.jetbrains.kotlinconf.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.jetbrains.kotlinconf.ui.theme.grey5Black


@OptIn(ExperimentalLayoutApi::class)
@Composable
fun SearchSessionTags(tags: List<String>, active: List<String>, onClick: (tag: String) -> Unit) {
    FlowRow(
        Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colors.grey5Black)
            .padding(12.dp)
    ) {
        tags.forEach { tag ->
            SearchTag(name = tag, isActive = tag in active, onClick = { onClick(tag) })
        }
    }
}
