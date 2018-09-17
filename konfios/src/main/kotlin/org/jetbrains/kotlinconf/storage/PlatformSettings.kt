package org.jetbrains.kotlinconf.storage

import platform.Foundation.NSUserDefaults
import platform.Foundation.NSBundle

actual class PlatformSettings public constructor() : Settings {
    private val delegate: NSUserDefaults = NSUserDefaults.standardUserDefaults()

    actual override fun putString(key: String, value: String) {
        delegate.setObject(value, key)
    }

    actual override fun getString(key: String, defaultValue: String): String =
            delegate.stringForKey(key) ?: defaultValue

    actual override fun putBoolean(key: String, value: Boolean) {
        delegate.setBool(value, key)
    }

    actual override fun getBoolean(key: String, defaultValue: Boolean): Boolean =
            if (hasKey(key)) delegate.boolForKey(key) else defaultValue

    fun hasKey(key: String): Boolean = delegate.objectForKey(key) != null
}