package org.jetbrains.kotlinconf.storage

expect class PlatformSettings : Settings {
    override fun putString(key: String, value: String)
    override fun getString(key: String, defaultValue: String): String
}

interface Settings {
    fun putString(key: String, value: String)
    fun getString(key: String, defaultValue: String = ""): String
}