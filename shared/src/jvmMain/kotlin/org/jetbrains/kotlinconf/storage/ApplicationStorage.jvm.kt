package org.jetbrains.kotlinconf.storage

import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.jetbrains.kotlinconf.ApplicationContext
import java.io.File

@Serializable
private data class ApplicationStorageData(val values: Map<String, String>)

@OptIn(FlowPreview::class, DelicateCoroutinesApi::class)
actual fun ApplicationStorage(context: ApplicationContext): ApplicationStorage =
    object : ApplicationStorage {
        private val data: MutableStateFlow<ApplicationStorageData>

        init {
            data = try {
                val content = File("store.json").readText()
                val storageData = Json.decodeFromString<ApplicationStorageData>(content)
                MutableStateFlow(storageData)
            } catch (cause: Throwable) {
                MutableStateFlow(ApplicationStorageData(emptyMap()))
            }

            GlobalScope.launch(Dispatchers.IO) {
                data.debounce(5_000)
                    .collect { storageData ->
                    val content = Json.encodeToString(storageData)
                    runCatching {
                        File("store.json").writeText(content)
                    }
                }
            }
        }

        override fun putBoolean(key: String, value: Boolean) {
            data.value = data.value.copy(values = data.value.values + (key to value.toString()))
        }

        override fun getBoolean(key: String, defaultValue: Boolean): Boolean {
            return data.value.values[key]?.toBoolean() ?: defaultValue
        }

        override fun putString(key: String, value: String) {
            data.value = data.value.copy(values = data.value.values + (key to value))
        }

        override fun getString(key: String): String? {
            return data.value.values[key]
        }
    }