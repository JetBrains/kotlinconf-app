import org.jetbrains.compose.storytale.story
import org.jetbrains.kotlinconf.ui.components.NowButton
import org.jetbrains.kotlinconf.ui.components.NowLabel
import org.jetbrains.kotlinconf.ui.components.NowButtonState
import org.jetbrains.kotlinconf.ui.theme.KotlinConfTheme
import androidx.compose.ui.Modifier

val `Now Label` by story {
    KotlinConfTheme {
        NowLabel()
    }
}

val `Now Button - Before` by story {
    KotlinConfTheme {
        NowButton(
            time = NowButtonState.Before,
            onClick = {}
        )
    }
}

val `Now Button - Current` by story {
    KotlinConfTheme {
        NowButton(
            time = NowButtonState.Current,
            onClick = {}
        )
    }
}

val `Now Button - After` by story {
    KotlinConfTheme {
        NowButton(
            time = NowButtonState.After,
            onClick = {}
        )
    }
}

val `Now Button with Parameters` by story {
    val state by parameter(NowButtonState.Before)
    val enabled by parameter(true)
    
    KotlinConfTheme {
        NowButton(
            time = state,
            onClick = {},
            enabled = enabled
        )
    }
}