package org.jetbrains.kotlinconf.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp

class Shapes(
    val roundedCornerMd: RoundedCornerShape,
)

internal val KotlinConfShapes: Shapes
    @Composable
    get() = Shapes(
        roundedCornerMd = RoundedCornerShape(16.dp),
    )
