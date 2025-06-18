import org.jetbrains.compose.storytale.story
import org.jetbrains.kotlinconf.ui.components.SectionTitle
import org.jetbrains.kotlinconf.ui.components.ParagraphTitle
import org.jetbrains.kotlinconf.ui.theme.KotlinConfTheme
import androidx.compose.ui.Modifier

val `Section Title` by story {
    KotlinConfTheme {
        SectionTitle(
            text = "Section Title Example"
        )
    }
}

val `Section Title with Long Text` by story {
    KotlinConfTheme {
        SectionTitle(
            text = "This is a very long section title that might wrap to multiple lines depending on the available width"
        )
    }
}

val `Paragraph Title` by story {
    KotlinConfTheme {
        ParagraphTitle(
            text = "Paragraph Title Example"
        )
    }
}

val `Paragraph Title with Long Text` by story {
    KotlinConfTheme {
        ParagraphTitle(
            text = "This is a very long paragraph title that might wrap to multiple lines depending on the available width"
        )
    }
}

val `Titles with Parameters` by story {
    val sectionText by parameter("Customizable Section Title")
    val paragraphText by parameter("Customizable Paragraph Title")
    
    KotlinConfTheme {
        SectionTitle(
            text = sectionText
        )
        ParagraphTitle(
            text = paragraphText
        )
    }
}