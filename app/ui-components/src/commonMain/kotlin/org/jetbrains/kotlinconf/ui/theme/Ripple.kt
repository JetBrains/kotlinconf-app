package org.jetbrains.kotlinconf.ui.theme

import androidx.compose.foundation.IndicationNodeFactory
import androidx.compose.foundation.interaction.InteractionSource
import androidx.compose.material.ripple.RippleAlpha
import androidx.compose.material.ripple.createRippleModifierNode
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.node.DelegatableNode
import androidx.compose.ui.unit.Dp

@Composable
fun rememberRippleIndication(): IndicationNodeFactory = remember { RippleNodeFactory() }

internal class RippleNodeFactory : IndicationNodeFactory {
    override fun create(interactionSource: InteractionSource): DelegatableNode {
        return createRippleModifierNode(
            interactionSource = interactionSource,
            bounded = true,
            radius = Dp.Unspecified,
            color = { Color.Unspecified },
            rippleAlpha = {
                RippleAlpha(
                    draggedAlpha = 0.16f,
                    focusedAlpha = 0.1f,
                    hoveredAlpha = 0.08f,
                    pressedAlpha = 0.1f,
                )
            }
        )
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false
        return true
    }

    override fun hashCode(): Int = this::class.hashCode()
}
