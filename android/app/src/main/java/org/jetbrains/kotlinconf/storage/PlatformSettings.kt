package org.jetbrains.kotlinconf.storage

import android.content.*
import android.preference.*

actual class PlatformSettings constructor(context: Context) : Settings {

    private val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)

    actual override fun putBoolean(key: String, value: Boolean) {
        sharedPreferences.edit().putBoolean(key, value).apply()
    }

    actual override fun getBoolean(key: String, defaultValue: Boolean): Boolean =
        sharedPreferences.getBoolean(key, defaultValue)

    actual override fun putString(key: String, value: String) {
        sharedPreferences.edit().putString(key, value).apply()
    }

    actual override fun getString(key: String, defaultValue: String): String =
        sharedPreferences.getString(key, defaultValue)
}