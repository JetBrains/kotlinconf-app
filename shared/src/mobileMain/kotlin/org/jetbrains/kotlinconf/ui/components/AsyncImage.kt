package org.jetbrains.kotlinconf.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.layout.ContentScale
import io.ktor.client.call.body
import io.ktor.client.request.get
import org.jetbrains.kotlinconf.HTTP_CLIENT

@Composable
fun AsyncImage(
    modifier: Modifier,
    imageUrl: String,
    contentDescription: String,
    contentScale: ContentScale = ContentScale.Crop,
) {
    remember {  }
    var image by remember { mutableStateOf<ImageBitmap?>(null) }

    LaunchedEffect(imageUrl) {
        image = loadImage(imageUrl)
    }

    val currentImage = image
    if (currentImage == null) {
        ImagePlaceholder(modifier)
        return
    }

    Image(
        painter = BitmapPainter(currentImage),
        contentDescription = contentDescription,
        modifier = modifier,
        contentScale = contentScale
    )
}

@Composable
fun ImagePlaceholder(modifier: Modifier) {
    Box(modifier = modifier) {
        CircularProgressIndicator(
            modifier = Modifier.align(Alignment.Center)
        )
    }
}

suspend fun loadImage(imageUrl: String): ImageBitmap = HTTP_CLIENT
    .get(imageUrl)
    .body<ByteArray>()
    .asBitmap()

internal expect fun ByteArray.asBitmap(): ImageBitmap