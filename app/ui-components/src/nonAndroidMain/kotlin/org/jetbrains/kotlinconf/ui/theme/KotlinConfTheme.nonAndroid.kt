package org.jetbrains.kotlinconf.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ProvidedValue
import androidx.compose.ui.InternalComposeUiApi
import androidx.compose.ui.LocalSystemTheme
import androidx.compose.ui.SystemTheme


@OptIn(InternalComposeUiApi::class)
actual object LocalAppTheme {
    private var default: SystemTheme? = null
    actual val current: Boolean
        @Composable get() = LocalSystemTheme.current == SystemTheme.Dark

    @Composable
    actual infix fun provides(value: Boolean?): ProvidedValue<*> {
        if (default == null) {
            default = LocalSystemTheme.current
        }
        val new = when(value) {
            true -> SystemTheme.Dark
            false -> SystemTheme.Light
            null -> default!!
        }
        return LocalSystemTheme.provides(new)
    }
}
