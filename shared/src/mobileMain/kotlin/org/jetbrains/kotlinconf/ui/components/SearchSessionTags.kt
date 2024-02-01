package org.jetbrains.kotlinconf.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.jetbrains.kotlinconf.theme.grey5Black


@OptIn(ExperimentalLayoutApi::class)
@Composable
fun SearchSessionTags(tags: List<TagView>, onClick: (tag: TagView) -> Unit) {
    FlowRow(
        Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colors.grey5Black)
            .padding(12.dp)
    ) {
        tags.forEach { tag ->
            SearchTag(name = tag.name, isActive = tag.isActive, onClick = { onClick(tag) })
        }
    }
}

val MOCK_TAGS: List<TagView> = listOf(
    TagView("Kotlin", true),
    TagView("Coroutines", false),
    TagView("Multiplatform", true),
    TagView("Android", false),
)

@Preview
@Composable
fun SearchSessionTagsPreview() {
    SearchSessionTags(MOCK_TAGS) {}
}