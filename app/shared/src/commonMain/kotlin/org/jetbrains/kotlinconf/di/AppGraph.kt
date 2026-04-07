package org.jetbrains.kotlinconf.di

import com.mmk.kmpnotifier.notification.configuration.NotificationPlatformConfiguration
import dev.zacsweers.metrox.viewmodel.ViewModelGraph
import kotlinx.coroutines.CoroutineScope
import org.jetbrains.kotlinconf.ConferenceService
import org.jetbrains.kotlinconf.TimeProvider
import org.jetbrains.kotlinconf.flags.FlagsManager
import org.jetbrains.kotlinconf.utils.BufferedDelegatingLogger
import org.jetbrains.kotlinconf.utils.Logger

interface AppGraph : ViewModelGraph {
    val conferenceService: ConferenceService
    val flagsManager: FlagsManager
    val timeProvider: TimeProvider
    val logger: Logger

    @BaseUrl
    val baseUrl: String

    val scope: CoroutineScope
    val bufferedDelegatingLogger: BufferedDelegatingLogger
    val notificationConfiguration: NotificationPlatformConfiguration
}
