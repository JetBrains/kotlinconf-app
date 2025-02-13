package org.jetbrains.kotlinconf.ui.components

import androidx.compose.animation.core.EaseInOutQuart
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.widthIn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import kotlinconfapp.ui_components.generated.resources.Res
import kotlinconfapp.ui_components.generated.resources.kodee_error_loading
import kotlinconfapp.ui_components.generated.resources.kodee_error_loading_dark
import kotlinconfapp.ui_components.generated.resources.loading
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.jetbrains.kotlinconf.ui.theme.KotlinConfTheme
import org.jetbrains.kotlinconf.ui.theme.PreviewHelper

@Composable
fun Loading(
    modifier: Modifier = Modifier,
    message: String = stringResource(Res.string.loading),
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        val rotation by rememberInfiniteTransition().animateFloat(
            initialValue = 0f,
            targetValue = 360f,
            animationSpec = infiniteRepeatable(
                animation = tween(2000, easing = EaseInOutQuart)
            )
        )
        Image(
            imageVector = vectorResource(
                if (KotlinConfTheme.colors.isDark) Res.drawable.kodee_error_loading_dark
                else Res.drawable.kodee_error_loading
            ),
            contentDescription = null,
            modifier = Modifier.graphicsLayer {
                rotationZ = rotation
            }
        )
        Spacer(Modifier.height(32.dp))
        StyledText(
            message,
            color = KotlinConfTheme.colors.secondaryText,
            modifier = Modifier.widthIn(max = 220.dp),
            style = KotlinConfTheme.typography.text1.copy(
                textAlign = TextAlign.Center,
            )
        )
    }
}

@Preview
@Composable
internal fun LoadingPreview() {
    PreviewHelper {
        Loading()
    }
}
