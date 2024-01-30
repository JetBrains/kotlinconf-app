package org.jetbrains.kotlinconf.ui.components

import android.graphics.BitmapFactory
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap

internal actual fun ByteArray.asBitmap(): ImageBitmap {
    val bitmap = BitmapFactory.decodeByteArray(this, 0, size)
        ?: error("Failed to decode bitmap: $this")

    return bitmap.asImageBitmap()
}