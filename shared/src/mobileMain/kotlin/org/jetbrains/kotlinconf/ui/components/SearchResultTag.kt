package org.jetbrains.kotlinconf.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.jetbrains.kotlinconf.ui.theme.grey5Grey90
import org.jetbrains.kotlinconf.ui.theme.orange
import org.jetbrains.kotlinconf.ui.theme.tagColor


@Composable
fun SearchResultTag(tag: TagView) {
    Column(
        Modifier
            .clip(shape = RoundedCornerShape(4.dp))
            .background(MaterialTheme.colors.grey5Grey90)
    ) {
        Text(
            tag.name,
            style = MaterialTheme.typography.body2.copy(color = if (tag.isActive) orange else tagColor),
            modifier = Modifier.padding(
                start = 4.dp,
                end = 4.dp,
                top = 2.dp,
                bottom = 2.dp
            )
        )
    }
}

@Preview
@Composable
fun SearchResultTagPreview() {
    SearchResultTag(TagView("Kotlin", isActive = true))
}
