package org.jetbrains.kotlinconf.storage

import platform.Foundation.*

class PlatformSettings public constructor() : Settings {
    private val delegate: NSUserDefaults = NSUserDefaults.standardUserDefaults()

    override fun putString(key: String, value: String) {
        delegate.setObject(value, key)
    }

    override fun getString(key: String, defaultValue: String): String =
        delegate.stringForKey(key) ?: defaultValue

    override fun putBoolean(key: String, value: Boolean) {
        delegate.setBool(value, key)
    }

    override fun getBoolean(key: String, defaultValue: Boolean): Boolean =
        if (hasKey(key)) delegate.boolForKey(key) else defaultValue

    fun hasKey(key: String): Boolean = delegate.objectForKey(key) != null
}