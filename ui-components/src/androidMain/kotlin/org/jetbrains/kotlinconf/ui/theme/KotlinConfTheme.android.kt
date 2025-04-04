package org.jetbrains.kotlinconf.ui.theme

import android.content.res.Configuration
import android.content.res.Configuration.UI_MODE_NIGHT_MASK
import android.content.res.Configuration.UI_MODE_NIGHT_NO
import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ProvidedValue
import androidx.compose.ui.platform.LocalConfiguration

actual object LocalAppTheme {
    private var default: Int? = null
    actual val current: Boolean
        @Composable get() = (LocalConfiguration.current.uiMode and UI_MODE_NIGHT_MASK) == UI_MODE_NIGHT_YES

    @Composable
    actual infix fun provides(value: Boolean?): ProvidedValue<*> {
        val configuration = Configuration(LocalConfiguration.current)

        if (default == null) {
            default = configuration.uiMode
        }

        val new = when(value) {
            true -> (configuration.uiMode and UI_MODE_NIGHT_MASK.inv()) or UI_MODE_NIGHT_YES
            false -> (configuration.uiMode and UI_MODE_NIGHT_MASK.inv()) or UI_MODE_NIGHT_NO
            null -> default!!
        }
        configuration.uiMode = new
        return LocalConfiguration.provides(configuration)
    }
}
