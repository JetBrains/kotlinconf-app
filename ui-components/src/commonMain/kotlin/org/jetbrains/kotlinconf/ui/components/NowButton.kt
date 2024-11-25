package org.jetbrains.kotlinconf.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.unit.dp
import kotlinconfapp.ui_components.generated.resources.Res
import kotlinconfapp.ui_components.generated.resources.arrow_down_16
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.jetbrains.kotlinconf.ui.components.NowButtonState.After
import org.jetbrains.kotlinconf.ui.components.NowButtonState.Before
import org.jetbrains.kotlinconf.ui.components.NowButtonState.Current
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
    val active = time != Current
    val textColor by animateColorAsState(
        if (active) KotlinConfTheme.colors.primaryTextInverted
        else KotlinConfTheme.colors.noteText
    )
    val background by animateColorAsState(
        if (active) KotlinConfTheme.colors.primaryBackground
        else KotlinConfTheme.colors.tileBackground
    )

    Row(
        modifier = modifier
            .clip(NowButtonShape)
            .clickable(onClick = onClick, enabled = enabled)
            .background(background)
            .animateContentSize()
            .width(72.dp)
            .heightIn(min = 36.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
    ) {
        StyledText(
            text = "Now",
            style = KotlinConfTheme.typography.text2,
            color = textColor,
        )

        AnimatedVisibility(
            visible = active,
            enter = fadeIn() + expandHorizontally(clip = false, expandFrom = Alignment.Start),
            exit = fadeOut() + shrinkHorizontally(clip = false, shrinkTowards = Alignment.Start),
        ) {
            Row {
                Spacer(Modifier.width(2.dp))
                Icon(
                    // TODO review icon sizing later, https://github.com/JetBrains/kotlinconf-app/issues/175
                    painter = painterResource(Res.drawable.arrow_down_16),
                    contentDescription = "Now",
                    modifier = Modifier
                        .size(16.dp)
                        .rotate(if (time == Before) 0f else 180f),
                    tint = textColor,
                )
            }
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
