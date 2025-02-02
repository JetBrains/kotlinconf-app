package org.jetbrains.kotlinconf

import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import org.jetbrains.compose.reload.DevelopmentEntryPoint
import org.jetbrains.kotlinconf.navigation.KotlinConfNavHost
import org.jetbrains.kotlinconf.screens.ScheduleViewModel
import org.jetbrains.kotlinconf.screens.SessionViewModel
import org.jetbrains.kotlinconf.screens.SettingsViewModel
import org.jetbrains.kotlinconf.ui.theme.KotlinConfTheme
import org.koin.compose.KoinMultiplatformApplication
import org.koin.compose.koinInject
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.koinConfiguration
import org.koin.dsl.module

@Composable
fun App(context: ApplicationContext) {
    KoinMultiplatformApplication(koinConfiguration(context)) {
        DevelopmentEntryPoint {
            val service = koinInject<ConferenceService>()
            val currentTheme by service.theme.collectAsState()
            val isDarkTheme = when (currentTheme) {
                Theme.SYSTEM -> isSystemInDarkTheme()
                Theme.LIGHT -> false
                Theme.DARK -> true
            }
            KotlinConfTheme(darkTheme = isDarkTheme) {
                Box(
                    Modifier
                        .fillMaxSize()
                        .background(KotlinConfTheme.colors.mainBackground)
                        .windowInsetsPadding(WindowInsets.safeDrawing)
                ) {
                    KotlinConfNavHost()
                }
            }
        }
    }
}

private fun koinConfiguration(context: ApplicationContext) = koinConfiguration {
    val appModule = module {
        single<ApplicationContext> { context }
        single<ConferenceService> {
            ConferenceService(
                get<ApplicationContext>(),
                URLs.API_ENDPOINT
            )
        }
    }

    val viewModelModule = module {
        viewModelOf(::ScheduleViewModel)
        viewModelOf(::SessionViewModel)
        viewModelOf(::SettingsViewModel)
    }

    modules(appModule, viewModelModule)
}
