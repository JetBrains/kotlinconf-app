package org.jetbrains.kotlinconf.storage

expect interface Settings {
    fun putString(key: String, value: String)
    fun getString(key: String, defaultValue: String): String
    fun remove(key: String)
}

expect class SettingsFactory {
    open fun create(name: String? = null): Settings
}