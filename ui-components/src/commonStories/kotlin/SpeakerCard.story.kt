import org.jetbrains.compose.storytale.story
import org.jetbrains.kotlinconf.ui.components.SpeakerCard
import org.jetbrains.kotlinconf.ui.theme.KotlinConfTheme
import androidx.compose.ui.Modifier

val `Speaker Card` by story {
    KotlinConfTheme {
        SpeakerCard(
            name = "John Doe",
            title = "Software Engineer at Example Inc.",
            photoUrl = "https://kotlinlang.org/assets/images/open-graph/kotlin-logo.png",
            onClick = {}
        )
    }
}

val `Speaker Card with Highlights` by story {
    KotlinConfTheme {
        SpeakerCard(
            name = "Jane Smith",
            nameHighlights = listOf(0..3), // Highlight "Jane"
            title = "Kotlin Developer at JetBrains",
            titleHighlights = listOf(0..6), // Highlight "Kotlin"
            photoUrl = "https://kotlinlang.org/assets/images/open-graph/kotlin-logo.png",
            onClick = {}
        )
    }
}

val `Speaker Card with Long Title` by story {
    KotlinConfTheme {
        SpeakerCard(
            name = "Alexander Johnson",
            title = "Senior Software Engineer and Team Lead for Kotlin Multiplatform Development",
            photoUrl = "https://kotlinlang.org/assets/images/open-graph/kotlin-logo.png",
            onClick = {}
        )
    }
}

val `Speaker Card with Parameters` by story {
    val name by parameter("Speaker Name")
    val title by parameter("Speaker Title")
    val highlightName by parameter(false)

    KotlinConfTheme {
        SpeakerCard(
            name = name,
            nameHighlights = if (highlightName) listOf(0..4) else emptyList(), // Highlight first few chars if enabled
            title = title,
            photoUrl = "https://kotlinlang.org/assets/images/open-graph/kotlin-logo.png",
            onClick = {}
        )
    }
}
