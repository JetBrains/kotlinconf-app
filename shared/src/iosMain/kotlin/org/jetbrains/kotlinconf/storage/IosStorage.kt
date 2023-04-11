package org.jetbrains.kotlinconf.storage

import platform.Foundation.*

actual class ApplicationContext

actual fun ApplicationStorage(context: ApplicationContext): ApplicationStorage = IosStorage()

internal class IosStorage : ApplicationStorage {
    private val delegate: NSUserDefaults = NSUserDefaults.standardUserDefaults()

    override fun putBoolean(key: String, value: Boolean) {
        delegate.setBool(value, key)
    }

    override fun getBoolean(key: String, defaultValue: Boolean): Boolean =
        if (hasKey(key)) delegate.boolForKey(key) else defaultValue

    override fun putString(key: String, value: String) {
        delegate.setObject(value, key)
    }

    override fun getString(key: String): String? = delegate.stringForKey(key)

    private fun hasKey(key: String): Boolean = delegate.objectForKey(key) != null
}