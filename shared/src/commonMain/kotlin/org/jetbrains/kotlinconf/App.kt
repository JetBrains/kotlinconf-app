package org.jetbrains.kotlinconf

import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.jetbrains.compose.reload.DevelopmentEntryPoint
import org.jetbrains.kotlinconf.navigation.KotlinConfNavHost
import org.jetbrains.kotlinconf.screens.NewsDetailViewModel
import org.jetbrains.kotlinconf.screens.NewsListViewModel
import org.jetbrains.kotlinconf.screens.PrivacyPolicyViewModel
import org.jetbrains.kotlinconf.screens.ScheduleViewModel
import org.jetbrains.kotlinconf.screens.SessionViewModel
import org.jetbrains.kotlinconf.screens.SettingsViewModel
import org.jetbrains.kotlinconf.screens.SpeakerDetailViewModel
import org.jetbrains.kotlinconf.screens.SpeakersViewModel
import org.jetbrains.kotlinconf.screens.StartNotificationsViewModel
import org.jetbrains.kotlinconf.storage.ApplicationStorage
import org.jetbrains.kotlinconf.storage.MultiplatformSettingsStorage
import org.jetbrains.kotlinconf.ui.theme.KotlinConfTheme
import org.jetbrains.kotlinconf.utils.Logger
import org.jetbrains.kotlinconf.utils.NoopProdLogger
import org.koin.compose.KoinMultiplatformApplication
import org.koin.compose.koinInject
import org.koin.core.module.Module
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.koinConfiguration
import org.koin.dsl.module

@Composable
fun App(
    platformModule: Module = module {},
    onThemeChange: ((isDarkTheme: Boolean) -> Unit)? = null,
) {
    KoinMultiplatformApplication(koinConfiguration(platformModule)) {
        DevelopmentEntryPoint {
            val service = koinInject<ConferenceService>()
            val currentTheme by service.getTheme().collectAsStateWithLifecycle(initialValue = Theme.SYSTEM)
            val isDarkTheme = when (currentTheme) {
                Theme.SYSTEM -> isSystemInDarkTheme()
                Theme.LIGHT -> false
                Theme.DARK -> true
            }

            if (onThemeChange != null) {
                LaunchedEffect(isDarkTheme) { onThemeChange(isDarkTheme) }
            }

            val isOnboardingComplete = service.isOnboardingComplete()
                .collectAsStateWithLifecycle(initialValue = null)
                .value

            KotlinConfTheme(darkTheme = isDarkTheme) {
                Box(
                    Modifier
                        .fillMaxSize()
                        .background(KotlinConfTheme.colors.mainBackground)
                ) {
                    if (isOnboardingComplete != null) {
                        KotlinConfNavHost(isOnboardingComplete)
                    }
                }
            }
        }
    }
}

private fun koinConfiguration(platformModule: Module) = koinConfiguration {
    val appModule = module {
        single { APIClient(URLs.API_ENDPOINT, get()) }
        single<ApplicationStorage> { MultiplatformSettingsStorage(get()) }
        single<TimeProvider> { ServerBasedTimeProvider(get()) }
//        single<TimeProvider> { FakeTimeProvider(get()) }
        singleOf(::ConferenceService)
        single<Logger> { NoopProdLogger() }
    }

    val viewModelModule = module {
        viewModelOf(::PrivacyPolicyViewModel)
        viewModelOf(::ScheduleViewModel)
        viewModelOf(::SessionViewModel)
        viewModelOf(::SettingsViewModel)
        viewModelOf(::NewsListViewModel)
        viewModelOf(::StartNotificationsViewModel)
        viewModelOf(::NewsDetailViewModel)
        viewModelOf(::SpeakersViewModel)
        viewModelOf(::SpeakerDetailViewModel)
    }

    // Note that the order of modules here is significant, later
    // modules can override dependencies from earlier modules
    modules(platformModule, appModule, viewModelModule)
}
