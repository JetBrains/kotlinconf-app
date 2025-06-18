import org.jetbrains.compose.storytale.story
import org.jetbrains.kotlinconf.ui.components.CardTag
import org.jetbrains.kotlinconf.ui.theme.KotlinConfTheme
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue

val `Unselected CardTag` by story {
    KotlinConfTheme {
        CardTag(
            label = "Unselected Tag",
            selected = false
        )
    }
}

val `Selected CardTag` by story {
    KotlinConfTheme {
        CardTag(
            label = "Selected Tag",
            selected = true
        )
    }
}

val `Interactive CardTag` by story {
    KotlinConfTheme {
        var selected by remember { mutableStateOf(false) }
        CardTag(
            label = if (selected) "Selected Tag (click to unselect)" else "Unselected Tag (click to select)",
            selected = selected
        )
        // Note: CardTag doesn't have a click handler, so this is just for demonstration
    }
}

val `CardTag with Parameters` by story {
    val label by parameter("Customizable Tag")
    val selected by parameter(false)
    
    KotlinConfTheme {
        CardTag(
            label = label,
            selected = selected
        )
    }
}