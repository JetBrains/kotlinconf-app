import org.jetbrains.compose.storytale.story
import org.jetbrains.kotlinconf.ui.components.PartnerCard
import org.jetbrains.kotlinconf.ui.theme.KotlinConfTheme
import kotlinconfapp.ui_components.generated.resources.UiRes
import kotlinconfapp.ui_components.generated.resources.kodee_large_positive_light

val `Partner Card` by story {
    KotlinConfTheme {
        PartnerCard(
            name = "Kodee",
            logo = UiRes.drawable.kodee_large_positive_light,
            onClick = {}
        )
    }
}

val `Partner Card with Parameters` by story {
    val name by parameter("Partner Name")
    
    KotlinConfTheme {
        PartnerCard(
            name = name,
            logo = UiRes.drawable.kodee_large_positive_light,
            onClick = {}
        )
    }
}