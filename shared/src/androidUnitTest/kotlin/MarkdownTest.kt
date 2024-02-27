import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import org.jetbrains.kotlinconf.ui.MarkdownStyle
import org.jetbrains.kotlinconf.ui.appendMarkdownLink
import org.jetbrains.kotlinconf.ui.markdownString
import org.junit.Test
import kotlin.test.assertEquals

class MarkdownTest {

    @Test
    fun testEmpty() {
        assertEquals(AnnotatedString(""), markdownString(""))

    }

    @Test
    fun testLink() {
        assertEquals(
            buildAnnotatedString {
                append("Hello, ")
                appendMarkdownLink("KotlinConf", "https://kotlinconf.com", MarkdownStyle().linkStyle)
                append("!")
            },
            markdownString("Hello, [KotlinConf](https://kotlinconf.com)!")
        )
    }

    @Test
    fun testUnderlined() {
        assertEquals(
            buildAnnotatedString {
                append("Hello, ")
                withStyle(style = SpanStyle(textDecoration = TextDecoration.Underline)) {
                    append("Badge scanning")
                }
                append("!")
            },
            markdownString("Hello, <u>Badge scanning</u>!")
        )
    }

    @Test
    fun testLineBreak() {
        assertEquals(
            buildAnnotatedString {
                append("Hello,")
                append("\n")
                append("World!")
            },
            markdownString("Hello,  \nWorld!")
        )
    }
}

