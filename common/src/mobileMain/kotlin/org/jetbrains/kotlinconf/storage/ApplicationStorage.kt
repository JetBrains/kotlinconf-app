package org.jetbrains.kotlinconf.storage

import kotlinx.coroutines.*
import kotlinx.coroutines.channels.*
import kotlinx.coroutines.flow.*
import kotlinx.serialization.*
import kotlinx.serialization.json.*
import org.jetbrains.kotlinconf.*
import kotlin.properties.*
import kotlin.reflect.*

expect class ApplicationContext

@Suppress("NO_ACTUAL_FOR_EXPECT")
expect fun ApplicationStorage(context: ApplicationContext): ApplicationStorage

interface ApplicationStorage {
    fun putBoolean(key: String, value: Boolean)
    fun getBoolean(key: String, defaultValue: Boolean): Boolean
    fun putString(key: String, value: String)
    fun getString(key: String): String?
}

@UseExperimental(UnstableDefault::class)
inline operator fun <reified T> ApplicationStorage.invoke(
    serializer: KSerializer<T>,
    crossinline block: () -> T
): ReadWriteProperty<ConferenceService, T> = object : ReadWriteProperty<ConferenceService, T> {
    private var currentValue: T? = null

    override fun setValue(thisRef: ConferenceService, property: KProperty<*>, value: T) {
        val key = property.name
        currentValue = value
        putString(key, Json.stringify(serializer, value))
    }

    override fun getValue(thisRef: ConferenceService, property: KProperty<*>): T {
        currentValue?.let { return it }

        val key = property.name

        val result = try {
            getString(key)?.let { Json.parse(serializer, it) }
        } catch (cause: Throwable) {
            null
        } ?: block()

        setValue(thisRef, property, result)
        return result
    }
}

@UseExperimental(ImplicitReflectionSerializer::class, ExperimentalStdlibApi::class, ExperimentalCoroutinesApi::class)
inline fun <reified T> ApplicationStorage.live(
    crossinline initial: () -> T
): ReadOnlyProperty<ConferenceService, ConflatedBroadcastChannel<T>> {
    val serializer = serializer<T>()

    return object : ReadOnlyProperty<ConferenceService, ConflatedBroadcastChannel<T>> {
        private var channel: ConflatedBroadcastChannel<T>? = null
        private var key: String? = null

        override fun getValue(thisRef: ConferenceService, property: KProperty<*>): ConflatedBroadcastChannel<T> {
            if (channel == null) {
                key = property.name
                val value = try {
                    getString(key!!)?.let { Json.parse(serializer, it) }
                } catch (_: Throwable) {
                    null
                } ?: initial()

                channel = ConflatedBroadcastChannel(value)
                channel!!.asFlow().wrap().watch {
                    putString(key!!, Json.stringify(serializer, it))
                }
            }

            return channel!!
        }
    }
}
