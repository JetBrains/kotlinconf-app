package org.jetbrains.kotlinconf

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import com.seiko.imageloader.ImageLoader
import com.seiko.imageloader.LocalImageLoader
import org.jetbrains.kotlinconf.ui.theme.KotlinConfTheme
import org.jetbrains.kotlinconf.ui.MainScreen

const val apiEndpoint = "https://kotlinconf-app-prod.labs.jb.gg"

@Composable
fun App(context: ApplicationContext) {
    KotlinConfTheme {
        val service = remember {
            ConferenceService(context, apiEndpoint)
        }

        CompositionLocalProvider(
            LocalImageLoader provides remember { createImageLoader(context) },
        ) {
            Surface(
                modifier = Modifier.fillMaxSize(),
                color = MaterialTheme.colors.background
            ) {
                MainScreen(service)
            }
        }
    }
}

expect fun createImageLoader(context: ApplicationContext): ImageLoader
