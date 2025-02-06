package org.jetbrains.kotlinconf.storage

import com.russhwolf.settings.ExperimentalSettingsApi
import com.russhwolf.settings.ObservableSettings
import com.russhwolf.settings.PropertiesSettings
import com.russhwolf.settings.observable.makeObservable
import org.jetbrains.kotlinconf.ApplicationContext
import java.io.File
import java.util.Properties

private val propsFile = File("store.properties")

@OptIn(ExperimentalSettingsApi::class)
actual fun createSettings(context: ApplicationContext): ObservableSettings {
    val props = try {
        propsFile.inputStream().use { Properties().apply { load(it) } }
    } catch (_: Exception) {
        Properties()
    }

    return PropertiesSettings(
        delegate = props,
        onModify = { props ->
            propsFile.bufferedWriter().use { writer ->
                props.store(writer, null)
            }
        }
    ).makeObservable()
}
