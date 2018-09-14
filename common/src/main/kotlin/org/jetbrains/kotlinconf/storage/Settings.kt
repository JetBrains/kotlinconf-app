package org.jetbrains.kotlinconf.storage

expect interface Settings {
    fun putString(key: String, value: String)
    fun getString(key: String, defaultValue: String): String
    fun remove(key: String)
}

expect open class SettingsFactory {
    open fun create(name: String? = null): Settings
}