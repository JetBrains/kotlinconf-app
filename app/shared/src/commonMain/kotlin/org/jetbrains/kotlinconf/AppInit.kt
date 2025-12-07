package org.jetbrains.kotlinconf

import com.mmk.kmpnotifier.notification.NotifierManager
import com.mmk.kmpnotifier.notification.NotifierManager.Listener
import com.mmk.kmpnotifier.notification.PayloadData
import com.mmk.kmpnotifier.notification.configuration.NotificationPlatformConfiguration
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import org.jetbrains.kotlinconf.di.AppGraph
import org.jetbrains.kotlinconf.navigation.navigateToSession
import org.jetbrains.kotlinconf.storage.ApplicationStorage
import org.jetbrains.kotlinconf.utils.BufferedDelegatingLogger
import org.jetbrains.kotlinconf.utils.DebugLogger
import org.jetbrains.kotlinconf.utils.Logger
import org.jetbrains.kotlinconf.utils.NoopProdLogger
import org.jetbrains.kotlinconf.utils.TaggedLogger
import org.jetbrains.kotlinconf.utils.tagged

fun initApp(
    appGraph: AppGraph,
    platformLogger: Logger,
) {
    initLogging(
        appScope = appGraph.scope,
        platformLogger = platformLogger,
        bufferedDelegatingLogger = appGraph.bufferedDelegatingLogger,
        applicationStorage = appGraph.applicationStorage,
    )
    initNotifier(
        configuration = appGraph.notificationConfiguration,
        logger = appGraph.bufferedDelegatingLogger,
    )
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
