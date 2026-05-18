package org.jetbrains.kotlinconf

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.core.net.toUri

@Composable
actual fun rememberMapHandler(): MapHandler {
    val context = LocalContext.current
    return remember(context) { AndroidMapHandler(context) }
}

private class AndroidMapHandler(private val context: Context) : MapHandler {
    override fun openNavigation(address: String) {
        val encodedAddress = Uri.encode(address)
        val gmmIntentUri = "geo:0,0?q=$encodedAddress".toUri()
        val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        context.startActivity(mapIntent)
    }
}
