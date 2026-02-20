package org.jetbrains.kotlinconf.ui.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.kotlinconf.ui.components.NowButtonState.After
import org.jetbrains.kotlinconf.ui.components.NowButtonState.Before
import org.jetbrains.kotlinconf.ui.components.NowButtonState.Current
import org.jetbrains.kotlinconf.ui.generated.resources.UiRes
import org.jetbrains.kotlinconf.ui.generated.resources.arrow_down_16
import org.jetbrains.kotlinconf.ui.generated.resources.now
import org.jetbrains.kotlinconf.ui.theme.KotlinConfTheme
import org.jetbrains.kotlinconf.ui.theme.PreviewHelper

private val NowButtonShape = RoundedCornerShape(
    topEndPercent = 50,
    bottomEndPercent = 50,
)

enum class NowButtonState {
    Before, Current, After,
}

@Composable
fun NowButton(
    time: NowButtonState,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = time != Current,
) {
    NowButtonImpl(
        time = time,
        enabled = enabled,
        onClick = onClick,
        minWidth = 72.dp,
        minHeight = 36.dp,
        textStyle = KotlinConfTheme.typography.text2,
        shape = NowButtonShape,
        modifier = modifier
    )
}

@Composable
fun FloatingNowButton(
    time: NowButtonState,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = time != Current,
) {
    NowButtonImpl(
        time = time,
        enabled = enabled,
        onClick = onClick,
        minWidth = 90.dp,
        minHeight = 48.dp,
        textStyle = KotlinConfTheme.typography.text1,
        shape = CircleShape,
        modifier = modifier
    )
}

@Composable
private fun NowButtonImpl(
    time: NowButtonState,
    enabled: Boolean,
    onClick: () -> Unit,
    minWidth: Dp,
    minHeight: Dp,
    textStyle: TextStyle,
    shape: Shape,
    modifier: Modifier = Modifier,
) {
    val active = time != Current
    val textColor by animateColorAsState(
        if (active) KotlinConfTheme.colors.primaryTextWhiteFixed
        else KotlinConfTheme.colors.noteText,
        ColorSpringSpec,
    )
    val background by animateColorAsState(
        if (active) KotlinConfTheme.colors.primaryBackground
        else KotlinConfTheme.colors.tileBackground,
        ColorSpringSpec,
    )
    val startPadding by animateDpAsState(if (active) 6.dp else 0.dp)

    Row(
        modifier = modifier
            .clip(shape)
            .clickable(onClick = onClick, enabled = enabled)
            .background(background)
            .sizeIn(minWidth = minWidth, minHeight = minHeight)
            .padding(start = startPadding),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
    ) {
        Text(
            text = stringResource(UiRes.string.now),
            style = textStyle,
            color = textColor,
        )

        AnimatedContent(
            targetState = time,
            transitionSpec = {
                (fadeIn() + expandHorizontally(clip = false, expandFrom = Alignment.Start)) togetherWith
                        (fadeOut() + shrinkHorizontally(clip = false, shrinkTowards = Alignment.Start))
            },
            modifier = Modifier.height(16.dp)
        ) { targetTime ->
            if (targetTime == Current) return@AnimatedContent
            Spacer(Modifier.width(2.dp))
            Icon(
                painter = painterResource(UiRes.drawable.arrow_down_16),
                contentDescription = null,
                modifier = Modifier
                    .size(16.dp)
                    .graphicsLayer { rotationZ = if (targetTime == Before) 0f else 180f },
                tint = textColor,
            )
        }
    }
}

@Preview
@Composable
internal fun NowButtonPreview() {
    PreviewHelper {
        NowButton(Before, {})
        NowButton(Current, {})
        NowButton(After, {})
    }
}

@Preview
@Composable
internal fun FloatingNowButtonPreview() {
    PreviewHelper {
        FloatingNowButton(Before, {})
        FloatingNowButton(Current, {})
        FloatingNowButton(After, {})
    }
}
