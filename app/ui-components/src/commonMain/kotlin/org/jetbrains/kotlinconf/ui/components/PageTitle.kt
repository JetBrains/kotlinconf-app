package org.jetbrains.kotlinconf.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.text.InlineTextContent
import androidx.compose.foundation.text.appendInlineContent
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.Placeholder
import androidx.compose.ui.text.PlaceholderVerticalAlign
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource
import org.jetbrains.kotlinconf.ui.generated.resources.UiRes
import org.jetbrains.kotlinconf.ui.generated.resources.action_bookmark
import org.jetbrains.kotlinconf.ui.generated.resources.bookmark_24
import org.jetbrains.kotlinconf.ui.generated.resources.bookmark_24_fill
import org.jetbrains.kotlinconf.ui.generated.resources.lightning_16_fill
import org.jetbrains.kotlinconf.ui.generated.resources.session_codelab
import org.jetbrains.kotlinconf.ui.generated.resources.session_education
import org.jetbrains.kotlinconf.ui.theme.KotlinConfTheme
import org.jetbrains.kotlinconf.ui.theme.PreviewHelper
import androidx.compose.ui.tooling.preview.PreviewLightDark

private const val iconId = "iconId'"
private const val eduPlaceholder = "[e]"
private const val codelabPlaceholder = "[c]"

private val iconPlaceholder = Placeholder(
    width = 1.1.em,
    height = 1.em,
    placeholderVerticalAlign = PlaceholderVerticalAlign.TextCenter,
)

private val pageTitleInlineContent = mapOf(
    iconId to InlineTextContent(iconPlaceholder) { placeholder ->
        InlineIconContent(placeholder)
    },
)

@Composable
private fun InlineIconContent(placeholder: String) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.CenterStart,
    ) {
        Icon(
            imageVector = vectorResource(
                when (placeholder) {
                    eduPlaceholder -> UiRes.drawable.session_education
                    codelabPlaceholder -> UiRes.drawable.session_codelab
                    else -> UiRes.drawable.session_codelab // Shouldn't happen, but let's not throw
                }
            ),
            contentDescription = null,
            tint = KotlinConfTheme.colors.accentText,
            modifier = Modifier.fillMaxHeight(0.9f).aspectRatio(1f),
        )
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun PageTitle(
    time: String,
    title: String,
    tags: Set<String>,
    lightning: Boolean,
    timeNote: String?,
    isLive: Boolean,
    bookmarked: Boolean,
    onBookmark: (Boolean) -> Unit,
    large: Boolean,
    modifier: Modifier = Modifier,
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(12.dp),
        modifier = modifier.semantics(mergeDescendants = true) {},
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.heightIn(min = if (large) 40.dp else 24.dp),
        ) {
            TimeBlock(lightning, time, timeNote, isLive)
            Spacer(Modifier.weight(1f))
            EndButtons(bookmarked, onBookmark, large)
        }
        Title(title, tags)
        Tags(tags)
    }
}

@Composable
private fun RowScope.TimeBlock(
    lightning: Boolean,
    time: String,
    timeNote: String?,
    isLive: Boolean
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (lightning) {
            Icon(
                modifier = Modifier.size(16.dp),
                painter = painterResource(UiRes.drawable.lightning_16_fill),
                contentDescription = null,
                tint = KotlinConfTheme.colors.orangeText,
            )
        }

        Text(
            text = time,
            style = KotlinConfTheme.typography.h3,
            color = KotlinConfTheme.colors.primaryText
        )
    }

    if (timeNote != null) {
        Text(
            text = timeNote,
            style = KotlinConfTheme.typography.text1,
            color = KotlinConfTheme.colors.noteText,
            maxLines = 1,
        )
    }

    AnimatedVisibility(isLive, enter = fadeIn(), exit = fadeOut()) {
        NowLabel(
            textStyle = KotlinConfTheme.typography.text1,
        )
    }
}

@Composable
private fun EndButtons(bookmarked: Boolean, onBookmark: (Boolean) -> Unit, large: Boolean) {
    val iconTint by animateColorAsState(
        if (bookmarked) KotlinConfTheme.colors.orangeText
        else KotlinConfTheme.colors.primaryText
    )

    Icon(
        modifier = Modifier
            .size(if (large) 40.dp else 24.dp)
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
            if (bookmarked) UiRes.drawable.bookmark_24_fill else UiRes.drawable.bookmark_24
        ),
        contentDescription = stringResource(UiRes.string.action_bookmark),
        tint = iconTint,
    )
}

@Composable
private fun Title(title: String, tags: Set<String>) {
    val isCodelab = "Codelab" in tags
    val isEducation = "Education" in tags
    val hasIcon = isCodelab || isEducation

    Text(
        text = if (hasIcon) {
            buildAnnotatedString {
                appendInlineContent(
                    id = iconId,
                    alternateText = when {
                        isEducation -> eduPlaceholder
                        isCodelab -> codelabPlaceholder
                        else -> codelabPlaceholder // Should never happen
                    },
                )
                append(title)
            }
        } else {
            AnnotatedString(title)
        },
        style = KotlinConfTheme.typography.h1,
        color = KotlinConfTheme.colors.primaryText,
        selectable = true,
        inlineContent = if (hasIcon) pageTitleInlineContent else emptyMap(),
        modifier = Modifier.semantics { heading() }.widthIn(max = 640.dp),
    )
}

@Composable
private fun Tags(tags: Set<String>) {
    FlowRow(
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp),
        modifier = Modifier.widthIn(max = 640.dp),
    ) {
        tags.forEach { tag ->
            CardTag(label = tag, selected = false)
        }
    }
}

@PreviewLightDark
@Composable
private fun PageTitleLightningPreview() = PreviewHelper {
    var bookmarked by remember { mutableStateOf(false) }
    PageTitle(
        time = "May 23, 13:00 - 13:20",
        title = "My Incredible Talk About Kotlin Multiplatform",
        tags = setOf("Lightning talk", "Intermediate", "Libraries"),
        bookmarked = bookmarked,
        lightning = true,
        timeNote = null,
        isLive = false,
        onBookmark = { bookmarked = it },
        large = false,
    )
}

@PreviewLightDark
@Composable
private fun PageTitleRegularPreview() = PreviewHelper {
    var bookmarked by remember { mutableStateOf(false) }
    PageTitle(
        time = "May 21, 9:00 - 9:40",
        title = "A Wonderful Server-side Kotlin Talk",
        tags = setOf(
            "Regular talk",
            "Beginner",
            "Server-side",
            "Several",
            "Other",
            "Fictional",
            "Tags",
            "To",
            "Display"
        ),
        bookmarked = bookmarked,
        lightning = false,
        timeNote = null,
        isLive = false,
        onBookmark = { bookmarked = it },
        large = false,
    )
}

@PreviewLightDark
@Composable
internal fun PageTitleWithNotesPreview() {
    PreviewHelper {
        PageTitle(
            time = "May 23, 15:00 - 15:20",
            title = "Starting Soon Talk Example",
            tags = setOf("Lightning talk", "Beginner"),
            bookmarked = true,
            lightning = true,
            timeNote = "in 22 min",
            isLive = false,
            onBookmark = { },
            large = false,
        )
        Spacer(Modifier.height(16.dp))
        PageTitle(
            time = "May 23, 14:00 - 14:40",
            title = "Live Talk Example",
            tags = setOf("Regular talk", "Advanced"),
            bookmarked = false,
            lightning = false,
            timeNote = null,
            isLive = true,
            onBookmark = { },
            large = false,
        )
    }
}

@PreviewLightDark
@Composable
internal fun LargePageTitleWithPreview() {
    PreviewHelper {
        PageTitle(
            time = "May 23, 15:00 - 15:20",
            title = "Starting Soon Talk Example",
            tags = setOf("Lightning talk", "Beginner"),
            bookmarked = true,
            lightning = true,
            timeNote = "in 22 min",
            isLive = false,
            onBookmark = { },
            large = true,
        )
    }
}

