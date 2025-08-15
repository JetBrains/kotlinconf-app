import org.jetbrains.compose.storytale.story
import org.jetbrains.kotlinconf.ui.components.Button
import org.jetbrains.kotlinconf.ui.theme.KotlinConfTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

val `Primary Button` by story {
    KotlinConfTheme {
        Button(
            label = "Primary Button",
            onClick = {},
            primary = true
        )
    }
}

val `Secondary Button` by story {
    KotlinConfTheme {
        Button(
            label = "Secondary Button",
            onClick = {},
            primary = false
        )
    }
}

val `Disabled Button` by story {
    val enabled by parameter(false)
    
    KotlinConfTheme {
        Button(
            label = "Disabled Button",
            onClick = {},
            enabled = enabled
        )
    }
}

val `Button with custom label` by story {
    val label by parameter("Custom Label")
    
    KotlinConfTheme {
        Button(
            label = label,
            onClick = {}
        )
    }
}