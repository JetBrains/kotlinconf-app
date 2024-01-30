package org.jetbrains.kotlinconf.ui.components

import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.toComposeImageBitmap
import org.jetbrains.skia.Image

internal actual fun ByteArray.asBitmap(): ImageBitmap = Image.makeFromEncoded(this)
    .toComposeImageBitmap()