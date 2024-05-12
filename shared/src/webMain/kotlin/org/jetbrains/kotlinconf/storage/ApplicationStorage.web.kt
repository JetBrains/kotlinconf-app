package org.jetbrains.kotlinconf.storage

import kotlinx.browser.localStorage
import org.jetbrains.kotlinconf.ApplicationContext

actual fun ApplicationStorage(context: ApplicationContext): ApplicationStorage {
    return AndroidStorage()
}

internal class AndroidStorage : ApplicationStorage {
    override fun putBoolean(key: String, value: Boolean) {
        localStorage.setItem(key, value.toString())
    }

    override fun getBoolean(key: String, defaultValue: Boolean): Boolean =
        localStorage.getItem(key)?.toBoolean() ?: defaultValue

    override fun putString(key: String, value: String) {
        localStorage.setItem(key, value)
    }

    override fun getString(key: String): String? = localStorage.getItem(key)
}
