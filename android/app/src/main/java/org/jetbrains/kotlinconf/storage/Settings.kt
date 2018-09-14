package org.jetbrains.kotlinconf.storage

import com.russhwolf.settings.PlatformSettings

actual typealias Settings = com.russhwolf.settings.Settings
actual open typealias SettingsFactory = PlatformSettings.Factory