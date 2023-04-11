package org.jetbrains.kotlinconf.storage

actual class ApplicationContext

actual fun ApplicationStorage(
    context: ApplicationContext
): ApplicationStorage = object : ApplicationStorage {
    private var booleans = mutableMapOf<String, Boolean>()
    private var strings = mutableMapOf<String, String>()

    override fun putBoolean(key: String, value: Boolean) {
        booleans[key] = value
    }

    override fun getBoolean(key: String, defaultValue: Boolean): Boolean {
        return booleans[key] ?: defaultValue
    }

    override fun putString(key: String, value: String) {
        strings[key] = value
    }

    override fun getString(key: String): String? = strings[key]
}