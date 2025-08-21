import org.jetbrains.compose.storytale.story
import org.jetbrains.kotlinconf.ui.components.Toggle
import org.jetbrains.kotlinconf.ui.theme.KotlinConfTheme
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue

val `Toggle Off` by story {
    KotlinConfTheme {
        var enabled by remember { mutableStateOf(false) }
        Toggle(
            enabled = enabled,
            onToggle = { enabled = it }
        )
    }
}

val `Toggle On` by story {
    KotlinConfTheme {
        var enabled by remember { mutableStateOf(true) }
        Toggle(
            enabled = enabled,
            onToggle = { enabled = it }
        )
    }
}

val `Toggle with Parameter` by story {
    val initialState by parameter(false)
    
    KotlinConfTheme {
        var enabled by remember { mutableStateOf(initialState) }
        Toggle(
            enabled = enabled,
            onToggle = { enabled = it }
        )
    }
}