package org.jetbrains.kotlinconf.storage

import org.jetbrains.kotlinconf.ApplicationContext

actual fun ApplicationStorage(context: ApplicationContext): ApplicationStorage = object : ApplicationStorage {
    override fun putBoolean(key: String, value: Boolean) {
    }

    override fun getBoolean(key: String, defaultValue: Boolean): Boolean {
        return defaultValue
    }

    override fun putString(key: String, value: String) {
    }

    override fun getString(key: String): String? {
        return null
    }
}