package org.jetbrains.kotlinconf

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.EaseOutQuad
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.layer.drawLayer
import androidx.compose.ui.graphics.rememberGraphicsLayer
import androidx.compose.ui.layout.ContentScale

@Composable
fun ThemeChangeAnimation(
    isDarkTheme: Boolean,
    enabled: Boolean,
    content: @Composable (appliedIsDarkTheme: Boolean) -> Unit,
) {
    var appliedIsDarkTheme by remember { mutableStateOf(isDarkTheme) }
    val graphicsLayer = rememberGraphicsLayer()
    var bitmap by remember { mutableStateOf<ImageBitmap?>(null) }
    val bitmapVisibility = remember { Animatable(1f) }

    LaunchedEffect(isDarkTheme) {
        if (enabled && appliedIsDarkTheme != isDarkTheme) {
            bitmap = graphicsLayer.toImageBitmap()
            bitmapVisibility.snapTo(1f)
            appliedIsDarkTheme = isDarkTheme
            bitmapVisibility.animateTo(0f, tween(500, easing = EaseOutQuad))
            bitmap = null
        } else {
            appliedIsDarkTheme = isDarkTheme
        }
    }

    Box(Modifier.fillMaxSize()) {
        Box(
            Modifier
                .fillMaxSize()
                .then(
                    if (enabled) {
                        Modifier.drawWithContent {
                            graphicsLayer.record {
                                this@drawWithContent.drawContent()
                            }
                            drawLayer(graphicsLayer)
                        }
                    } else {
                        Modifier
                    }
                )
        ) {
            content(appliedIsDarkTheme)
        }

        bitmap?.let { bmp ->
            Image(
                bitmap = bmp,
                contentDescription = null,
                modifier = Modifier
                    .fillMaxHeight(fraction = bitmapVisibility.value)
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter),
                contentScale = ContentScale.Crop,
                alignment = Alignment.BottomCenter
            )
        }
    }
}
