package org.jetbrains.kotlinconf.storage

import com.russhwolf.settings.ExperimentalSettingsApi
import com.russhwolf.settings.ObservableSettings
import com.russhwolf.settings.StorageSettings
import com.russhwolf.settings.observable.makeObservable
import org.jetbrains.kotlinconf.ApplicationContext

@OptIn(ExperimentalSettingsApi::class)
actual fun createSettings(context: ApplicationContext): ObservableSettings {
    return StorageSettings().makeObservable()
}
