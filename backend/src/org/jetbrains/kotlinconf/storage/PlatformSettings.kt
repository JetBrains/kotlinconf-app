package org.jetbrains.kotlinconf.storage

actual class PlatformSettings : Settings {
    actual override fun putString(key: String, value: String) {}
    actual override fun getString(key: String, defaultValue: String): String {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}