import org.jetbrains.compose.storytale.story
import org.jetbrains.kotlinconf.ui.components.PageTitle
import org.jetbrains.kotlinconf.ui.theme.KotlinConfTheme
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue

val `Regular Talk PageTitle` by story {
    KotlinConfTheme {
        var bookmarked by remember { mutableStateOf(false) }
        PageTitle(
            time = "May 21, 9:00 - 9:40",
            title = "A Wonderful Server-side Kotlin Talk",
            tags = setOf("Regular talk", "Beginner", "Server-side"),
            bookmarked = bookmarked,
            lightning = false,
            onBookmark = { bookmarked = it }
        )
    }
}

val `Lightning Talk PageTitle` by story {
    KotlinConfTheme {
        var bookmarked by remember { mutableStateOf(false) }
        PageTitle(
            time = "May 23, 13:00 - 13:20",
            title = "My Incredible Talk About Kotlin Multiplatform",
            tags = setOf("Lightning talk", "Intermediate", "Libraries"),
            bookmarked = bookmarked,
            lightning = true,
            onBookmark = { bookmarked = it }
        )
    }
}

val `Bookmarked PageTitle` by story {
    KotlinConfTheme {
        var bookmarked by remember { mutableStateOf(true) }
        PageTitle(
            time = "May 22, 11:00 - 11:40",
            title = "Advanced Kotlin Coroutines",
            tags = setOf("Regular talk", "Advanced", "Concurrency"),
            bookmarked = bookmarked,
            lightning = false,
            onBookmark = { bookmarked = it }
        )
    }
}

val `Education PageTitle` by story {
    KotlinConfTheme {
        var bookmarked by remember { mutableStateOf(false) }
        PageTitle(
            time = "May 22, 14:00 - 15:30",
            title = "Introduction to Kotlin Multiplatform",
            tags = setOf("Education", "Beginner", "Multiplatform"),
            bookmarked = bookmarked,
            lightning = false,
            onBookmark = { bookmarked = it }
        )
    }
}

val `Codelab PageTitle` by story {
    KotlinConfTheme {
        var bookmarked by remember { mutableStateOf(false) }
        PageTitle(
            time = "May 23, 10:00 - 11:30",
            title = "Building Your First Compose Multiplatform App",
            tags = setOf("Codelab", "Intermediate", "Compose"),
            bookmarked = bookmarked,
            lightning = false,
            onBookmark = { bookmarked = it }
        )
    }
}

val `PageTitle with Parameters` by story {
    val title by parameter("Customizable Title")
    val time by parameter("May 22, 10:00 - 10:40")
    val isLightning by parameter(false)
    val initialBookmarked by parameter(false)
    
    KotlinConfTheme {
        var bookmarked by remember { mutableStateOf(initialBookmarked) }
        PageTitle(
            time = time,
            title = title,
            tags = setOf("Regular talk", "Intermediate", "Kotlin"),
            bookmarked = bookmarked,
            lightning = isLightning,
            onBookmark = { bookmarked = it }
        )
    }
}