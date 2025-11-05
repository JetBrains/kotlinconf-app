package org.jetbrains.kotlinconf

import com.mmk.kmpnotifier.notification.NotifierManager
import com.mmk.kmpnotifier.notification.NotifierManager.Listener
import com.mmk.kmpnotifier.notification.PayloadData
import com.mmk.kmpnotifier.notification.configuration.NotificationPlatformConfiguration
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import org.jetbrains.kotlinconf.navigation.navigateToSession
import org.jetbrains.kotlinconf.screens.AboutConferenceViewModel
import org.jetbrains.kotlinconf.screens.LicensesViewModel
import org.jetbrains.kotlinconf.screens.PrivacyNoticeViewModel
import org.jetbrains.kotlinconf.screens.ScheduleViewModel
import org.jetbrains.kotlinconf.screens.SessionViewModel
import org.jetbrains.kotlinconf.screens.SettingsViewModel
import org.jetbrains.kotlinconf.screens.SpeakerDetailViewModel
import org.jetbrains.kotlinconf.screens.SpeakersViewModel
import org.jetbrains.kotlinconf.screens.StartNotificationsViewModel
import org.jetbrains.kotlinconf.storage.ApplicationStorage
import org.jetbrains.kotlinconf.storage.MultiplatformSettingsStorage
import org.jetbrains.kotlinconf.utils.BufferedDelegatingLogger
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
    val appScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
    val koin = initKoin(appScope, platformModule, flags)
    initLogging(
        appScope = appScope,
        platformLogger = platformLogger,
        bufferedDelegatingLogger = koin.get(),
        applicationStorage = koin.get(),
    )
    initNotifier(
        configuration = koin.get(),
        logger = koin.get(),
    )
}

private fun initKoin(
    appScope: CoroutineScope,
    platformModule: Module,
    platformFlags: Flags,
): Koin {
    return startKoin {
        val appModule = module {
            single { BufferedDelegatingLogger() }
            single<Logger> { get<BufferedDelegatingLogger>() }

            single<ApplicationStorage> { MultiplatformSettingsStorage(get(), get()) }
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
            single { FlagsManager(platformFlags, get(), get()) }
            single { appScope }
            singleOf(::ConferenceService)
        }

        val viewModelModule = module {
            viewModelOf(::AboutConferenceViewModel)
            viewModelOf(::LicensesViewModel)
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

private fun initLogging(
    appScope: CoroutineScope,
    platformLogger: Logger,
    bufferedDelegatingLogger: BufferedDelegatingLogger,
    applicationStorage: ApplicationStorage,
) {
    appScope.launch {
        val flags = applicationStorage.getFlags().first()
        bufferedDelegatingLogger.attach(
            when {
                flags != null && flags.debugLogging -> DebugLogger(platformLogger)
                else -> NoopProdLogger()
            }
        )
    }
}

private fun initNotifier(
    configuration: NotificationPlatformConfiguration,
    logger: Logger,
) {
    NotifierManager.initialize(configuration)
    NotifierManager.addListener(object : Listener {
        var taggedLogger: TaggedLogger = logger.tagged("KMPNotifier")

        override fun onNotificationClicked(data: PayloadData) {
            super.onNotificationClicked(data)
            taggedLogger.log { "Notification clicked with $data" }

            val sessionId = data[PushNotificationConstants.KEY_SESSION_ID] as? String
            if (sessionId != null) {
                taggedLogger.log { "Navigating to session: $sessionId" }
                navigateToSession(SessionId(sessionId))
                return
            }

            taggedLogger.log { "No data to navigate with, ignoring notification" }
        }

        override fun onNewToken(token: String) {
            taggedLogger.log { "New token received: $token" }
        }
    })
}
