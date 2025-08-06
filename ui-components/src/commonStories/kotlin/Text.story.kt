import org.jetbrains.compose.storytale.story
import org.jetbrains.kotlinconf.ui.components.Text
import org.jetbrains.kotlinconf.ui.theme.KotlinConfTheme
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle

val `Default Text` by story {
    KotlinConfTheme {
        Text(
            text = "This is a default text"
        )
    }
}

val `Colored Text` by story {
    KotlinConfTheme {
        Text(
            text = "This text has a custom color",
            color = KotlinConfTheme.colors.primaryBackground
        )
    }
}

val `Selectable Text` by story {
    KotlinConfTheme {
        Text(
            text = "This text is selectable, try selecting it!",
            selectable = true
        )
    }
}

val `Text with Max Lines` by story {
    KotlinConfTheme {
        Text(
            text = "This text has a maximum of 1 line and will be truncated if it exceeds that limit. This sentence is intentionally long to demonstrate the truncation.",
            maxLines = 1
        )
    }
}

val `Text with Different Styles` by story {
    KotlinConfTheme {
        Text(
            text = "This text uses the h1 style",
            style = KotlinConfTheme.typography.h1
        )
        Text(
            text = "This text uses the h2 style",
            style = KotlinConfTheme.typography.h2
        )
        Text(
            text = "This text uses the h3 style",
            style = KotlinConfTheme.typography.h3
        )
        Text(
            text = "This text uses the h4 style",
            style = KotlinConfTheme.typography.h4
        )
        Text(
            text = "This text uses the text1 style (default)",
            style = KotlinConfTheme.typography.text1
        )
        Text(
            text = "This text uses the text2 style",
            style = KotlinConfTheme.typography.text2
        )
    }
}

val `Annotated Text` by story {
    KotlinConfTheme {
        val annotatedString = buildAnnotatedString {
            append("This text has ")
            withStyle(style = SpanStyle(fontWeight = FontWeight.Bold, color = KotlinConfTheme.colors.primaryBackground)) {
                append("different styles")
            }
            append(" within it")
        }

        Text(
            text = annotatedString
        )
    }
}

val `Text with Parameters` by story {
    val text by parameter("Customizable text")
    val selectable by parameter(false)
    val maxLines by parameter(Int.MAX_VALUE)

    KotlinConfTheme {
        Text(
            text = text,
            selectable = selectable,
            maxLines = maxLines
        )
    }
}
