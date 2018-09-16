package org.jetbrains.kotlinconf.storage

import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager

actual class PlatformSettings constructor(private val context: Context) : Settings {
    private val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)

    actual override fun putString(key: String, value: String): Unit =
            sharedPreferences.edit().putString(key, value).apply()

    actual override fun getString(key: String, defaultValue: String): String =
            sharedPreferences.getString(key, defaultValue)
}