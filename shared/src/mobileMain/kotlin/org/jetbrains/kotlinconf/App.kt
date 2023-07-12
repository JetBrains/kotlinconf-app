package org.jetbrains.kotlinconf.org.jetbrains.kotlinconf

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import org.jetbrains.kotlinconf.ConferenceService
import org.jetbrains.kotlinconf.android.theme.KotlinConfTheme
import org.jetbrains.kotlinconf.android.ui.MainScreen
import org.jetbrains.kotlinconf.storage.ApplicationContext

@Composable
fun App(context: ApplicationContext) {
    KotlinConfTheme {
        val service = ConferenceService(
            context,
            "https://kotlin-conf-staging.labs.jb.gg/"
        )

        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colors.background
        ) {
            MainScreen(service)
        }
    }
}
