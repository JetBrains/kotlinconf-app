import org.jetbrains.compose.storytale.story
import org.jetbrains.kotlinconf.ui.components.NewsCard
import org.jetbrains.kotlinconf.ui.theme.KotlinConfTheme
import androidx.compose.ui.Modifier

val `News Card with Photo` by story {
    KotlinConfTheme {
        NewsCard(
            title = "Kotlin 2.0 Released",
            date = "May 23, 2023",
            photoUrl = "https://kotlinlang.org/assets/images/open-graph/kotlin-logo.png",
            onClick = {}
        )
    }
}

val `News Card without Photo` by story {
    KotlinConfTheme {
        NewsCard(
            title = "Important Conference Update",
            date = "May 22, 2023",
            photoUrl = null,
            onClick = {}
        )
    }
}

val `News Card with Long Title` by story {
    KotlinConfTheme {
        NewsCard(
            title = "This is a very long news title that might wrap to multiple lines depending on the available width",
            date = "May 21, 2023",
            photoUrl = "https://kotlinlang.org/assets/images/open-graph/kotlin-logo.png",
            onClick = {}
        )
    }
}

val `News Card with Parameters` by story {
    val title by parameter("Customizable News Title")
    val date by parameter("May 20, 2023")
    val hasPhoto by parameter(true)
    
    KotlinConfTheme {
        NewsCard(
            title = title,
            date = date,
            photoUrl = if (hasPhoto) "https://kotlinlang.org/assets/images/open-graph/kotlin-logo.png" else null,
            onClick = {}
        )
    }
}