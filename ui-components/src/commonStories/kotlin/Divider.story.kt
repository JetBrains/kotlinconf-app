import org.jetbrains.compose.storytale.story
import org.jetbrains.kotlinconf.ui.components.Divider
import org.jetbrains.kotlinconf.ui.theme.KotlinConfTheme
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

val `Default Divider` by story {
    KotlinConfTheme {
        Divider(
            thickness = 1.dp,
            color = KotlinConfTheme.colors.strokeHalf
        )
    }
}

val `Thick Divider` by story {
    KotlinConfTheme {
        Divider(
            thickness = 4.dp,
            color = KotlinConfTheme.colors.strokeHalf
        )
    }
}

val `Custom Color Divider` by story {
    KotlinConfTheme {
        Divider(
            thickness = 2.dp,
            color = KotlinConfTheme.colors.primaryBackground
        )
    }
}

val `Divider with Parameters` by story {
    val thickness by parameter(2)
    val useCustomColor by parameter(false)
    
    KotlinConfTheme {
        Divider(
            thickness = thickness.dp,
            color = if (useCustomColor) KotlinConfTheme.colors.primaryBackground else KotlinConfTheme.colors.strokeHalf
        )
    }
}
