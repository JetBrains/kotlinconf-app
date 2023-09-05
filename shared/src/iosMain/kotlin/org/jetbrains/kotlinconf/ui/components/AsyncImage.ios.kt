package org.jetbrains.kotlinconf.ui.components

import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.toComposeImageBitmap
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.Resource
import org.jetbrains.skia.Image

@OptIn(ExperimentalResourceApi::class)
internal actual suspend fun Resource.asBitmap(): ImageBitmap {
    val bytes = readBytes()
    return Image.makeFromEncoded(bytes)
        .toComposeImageBitmap()
}