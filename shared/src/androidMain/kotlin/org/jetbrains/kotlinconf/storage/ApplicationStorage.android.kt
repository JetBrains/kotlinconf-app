package org.jetbrains.kotlinconf.storage

import android.content.Context
import androidx.preference.PreferenceManager
import org.jetbrains.kotlinconf.ApplicationContext

actual fun ApplicationStorage(context: ApplicationContext): ApplicationStorage {
    return AndroidStorage(context.application)
}

internal class AndroidStorage(
    context: Context
) : ApplicationStorage {
    private val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)

    override fun putBoolean(key: String, value: Boolean) {
        sharedPreferences.edit().putBoolean(key, value).apply()
    }

    override fun getBoolean(key: String, defaultValue: Boolean): Boolean =
        sharedPreferences.getBoolean(key, defaultValue)

    override fun putString(key: String, value: String) {
        sharedPreferences.edit().putString(key, value).apply()
    }

    override fun getString(key: String): String? {
        return sharedPreferences.getString(key, null)
    }
}
