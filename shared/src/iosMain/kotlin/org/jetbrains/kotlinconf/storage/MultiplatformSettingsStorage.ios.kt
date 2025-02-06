package org.jetbrains.kotlinconf.storage

import com.russhwolf.settings.NSUserDefaultsSettings
import com.russhwolf.settings.ObservableSettings
import org.jetbrains.kotlinconf.ApplicationContext
import platform.Foundation.NSUserDefaults

actual fun createSettings(context: ApplicationContext): ObservableSettings =
    NSUserDefaultsSettings(NSUserDefaults.standardUserDefaults)
