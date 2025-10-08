package org.jetbrains.kotlinconf.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.unit.dp
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewDynamicColors
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.tooling.preview.PreviewScreenSizes
import androidx.compose.ui.tooling.preview.UiModes.UI_MODE_NIGHT_MASK
import androidx.compose.ui.tooling.preview.UiModes.UI_MODE_NIGHT_NO
import androidx.compose.ui.tooling.preview.UiModes.UI_MODE_NIGHT_YES
import androidx.compose.ui.tooling.preview.UiModes.UI_MODE_TYPE_NORMAL
import androidx.compose.ui.tooling.preview.Wallpapers
import androidx.compose.ui.tooling.preview.Wallpapers.RED_DOMINATED_EXAMPLE
import org.jetbrains.kotlinconf.ui.theme.KotlinConfTheme
import org.jetbrains.kotlinconf.ui.theme.PreviewHelper

private val ButtonShape = RoundedCornerShape(size = 100.dp)

@Composable
fun Button(
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    primary: Boolean = false,
    enabled: Boolean = true,
) {
    val backgroundColor by animateColorAsState(
        if (primary) KotlinConfTheme.colors.primaryBackground
        else Color.Transparent
    )
    val borderColor by animateColorAsState(
        if (primary) Color.Transparent
        else KotlinConfTheme.colors.strokeHalf
    )
    val textColor by animateColorAsState(
        if (primary) KotlinConfTheme.colors.primaryTextInverted
        else KotlinConfTheme.colors.primaryText
    )
    val alpha by animateFloatAsState(if (enabled) 1f else 0.5f)

    Box(
        modifier = modifier
            .alpha(alpha)
            .heightIn(min = 56.dp)
            .border(width = 1.dp, color = borderColor, shape = ButtonShape)
            .clip(ButtonShape)
            .then(if (enabled) Modifier.clickable(onClick = onClick) else Modifier)
            .semantics { role = Role.Button }
            .background(backgroundColor)
            .padding(horizontal = 32.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            label,
            style = KotlinConfTheme.typography.text1,
            color = textColor,
        )
    }
}


@Retention(AnnotationRetention.BINARY)
@Target(AnnotationTarget.ANNOTATION_CLASS, AnnotationTarget.FUNCTION)
@Preview(name = "85%", fontScale = 0.85f)
@Preview(name = "100%", fontScale = 1.0f)
@Preview(name = "115%", fontScale = 1.15f)
@Preview(name = "130%", fontScale = 1.3f)
@Preview(name = "150%", fontScale = 1.5f)
@Preview(name = "180%", fontScale = 1.8f)
@Preview(name = "200%", fontScale = 2f)
annotation class PreviewFontScale

@PreviewFontScale
@Composable
internal fun ButtonPreview() {
    KotlinConfTheme(darkTheme = false) {
        Button("Font scale preview internal", { }, primary = true)
    }
}

@Preview(name = "115%", fontScale = 1.15f, device = "spec:parent=pixel_5,orientation=landscape,navigation=buttons")

@androidx.compose.ui.tooling.preview.PreviewFontScale
@Composable
internal fun ButtonPreviewExt() {
    KotlinConfTheme(darkTheme = false) {
        Button("Font scale preview external", { }, primary = true)
    }
}
