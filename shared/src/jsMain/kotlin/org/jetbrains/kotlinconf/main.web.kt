package org.jetbrains.kotlinconf

import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.window.CanvasBasedWindow
import com.russhwolf.settings.ExperimentalSettingsApi
import com.russhwolf.settings.ObservableSettings
import com.russhwolf.settings.StorageSettings
import com.russhwolf.settings.observable.makeObservable
import org.jetbrains.skiko.wasm.onWasmReady
import org.koin.dsl.module

private val platformModule = module {
    single<ObservableSettings> {
        @OptIn(ExperimentalSettingsApi::class)
        StorageSettings().makeObservable()
    }
    single<NotificationService> { ServiceWorkerNotificationService(get()) }
}

@OptIn(ExperimentalComposeUiApi::class)
fun main() {
    onWasmReady {
        CanvasBasedWindow {
            App(platformModule)
        }
    }
}
