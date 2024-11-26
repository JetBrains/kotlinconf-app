package org.jetbrains.kotlinconf

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import org.jetbrains.kotlinconf.ui.components.StyledText
import org.jetbrains.kotlinconf.ui.theme.KotlinConfTheme

const val apiEndpoint = "https://kotlinconf-app-prod.labs.jb.gg"

@Composable
fun App(context: ApplicationContext) {
    KotlinConfTheme {
        val service = remember {
            ConferenceService(context, apiEndpoint)
        }
        Box(
            Modifier.fillMaxSize()
                .background(KotlinConfTheme.colors.mainBackground),
            contentAlignment = Alignment.Center
        ) {
            StyledText("Placeholder")
        }
    }
}
