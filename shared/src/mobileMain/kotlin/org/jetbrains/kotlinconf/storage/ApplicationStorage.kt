package org.jetbrains.kotlinconf.storage

import kotlinx.serialization.KSerializer
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.jetbrains.kotlinconf.ApplicationContext
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

expect fun ApplicationStorage(context: ApplicationContext): ApplicationStorage

interface ApplicationStorage {
    fun putBoolean(key: String, value: Boolean)
    fun getBoolean(key: String, defaultValue: Boolean): Boolean
    fun putString(key: String, value: String)
    fun getString(key: String): String?
}

inline fun <reified T> ApplicationStorage.put(key: String, value: T) {
    putString(key, Json.encodeToString(value))
}

inline fun <reified T> ApplicationStorage.get(key: String): T? {
    val value = getString(key) ?: return null
    return runCatching {
        Json.decodeFromString<T>(value)
    }.getOrNull()
}

inline fun <reified T> ApplicationStorage.bind(
    serializer: KSerializer<T>,
    crossinline block: () -> T
): ReadWriteProperty<Any, T> = object : ReadWriteProperty<Any, T> {
    private var currentValue: T? = null

    override fun setValue(thisRef: Any, property: KProperty<*>, value: T) {
        val key = property.name
        currentValue = value
        putString(key, Json.encodeToString(serializer, value))
    }

    override fun getValue(thisRef: Any, property: KProperty<*>): T {
        currentValue?.let { return it }

        val key = property.name
        val value = getString(key)
        val result = runCatching {
            value?.let { Json.decodeFromString(serializer, it) }
        }.getOrNull() ?: block()

        setValue(thisRef, property, result)
        return result
    }
}

