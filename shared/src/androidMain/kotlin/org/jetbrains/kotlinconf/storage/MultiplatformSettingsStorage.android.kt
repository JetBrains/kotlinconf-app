package org.jetbrains.kotlinconf.storage

import androidx.preference.PreferenceManager
import com.russhwolf.settings.ObservableSettings
import com.russhwolf.settings.SharedPreferencesSettings
import org.jetbrains.kotlinconf.ApplicationContext

actual fun createSettings(context: ApplicationContext): ObservableSettings =
    SharedPreferencesSettings(PreferenceManager.getDefaultSharedPreferences(context.application))
