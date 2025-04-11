package org.jetbrains.kotlinconf

import com.mmk.kmpnotifier.notification.NotifierManager
import com.mmk.kmpnotifier.notification.NotifierManager.Listener
import com.mmk.kmpnotifier.notification.PayloadData
import com.mmk.kmpnotifier.notification.configuration.NotificationPlatformConfiguration
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import org.jetbrains.kotlinconf.navigation.navigateToNews
import org.jetbrains.kotlinconf.navigation.navigateToSession
import org.jetbrains.kotlinconf.screens.AboutConferenceViewModel
import org.jetbrains.kotlinconf.screens.LicensesViewModel
import org.jetbrains.kotlinconf.screens.NewsDetailViewModel
import org.jetbrains.kotlinconf.screens.NewsListViewModel
import org.jetbrains.kotlinconf.screens.PrivacyNoticeViewModel
import org.jetbrains.kotlinconf.screens.ScheduleViewModel
import org.jetbrains.kotlinconf.screens.SessionViewModel
import org.jetbrains.kotlinconf.screens.SettingsViewModel
import org.jetbrains.kotlinconf.screens.SpeakerDetailViewModel
import org.jetbrains.kotlinconf.screens.SpeakersViewModel
import org.jetbrains.kotlinconf.screens.StartNotificationsViewModel
import org.jetbrains.kotlinconf.storage.ApplicationStorage
import org.jetbrains.kotlinconf.storage.MultiplatformSettingsStorage
import org.jetbrains.kotlinconf.utils.DebugLogger
import org.jetbrains.kotlinconf.utils.Logger
import org.jetbrains.kotlinconf.utils.NoopProdLogger
import org.jetbrains.kotlinconf.utils.TaggedLogger
import org.jetbrains.kotlinconf.utils.tagged
import org.koin.core.Koin
import org.koin.core.context.startKoin
import org.koin.core.module.Module
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

fun initApp(
    platformLogger: Logger,
    platformModule: Module,
    flags: Flags = Flags(),
) {
    val koin = initKoin(platformLogger, platformModule, flags)
    initNotifier(configuration = koin.get(), logger = koin.get())
}

private fun initKoin(
    platformLogger: Logger,
    platformModule: Module,
    platformFlags: Flags,
): Koin {
    return startKoin {
        val appModule = module {
            single<ApplicationStorage> { MultiplatformSettingsStorage(get()) }
            single {
                val flags = get<ApplicationStorage>().getFlagsBlocking()
                val endpoint = when {
                    flags != null && (flags != platformFlags) -> URLs.STAGING_URL
                    else -> URLs.PRODUCTION_URL
                }
                APIClient(endpoint, get())
            }
            single<TimeProvider> {
                val flags = get<ApplicationStorage>().getFlagsBlocking()
                when {
                    flags != null && flags.useFakeTime -> FakeTimeProvider(get())
                    else -> ServerBasedTimeProvider(get())
                }
            }
            single<Logger> {
                val flags = get<ApplicationStorage>().getFlagsBlocking()
                when {
                    flags != null && flags.debugLogging -> DebugLogger(platformLogger)
                    else -> NoopProdLogger()
                }
            }
            single { FlagsManager(platformFlags, get(), get()) }
            single { CoroutineScope(SupervisorJob() + Dispatchers.Default) }
            singleOf(::ConferenceService)
        }

        val viewModelModule = module {
            viewModelOf(::AboutConferenceViewModel)
            viewModelOf(::LicensesViewModel)
            viewModelOf(::NewsDetailViewModel)
            viewModelOf(::NewsListViewModel)
            viewModelOf(::PrivacyNoticeViewModel)
            viewModelOf(::ScheduleViewModel)
            viewModelOf(::SessionViewModel)
            viewModelOf(::SettingsViewModel)
            viewModelOf(::SpeakerDetailViewModel)
            viewModelOf(::SpeakersViewModel)
            viewModelOf(::StartNotificationsViewModel)
        }

        // Note that the order of modules here is significant, later
        // modules can override dependencies from earlier modules
        modules(platformModule, appModule, viewModelModule)
    }.koin
}

private fun initNotifier(
    configuration: NotificationPlatformConfiguration,
    logger: Logger,
) {
    NotifierManager.initialize(configuration)
    NotifierManager.addListener(object : Listener {
        var taggedLogger: TaggedLogger? = logger.tagged("KMPNotifier")

        override fun onNotificationClicked(data: PayloadData) {
            super.onNotificationClicked(data)
            taggedLogger?.log { "Notification clicked with $data" }

            val newsId = data[PushNotificationConstants.KEY_NEWS_ID] as? String
            if (newsId != null) {
                taggedLogger?.log { "Navigating to news: $newsId" }
                navigateToNews(newsId)
                return
            }

            val sessionId = data[PushNotificationConstants.KEY_SESSION_ID] as? String
            if (sessionId != null) {
                taggedLogger?.log { "Navigating to session: $sessionId" }
                navigateToSession(SessionId(sessionId))
                return
            }

            taggedLogger?.log { "No data to navigate with, ignoring notification" }
        }

        override fun onNewToken(token: String) {
            taggedLogger?.log { "New token received: $token" }
        }
    })
}
