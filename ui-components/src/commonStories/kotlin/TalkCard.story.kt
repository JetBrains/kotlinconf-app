import org.jetbrains.compose.storytale.story
import org.jetbrains.kotlinconf.ui.components.TalkCard
import org.jetbrains.kotlinconf.ui.components.TalkStatus
import org.jetbrains.kotlinconf.ui.components.Emotion
import org.jetbrains.kotlinconf.ui.theme.KotlinConfTheme
import androidx.compose.ui.Modifier

val `Upcoming Talk Card` by story {
    KotlinConfTheme {
        var bookmarked = false
        TalkCard(
            title = "Introduction to Kotlin Multiplatform",
            titleHighlights = emptyList(),
            bookmarked = bookmarked,
            onBookmark = { bookmarked = it },
            tags = setOf("Regular talk", "Beginner", "Multiplatform"),
            tagHighlights = emptyList(),
            speakers = "John Doe",
            speakerHighlights = emptyList(),
            location = "Main Hall",
            lightning = false,
            time = "May 23, 10:00 - 10:40",
            timeNote = null,
            status = TalkStatus.Upcoming,
            initialEmotion = null,
            onSubmitFeedback = {},
            onRequestFeedbackWithComment = null,
            onSubmitFeedbackWithComment = { _, _ -> },
            onClick = {},
            feedbackEnabled = false,
            userSignedIn = true
        )
    }
}

val `Live Talk Card` by story {
    KotlinConfTheme {
        var bookmarked = true
        TalkCard(
            title = "Advanced Kotlin Coroutines",
            titleHighlights = emptyList(),
            bookmarked = bookmarked,
            onBookmark = { bookmarked = it },
            tags = setOf("Regular talk", "Advanced", "Concurrency"),
            tagHighlights = emptyList(),
            speakers = "Jane Smith",
            speakerHighlights = emptyList(),
            location = "Room A",
            lightning = false,
            time = "May 23, 11:00 - 11:40",
            timeNote = "Live now",
            status = TalkStatus.Live,
            initialEmotion = null,
            onSubmitFeedback = {},
            onRequestFeedbackWithComment = null,
            onSubmitFeedbackWithComment = { _, _ -> },
            onClick = {},
            feedbackEnabled = false,
            userSignedIn = true
        )
    }
}

val `Past Talk Card` by story {
    KotlinConfTheme {
        var bookmarked = false
        TalkCard(
            title = "Kotlin for Server-side Development",
            titleHighlights = emptyList(),
            bookmarked = bookmarked,
            onBookmark = { bookmarked = it },
            tags = setOf("Regular talk", "Intermediate", "Server-side"),
            tagHighlights = emptyList(),
            speakers = "Alex Johnson",
            speakerHighlights = emptyList(),
            location = "Room B",
            lightning = false,
            time = "May 22, 14:00 - 14:40",
            timeNote = null,
            status = TalkStatus.Past,
            initialEmotion = null,
            onSubmitFeedback = {},
            onRequestFeedbackWithComment = { },
            onSubmitFeedbackWithComment = { _, _ -> },
            onClick = {},
            feedbackEnabled = true,
            userSignedIn = true
        )
    }
}

val `Lightning Talk Card` by story {
    KotlinConfTheme {
        var bookmarked = false
        TalkCard(
            title = "Quick Tips for Kotlin Development",
            titleHighlights = emptyList(),
            bookmarked = bookmarked,
            onBookmark = { bookmarked = it },
            tags = setOf("Lightning talk", "All levels", "Tips & Tricks"),
            tagHighlights = emptyList(),
            speakers = "Sarah Williams",
            speakerHighlights = emptyList(),
            location = "Lightning Stage",
            lightning = true,
            time = "May 23, 13:00 - 13:20",
            timeNote = null,
            status = TalkStatus.Upcoming,
            initialEmotion = null,
            onSubmitFeedback = {},
            onRequestFeedbackWithComment = null,
            onSubmitFeedbackWithComment = { _, _ -> },
            onClick = {},
            feedbackEnabled = false,
            userSignedIn = true
        )
    }
}