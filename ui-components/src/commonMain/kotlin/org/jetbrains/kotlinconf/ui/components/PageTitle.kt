package org.jetbrains.kotlinconf.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.selection.toggleable
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import kotlinconfapp.ui_components.generated.resources.Res
import kotlinconfapp.ui_components.generated.resources.action_bookmark
import kotlinconfapp.ui_components.generated.resources.bookmark_24
import kotlinconfapp.ui_components.generated.resources.bookmark_24_fill
import kotlinconfapp.ui_components.generated.resources.lightning_16_fill
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.jetbrains.kotlinconf.ui.theme.KotlinConfTheme
import org.jetbrains.kotlinconf.ui.theme.PreviewHelper

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun PageTitle(
    time: String,
    title: String,
    tags: List<String>,
    lightning: Boolean,
    bookmarked: Boolean,
    onBookmark: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(12.dp),
        modifier = modifier.semantics(mergeDescendants = true) {},
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            if (lightning) {
                Icon(
                    modifier = Modifier.size(16.dp),
                    painter = painterResource(Res.drawable.lightning_16_fill),
                    contentDescription = null,
                    tint = KotlinConfTheme.colors.orangeText,
                )
            }

            StyledText(
                text = time,
                style = KotlinConfTheme.typography.h3,
                color = KotlinConfTheme.colors.primaryText
            )

            Spacer(Modifier.weight(1f))

            val iconTint by animateColorAsState(
                if (bookmarked) KotlinConfTheme.colors.orangeText
                else KotlinConfTheme.colors.primaryText
            )
            Icon(
                modifier = Modifier
                    .size(24.dp)
                    .wrapContentSize(unbounded = true)
                    .toggleable(
                        value = bookmarked,
                        onValueChange = { onBookmark(it) },
                        role = Role.Checkbox,
                        indication = null,
                        interactionSource = null,
                    )
                    .padding(12.dp),
                painter = painterResource(
                    if (bookmarked) Res.drawable.bookmark_24_fill else Res.drawable.bookmark_24
                ),
                contentDescription = stringResource(Res.string.action_bookmark),
                tint = iconTint,
            )
        }
        StyledText(
            text = title,
            style = KotlinConfTheme.typography.h1,
            color = KotlinConfTheme.colors.primaryText
        )
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            tags.forEach { tag ->
                CardTag(label = tag, selected = false)
            }
        }
    }
}

@Preview
@Composable
internal fun PageTitlePreview() {
    PreviewHelper {
        var bookmarked1 by remember { mutableStateOf(false) }
        PageTitle(
            time = "May 23, 13:00 - 13:20",
            title = "My Incredible Talk About Kotlin Multiplatform",
            tags = listOf("Lightning talk", "Intermediate", "Libraries"),
            bookmarked = bookmarked1,
            lightning = true,
            onBookmark = { bookmarked1 = it },
        )

        var bookmarked2 by remember { mutableStateOf(false) }
        PageTitle(
            time = "May 21, 9:00 - 9:40",
            title = "A Wonderful Server-side Kotlin Talk",
            tags = listOf("Regular talk", "Beginner", "Server-side"),
            bookmarked = bookmarked2,
            lightning = false,
            onBookmark = { bookmarked2 = it },
        )
    }
}
