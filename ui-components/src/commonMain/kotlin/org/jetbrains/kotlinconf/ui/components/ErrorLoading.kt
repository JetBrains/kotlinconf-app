package org.jetbrains.kotlinconf.ui.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.EaseInCubic
import androidx.compose.animation.core.EaseOutCubic
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import kotlinconfapp.ui_components.generated.resources.Res
import kotlinconfapp.ui_components.generated.resources.action_error_reload
import kotlinconfapp.ui_components.generated.resources.kodee_error_loading_anim_bg
import kotlinconfapp.ui_components.generated.resources.kodee_error_lost
import kotlinconfapp.ui_components.generated.resources.loading
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.jetbrains.kotlinconf.ui.theme.KotlinConfTheme
import org.jetbrains.kotlinconf.ui.theme.PreviewHelper
import org.jetbrains.kotlinconf.ui.theme.UI
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun MinorError(
    message: String,
    modifier: Modifier = Modifier,
) {
    ErrorText(
        message,
        modifier
            .fillMaxWidth()
            .padding(32.dp)
            .semantics {
                liveRegion = LiveRegionMode.Assertive
            }
    )
}

@Composable
fun MajorError(
    message: String,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Image(
            imageVector = vectorResource(Res.drawable.kodee_error_lost),
            contentDescription = null,
        )
        Spacer(Modifier.height(16.dp))
        ErrorText(
            message = message,
            modifier = Modifier.semantics {
                liveRegion = LiveRegionMode.Assertive
            }
        )
    }
}

@Composable
fun NormalErrorWithLoading(
    message: String,
    modifier: Modifier = Modifier,
    isLoading: Boolean = false,
    loadingText: String = stringResource(Res.string.loading),
    retryText: String = stringResource(Res.string.action_error_reload),
    onRetry: (() -> Unit)? = null,
) {
    Column(
        modifier = modifier.animateContentSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Loading(enabled = isLoading)

        Spacer(Modifier.height(16.dp))

        AnimatedContent(
            isLoading,
            transitionSpec = {
                val animIn = fadeIn(
                    animationSpec = tween(220, delayMillis = 100)
                ) + expandVertically(
                    expandFrom = Alignment.CenterVertically,
                    animationSpec = tween(100, easing = EaseInCubic)
                )
                val animOut = fadeOut(animationSpec = tween(90))
                animIn togetherWith animOut
            },
            modifier = Modifier.fillMaxWidth(),
        ) { loadingState ->
            if (loadingState) {
                ErrorText(loadingText)
            } else {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    ErrorText(message)
                    if (onRetry != null) {
                        Spacer(Modifier.height(12.dp))
                        Button(retryText, onRetry)
                    }
                }
            }
        }
    }
}

@Composable
private fun ErrorText(
    message: String,
    modifier: Modifier = Modifier,
) {
    Text(
        message,
        color = KotlinConfTheme.colors.secondaryText,
        modifier = modifier.widthIn(max = 220.dp),
        style = KotlinConfTheme.typography.text1.copy(
            textAlign = TextAlign.Center,
        )
    )
}

@Composable
fun Loading(
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
) {
    Box(modifier = modifier.size(95.dp, 80.dp)) {
        val kodeeLoadingPainter = rememberVectorPainter(
            vectorResource(Res.drawable.kodee_error_loading_anim_bg)
        )

        var lastValue by remember { mutableStateOf(0f) }
        val animationProgress = remember { Animatable(lastValue) }

        LaunchedEffect(enabled) {
            if (enabled) {
                try {
                    animationProgress.animateTo(
                        targetValue = lastValue + 2 * PI.toFloat(),
                        animationSpec = infiniteRepeatable(
                            animation = tween(durationMillis = 1000, easing = LinearEasing),
                            repeatMode = RepeatMode.Restart,
                        )
                    )
                } finally {
                    withContext(NonCancellable) {
                        animationProgress.animateTo(
                            targetValue = animationProgress.value + 1.25f * PI.toFloat(),
                            animationSpec = tween(durationMillis = 800, easing = EaseOutCubic),
                        ) {
                            lastValue = value
                        }
                    }
                }
            }
        }

        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(95f / 80f),
        ) {
            val scaleFactor = size.width / 95.dp.toPx() * density

            val circleSize = 4.3f * scaleFactor
            val circleRadius = 12f * scaleFactor

            fun drawEye(offsetX: Float, offsetY: Float) {
                val eyeCenterX = offsetX + circleRadius + circleSize
                val eyeCenterY = offsetY + circleRadius + circleSize

                for (i in 0..<6) {
                    val angle = i * (2 * PI / 6) + (PI / 6)
                    val x = eyeCenterX + circleRadius * cos(angle).toFloat()
                    val y = eyeCenterY + circleRadius * sin(angle).toFloat()

                    val dotAngle = i * (2 * PI / 6) + (PI / 6)
                    val alpha = ((sin(animationProgress.value - dotAngle) + 1) / 2).toFloat()

                    drawCircle(
                        color = lerp(UI.white20, UI.white100, alpha),
                        radius = circleSize,
                        center = Offset(x, y),
                    )
                }
            }

            with(kodeeLoadingPainter) {
                draw(Size(95f, 80f) * scaleFactor)
            }

            drawEye(12f * scaleFactor, 34f * scaleFactor)
            drawEye(50.5f * scaleFactor, 34f * scaleFactor)
        }
    }
}


@Preview
@Composable
internal fun LoadingPreview() {
    PreviewHelper {
        Loading(enabled = true)
        Loading(enabled = false)

        NormalErrorWithLoading(
            message = "Error message",
            isLoading = true,
            loadingText = "Loading...",
            retryText = "Retry",
            onRetry = {}
        )

        var scope = rememberCoroutineScope()
        var loading by remember { mutableStateOf(false) }
        NormalErrorWithLoading(
            message = "Error message",
            isLoading = loading,
            loadingText = "Loading...",
            retryText = "Retry",
            onRetry = {
                scope.launch {
                    loading = true
                    delay(1000)
                    loading = false
                }
            }
        )

        NormalErrorWithLoading(
            message = "Error message",
            isLoading = false,
            loadingText = "Loading...",
            retryText = "Retry"
        )

        MinorError(
            message = "Minor error message"
        )

        MajorError(
            message = "Major error message"
        )
    }
}
