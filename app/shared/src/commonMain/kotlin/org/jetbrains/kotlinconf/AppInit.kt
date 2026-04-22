package org.jetbrains.kotlinconf

import com.mmk.kmpnotifier.notification.NotifierManager
import com.mmk.kmpnotifier.notification.NotifierManager.Listener
import com.mmk.kmpnotifier.notification.PayloadData
import com.mmk.kmpnotifier.notification.configuration.NotificationPlatformConfiguration
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.jetbrains.kotlinconf.di.KotlinConfKoinApp
import org.jetbrains.kotlinconf.flags.Flags
import org.jetbrains.kotlinconf.flags.FlagsManager
import org.jetbrains.kotlinconf.navigation.navigateToSession
import org.jetbrains.kotlinconf.utils.BufferedDelegatingLogger
import org.jetbrains.kotlinconf.utils.DebugLogger
import org.jetbrains.kotlinconf.utils.Logger
import org.jetbrains.kotlinconf.utils.NoopProdLogger
import org.jetbrains.kotlinconf.utils.TaggedLogger
import org.jetbrains.kotlinconf.utils.tagged
import org.koin.core.annotation.InjectedParam
import org.koin.core.annotation.Named
import org.koin.core.annotation.Singleton
import org.koin.core.parameter.parametersOf
import org.koin.core.qualifier.named
import org.koin.dsl.KoinAppDeclaration
import org.koin.dsl.includes
import org.koin.plugin.module.dsl.startKoin

fun initApp(platformLogger: Logger, platformFlags : Flags = Flags(), configuration : KoinAppDeclaration? = null) {
    val koin = startKoin<KotlinConfKoinApp> {
        includes(configuration)
    }.koin
    koin.declare(platformFlags)
    koin.get<Unit>(named("initFlagsAndLogging")){ parametersOf(platformLogger) }
    koin.get<Unit>(named("initNotifier"))
}

@Named("initFlagsAndLogging")
@Singleton
fun initFlagsAndLogging(
    appScope: CoroutineScope,
    @InjectedParam platformLogger: Logger,
    bufferedDelegatingLogger: BufferedDelegatingLogger,
    flagsManager: FlagsManager,
) {
    appScope.launch {
        val flags = flagsManager.initAndGetFlags()
        bufferedDelegatingLogger.attach(
            when {
                flags.debugLogging -> DebugLogger(platformLogger)
                else -> NoopProdLogger()
            }
        )
    }
}

@Named("initNotifier")
@Singleton
fun initNotifier(
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
