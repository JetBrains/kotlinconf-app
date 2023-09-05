package org.jetbrains.kotlinconf.ui.components

import android.graphics.BitmapFactory
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.Resource

@OptIn(ExperimentalResourceApi::class)
internal actual suspend fun Resource.asBitmap(): ImageBitmap {
    val bytes = readBytes()
    val bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
        ?: error("Failed to decode bitmap: $this")

    return bitmap.asImageBitmap()
}