package org.jetbrains.kotlinconf.storage

expect class PlatformSettings : Settings {
    override fun putBoolean(key: String, value: Boolean)
    override fun getBoolean(key: String, defaultValue: Boolean): Boolean
    override fun putString(key: String, value: String)
    override fun getString(key: String, defaultValue: String): String
}

interface Settings {
    fun putBoolean(key: String, value: Boolean)
    fun getBoolean(key: String, defaultValue: Boolean): Boolean
    fun putString(key: String, value: String)
    fun getString(key: String, defaultValue: String = ""): String
}