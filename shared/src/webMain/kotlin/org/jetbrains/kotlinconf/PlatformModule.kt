package org.jetbrains.kotlinconf

import com.russhwolf.settings.ExperimentalSettingsApi
import com.russhwolf.settings.ObservableSettings
import com.russhwolf.settings.StorageSettings
import com.russhwolf.settings.observable.makeObservable
import org.koin.dsl.module

val platformModule = module {
    single<ObservableSettings> {
        @OptIn(ExperimentalSettingsApi::class)
        StorageSettings().makeObservable()
    }
    single<NotificationService> { ServiceWorkerNotificationService(get()) }
}
