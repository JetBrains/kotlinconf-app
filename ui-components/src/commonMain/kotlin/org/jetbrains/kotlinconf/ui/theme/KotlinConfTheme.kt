package org.jetbrains.kotlinconf.ui.theme

import androidx.compose.foundation.IndicationNodeFactory
import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.interaction.InteractionSource
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ProvidedValue
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.drawscope.ContentDrawScope
import androidx.compose.ui.node.DelegatableNode
import androidx.compose.ui.node.DrawModifierNode

private object NoIndication : IndicationNodeFactory {
    override fun create(interactionSource: InteractionSource): DelegatableNode = object: DelegatableNode, DrawModifierNode, Modifier.Node() {
        override fun ContentDrawScope.draw() {
            drawContent()
        }
    }
    override fun equals(other: Any?): Boolean = other === NoIndication
    override fun hashCode(): Int = 0
}

val LocalColors = compositionLocalOf<Colors> {
    error("KotlinConfTheme must be part of the call hierarchy to provide colors")
}

val LocalTypography = compositionLocalOf<Typography> {
    error("KotlinConfTheme must be part of the call hierarchy to provide typography")
}

object KotlinConfTheme {
    val colors: Colors
        @Composable
        @ReadOnlyComposable
        get() = LocalColors.current

    val typography: Typography
        @Composable
        @ReadOnlyComposable
        get() = LocalTypography.current
}

// Exposes custom theme value to Compose resources, https://youtrack.jetbrains.com/issue/CMP-4197
expect object LocalAppTheme {
    val current: Boolean @Composable get
    @Composable
    infix fun provides(value: Boolean?): ProvidedValue<*>
}

@Composable
fun KotlinConfTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    rippleEnabled: Boolean = true,
    content: @Composable () -> Unit,
) {
    CompositionLocalProvider(
        LocalColors provides if (darkTheme) KotlinConfDarkColors else KotlinConfLightColors,
        LocalTypography provides KotlinConfTypography,
        LocalIndication provides if (rippleEnabled) rememberRippleIndication() else NoIndication,
        LocalAppTheme provides darkTheme,
    ) {
        content()
    }
}
