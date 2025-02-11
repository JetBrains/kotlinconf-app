package org.jetbrains.kotlinconf

import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.window.CanvasBasedWindow
import com.russhwolf.settings.ExperimentalSettingsApi
import com.russhwolf.settings.ObservableSettings
import com.russhwolf.settings.StorageSettings
import com.russhwolf.settings.observable.makeObservable
import org.jetbrains.kotlinconf.ui.initCoil
import org.koin.dsl.module

@OptIn(ExperimentalComposeUiApi::class)
fun main() {
    initCoil()
    CanvasBasedWindow {
        App(platformModule = module {
            single<ObservableSettings> {
                @OptIn(ExperimentalSettingsApi::class)
                StorageSettings().makeObservable()
            }
            single<NotificationService> { ServiceWorkerNotificationService(get()) }
        })
    }
}
