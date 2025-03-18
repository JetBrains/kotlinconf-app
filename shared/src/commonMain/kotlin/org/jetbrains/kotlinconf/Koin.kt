package org.jetbrains.kotlinconf

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
import org.jetbrains.kotlinconf.utils.Logger
import org.jetbrains.kotlinconf.utils.NoopProdLogger
import org.koin.core.context.startKoin
import org.koin.core.module.Module
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

fun initKoin(platformModule: Module) {
    startKoin {
        val appModule = module {
            single { APIClient(URLs.API_ENDPOINT, get()) }
            single<ApplicationStorage> { MultiplatformSettingsStorage(get()) }
            single<TimeProvider> { ServerBasedTimeProvider(get()) }
//            single<TimeProvider> { FakeTimeProvider(get()) }
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
}
